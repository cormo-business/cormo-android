package com.cormo.neulbeot.page.exercise.activity.squart_version

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.exercise.ExEndActivity

class SquartActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var overlayView: SquartOverlay
    private val analyzer = SquartAnalyzer()
    private val squartCounter = SquartCounter()

    private val ask =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
            if (ok) startCamera() else finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.squart_layout)

        previewView = findViewById(R.id.previewView)
        overlayView = findViewById(R.id.overlayView)

        val startDialog = findViewById<ConstraintLayout>(R.id.start_dialog)
        val btnStart = findViewById<TextView>(R.id.btn_start)
        val btnExit = findViewById<TextView>(R.id.btn_exit)

        btnExit.setOnClickListener {
            finish()
        }

        btnStart.setOnClickListener {
            startDialog.visibility = View.GONE
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) startCamera()
        else ask.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
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

                        analyzer.analyze(image, info) { result ->

                            // 스쿼트 카운팅
                            squartCounter.update(result.landmarks)

                            // OverlayView 업데이트
                            overlayView.update(
                                result,
                                info,
                                squartCounter.count
                            )

                            if(squartCounter.count == 10){
                                startActivity(Intent(this, ExEndActivity::class.java)
                                    .putExtra("activity","squart"))
                                finish()
                                squartCounter.reset()
                            }
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
