package com.cormo.neulbeot.page.exercise.activity.squart_version

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions

data class Landmark(val x: Float, val y: Float)

data class DetectionResult(
    val landmarks: List<Landmark> = emptyList(),
    val info: String? = null
)

data class FrameInfo(
    val width: Int,
    val height: Int,
    val rotation: Int,
    val isFront: Boolean
)

class SquartAnalyzer {

    private val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()

    private val detector: PoseDetector = PoseDetection.getClient(options)

    @OptIn(ExperimentalGetImage::class)
    fun analyze(
        image: ImageProxy,
        frameInfo: FrameInfo,
        onResult: (DetectionResult) -> Unit
    ) {
        val mediaImage = image.image ?: run {
            image.close()
            onResult(DetectionResult(info = "image null"))
            return
        }

        val img = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

        detector.process(img)
            .addOnSuccessListener { pose: Pose ->

                val rotatedW =
                    if (frameInfo.rotation % 180 == 0) frameInfo.width.toFloat()
                    else frameInfo.height.toFloat()

                val rotatedH =
                    if (frameInfo.rotation % 180 == 0) frameInfo.height.toFloat()
                    else frameInfo.width.toFloat()

                val list = mutableListOf<Landmark>()

                // 🔥 전체 랜드마크는 여기서 순회
                for (lm in pose.allPoseLandmarks) {
                    val x = (lm.position.x / rotatedW).coerceIn(0f, 1f)
                    val y = (lm.position.y / rotatedH).coerceIn(0f, 1f)
                    list += Landmark(x, y)
                }

                onResult(
                    DetectionResult(
                        landmarks = list,
                        info = "ok(${list.size})"
                    )
                )
            }
            .addOnFailureListener { e ->
                onResult(DetectionResult(info = e.message))
            }
            .addOnCompleteListener {
                image.close()
            }
    }

    fun close() = detector.close()
}
