/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cormo.neulbeot.page.exercise.activity.pickme.fragment

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
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import com.cormo.neulbeot.page.exercise.activity.pickme.PickmeHandLandmarkerHelper
import com.cormo.neulbeot.page.exercise.activity.pickme.PickmeMainViewModel
import com.cormo.neulbeot.databinding.PickmeFragmentCameraBinding
import com.cormo.neulbeot.page.exercise.ExEndActivity
import com.cormo.neulbeot.R

import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PickmeCameraFragment : Fragment(), PickmeHandLandmarkerHelper.LandmarkerListener {

    companion object {
        private const val TAG = "HandGameOverlay"
    }

    private var _binding: PickmeFragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var handLandmarkerHelper: PickmeHandLandmarkerHelper
    private val viewModel: PickmeMainViewModel by activityViewModels()

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    private lateinit var backgroundExecutor: ExecutorService

    private var score: Int = 0
    private var gameTimer: CountDownTimer? = null
    private var timeLeft: Int = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PickmeFragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backgroundExecutor = Executors.newSingleThreadExecutor()

        val bmp = BitmapFactory.decodeResource(resources, R.drawable.target)
        binding.overlay.setTargetImage(bmp)

        binding.viewFinder.post {
            setUpCamera()
        }

        backgroundExecutor.execute {
            handLandmarkerHelper = PickmeHandLandmarkerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = viewModel.currentMinHandDetectionConfidence,
                minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
                minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
                maxNumHands = viewModel.currentMaxHands,
                currentDelegate = viewModel.currentDelegate,
                handLandmarkerHelperListener = this
            )
        }

        setUpGame()
    }

    private fun setUpGame() {

        binding.overlay.setOnHitListener {
            score++
        }

        binding.overlay.post {
            binding.overlay.startGame()
        }

        gameTimer?.cancel()
        gameTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft--
                // 시간 UI 쓰고 싶으면 여기서 사용
                binding.timeText.text = "Time: $timeLeft"

                // 점수 UI
                binding.gameScore.text = "Score: $score"

                // 시간 종료
                if(timeLeft == 0){
                    onFinish()
                }
            }

            override fun onFinish() {
                binding.overlay.stopGame()
                Toast.makeText(
                    requireContext(),
                    "Game Over! Score: $score",
                    Toast.LENGTH_SHORT
                ).show()

                parentFragmentManager.popBackStack()

                startActivity(Intent(context, ExEndActivity::class.java)
                    .putExtra("activity",score.toString() + "점"))
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameTimer?.cancel()
        _binding = null
        backgroundExecutor.shutdown()
    }

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
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

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
            .also {
                it.setAnalyzer(backgroundExecutor) { image ->
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

    private fun detectHand(imageProxy: ImageProxy) {
        handLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
        )
    }

    override fun onResults(resultBundle: PickmeHandLandmarkerHelper.ResultBundle) {
        activity?.runOnUiThread {
            val result = resultBundle.results.first()

            binding.overlay.setResults(
                result,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )
        }
    }

    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }
}
