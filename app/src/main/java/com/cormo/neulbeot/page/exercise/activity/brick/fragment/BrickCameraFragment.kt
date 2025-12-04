package com.cormo.neulbeot.page.exercise.activity.brick.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cormo.neulbeot.R
import com.cormo.neulbeot.databinding.BrickFragmentCameraBinding
import com.cormo.neulbeot.page.exercise.ExEndActivity
import com.cormo.neulbeot.page.exercise.activity.brick.BrickHandLandmarkerHelper
import com.cormo.neulbeot.page.exercise.activity.brick.BrickMainViewModel
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BrickCameraFragment : Fragment(), BrickHandLandmarkerHelper.LandmarkerListener {

    companion object {
        private const val TAG = "로그"
    }

    private var _binding: BrickFragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var handLandmarkerHelper: BrickHandLandmarkerHelper
    private val viewModel: BrickMainViewModel by activityViewModels()

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing: Int = CameraSelector.LENS_FACING_FRONT

    private lateinit var backgroundExecutor: ExecutorService

    private var score: Int = 0
    private var gameTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BrickFragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backgroundExecutor = Executors.newSingleThreadExecutor()

        // 타겟 이미지 세팅
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.target)
        binding.overlay.setTargetImage(bmp)

        // 로딩 오버레이 보여주기 (초기화 시작)
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.loadingText.text = "게임을 준비 중입니다..."

        // Landmarker 초기화 (백그라운드에서 초기화, 준비 안 됐으면 프레임은 스킵)
        backgroundExecutor.execute {
            try {
                handLandmarkerHelper = BrickHandLandmarkerHelper(
                    context = requireContext(),
                    runningMode = RunningMode.LIVE_STREAM,
                    minHandDetectionConfidence = viewModel.currentMinHandDetectionConfidence,
                    minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
                    minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
                    maxNumHands = viewModel.currentMaxHands,
                    currentDelegate = viewModel.currentDelegate,
                    handLandmarkerHelperListener = this
                )

                requireActivity().runOnUiThread {
                    setUpCamera() // 카메라 준비
                    // 로딩 UI 제거
                    binding.loadingOverlay.visibility = View.GONE
                    setUpGame() // 게임 시작 설정
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing HandLandmarkerHelper", e)
            }
        }
    }

    /** 게임 로직 / 타이머 설정 */
    private fun setUpGame() {
        var timeLeft = 60
        score = 0

        // 맞췄을 때 점수 증가
        binding.overlay.setOnHitListener {
            score++
        }

        // 오버레이 게임 시작
        binding.overlay.post {
            binding.overlay.startGame()
        }

        // 기존 타이머 있으면 취소
        gameTimer?.cancel()

        // 10초 타이머
        gameTimer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
                binding.timeText.text = "Time: $timeLeft"
                binding.gameScore.text = "Score: $score"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    /** 게임 종료 처리 (한 곳에서만 처리) */
    private fun endGame() {
        if (!isAdded || _binding == null) return

        binding.overlay.stopGame()

        Toast.makeText(
            requireContext(),
            "Game Over! Score: $score",
            Toast.LENGTH_SHORT
        ).show()

        val act = activity ?: return
        val intent = Intent(act, ExEndActivity::class.java)
            .putExtra("activity", "brick")
            .putExtra("score","${score}점")

        act.startActivity(intent)
        // 이전 액티비티도 종료하고 싶으면 주석 해제
        act.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameTimer?.cancel()
        cameraProvider?.unbindAll()
        backgroundExecutor.shutdown()
        preview = null
        imageAnalyzer = null
        camera = null
        cameraProvider = null
        _binding = null
    }

    /** CameraX 세팅 */
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        val provider = cameraProvider ?: return

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraFacing)
            .build()

        preview = Preview.Builder()
            .setTargetResolution(Size(640, 480))
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(backgroundExecutor) { image ->
                    detectHand(image)
                }
            }

        provider.unbindAll()

        try {
            camera = provider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }

    /** 손 인식 처리 */
    private fun detectHand(imageProxy: ImageProxy) {
        if (!::handLandmarkerHelper.isInitialized) {
            // 아직 초기화 전이면 프레임만 버리고 return
            imageProxy.close()
            return
        }

        try {
            handLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during hand detection", e)
            imageProxy.close()
        }
    }

    /** MediaPipe 결과 콜백 */
    override fun onResults(resultBundle: BrickHandLandmarkerHelper.ResultBundle) {
        val safeBinding = _binding ?: return
        if (!isAdded) return

        activity?.runOnUiThread {
            val result = resultBundle.results.first()

            safeBinding.overlay.setResults(
                result,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )
        }
    }

    /** MediaPipe 에러 콜백 */
    override fun onError(error: String, errorCode: Int) {
        if (!isAdded || _binding == null) return

        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }
}
