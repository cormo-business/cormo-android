package com.cormo.neulbeot.page.exercise.activity.jjuka_State_version

import com.cormo.neulbeot.R

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import android.view.Surface
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class JJukaActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var JJukaOverlay: JJukaOverlay
    private val JJukaAnalyzer = JJukaAnalyzer()

    // 스트레칭 상태 관리
    private val stretchState = StretchState()

    private val askCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
            if (ok) startCamera() else finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jjuka_layout)
        previewView = findViewById(R.id.previewView)
        JJukaOverlay = findViewById(R.id.overlayView)

        val startDialog = findViewById<ConstraintLayout>(R.id.start_dialog)
        val btnStart = findViewById<TextView>(R.id.btn_start)

        btnStart.setOnClickListener {
            startDialog.visibility = View.GONE
        }

        // 크롭 없이 원본 비율 맞춤
        previewView.scaleType = PreviewView.ScaleType.FIT_CENTER

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) startCamera()
        else askCamera.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val rotation = previewView.display?.rotation ?: Surface.ROTATION_0

            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val analysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { ia ->
                    ia.setAnalyzer(ContextCompat.getMainExecutor(this)) { image ->

                        val info = FrameInfo(
                            width = image.width,
                            height = image.height,
                            rotation = image.imageInfo.rotationDegrees,
                            isFront = true
                        )

                        JJukaAnalyzer.analyze(image, info) { result ->
                            // =========== 스트레칭 상태 업데이트 ===========
                            val phase = StretchEvaluator.evaluate(result.landmarks)
                            stretchState.updatePhase(phase, System.currentTimeMillis())

                            // 게이지 업데이트
                            JJukaOverlay.setStretchGauge(
                                stretchState.progress(),
                                stretchState.phase.name
                            )

                            // 랜드마크 업데이트
                            JJukaOverlay.update(result, info)
                        }
                    }
                }

            val front = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            provider.unbindAll()
            provider.bindToLifecycle(this, front, preview, analysis)
        }, ContextCompat.getMainExecutor(this))
    }
}
