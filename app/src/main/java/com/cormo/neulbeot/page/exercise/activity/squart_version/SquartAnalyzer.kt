//package com.cormo.neulbeot.page.exercise.activity.squart_version
//
//import androidx.camera.core.ExperimentalGetImage
//import androidx.camera.core.ImageProxy
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.pose.Pose
//import com.google.mlkit.vision.pose.PoseDetection
//import com.google.mlkit.vision.pose.PoseDetector
//import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
//
//data class Landmark(val x: Float, val y: Float)
//
//data class DetectionResult(
//    val landmarks: List<Landmark> = emptyList(),
//    val info: String? = null
//)
//
//data class FrameInfo(
//    val width: Int,
//    val height: Int,
//    val rotation: Int,
//    val isFront: Boolean
//)
//
//class SquartAnalyzer {
//
//    private val options = AccuratePoseDetectorOptions.Builder()
//        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
//        .build()
//
//    private val detector: PoseDetector = PoseDetection.getClient(options)
//
//    @OptIn(ExperimentalGetImage::class)
//    fun analyze(
//        image: ImageProxy,
//        frameInfo: FrameInfo,
//        onResult: (DetectionResult) -> Unit
//    ) {
//        val mediaImage = image.image ?: run {
//            image.close()
//            onResult(DetectionResult(info = "image null"))
//            return
//        }
//
//        val img = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
//
//        detector.process(img)
//            .addOnSuccessListener { pose: Pose ->
//
//                val rotatedW =
//                    if (frameInfo.rotation % 180 == 0) frameInfo.width.toFloat()
//                    else frameInfo.height.toFloat()
//
//                val rotatedH =
//                    if (frameInfo.rotation % 180 == 0) frameInfo.height.toFloat()
//                    else frameInfo.width.toFloat()
//
//                val list = mutableListOf<Landmark>()
//
//                // 🔥 전체 랜드마크는 여기서 순회
//                for (lm in pose.allPoseLandmarks) {
//                    val x = (lm.position.x / rotatedW).coerceIn(0f, 1f)
//                    val y = (lm.position.y / rotatedH).coerceIn(0f, 1f)
//                    list += Landmark(x, y)
//                }
//
//                onResult(
//                    DetectionResult(
//                        landmarks = list,
//                        info = "ok(${list.size})"
//                    )
//                )
//            }
//            .addOnFailureListener { e ->
//                onResult(DetectionResult(info = e.message))
//            }
//            .addOnCompleteListener {
//                image.close()
//            }
//    }
//
//    fun close() = detector.close()
//}
package com.cormo.neulbeot.page.exercise.activity.squart_version

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlin.math.*

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

    private var currentState: String = "Idle"

    private fun angle(p1: Landmark, p2: Landmark, p3: Landmark): Float {
        val v1x = p1.x - p2.x
        val v1y = p1.y - p2.y
        val v2x = p3.x - p2.x
        val v2y = p3.y - p2.y
        val dot = v1x * v2x + v1y * v2y
        val m1 = sqrt(v1x*v1x + v1y*v1y)
        val m2 = sqrt(v2x*v2x + v2y*v2y)
        if (m1 == 0f || m2 == 0f) return 0f
        val cos = (dot / (m1*m2)).coerceIn(-1f, 1f)
        return acos(cos) * 180f / Math.PI.toFloat()
    }

    private fun determineState(lm: List<Landmark>): String {
        if (lm.size < 28) return "No Pose"

        val hip = lm[23]
        val knee = lm[25]
        val ankle = lm[27]

        val kneeAngle = angle(hip, knee, ankle)

        return when {
            currentState == "Idle" && kneeAngle < 150 -> {
                currentState = "Down"; "Down"
            }
            currentState == "Down" && kneeAngle < 90 -> {
                currentState = "Bottom"; "Bottom"
            }
            currentState == "Bottom" && kneeAngle > 120 -> {
                currentState = "Up"; "Up"
            }
            currentState == "Up" && kneeAngle > 165 -> {
                currentState = "Finish"; "Finish"
            }
            else -> currentState
        }
    }

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

                for (lm in pose.allPoseLandmarks) {
                    list += Landmark(
                        (lm.position.x / rotatedW).coerceIn(0f, 1f),
                        (lm.position.y / rotatedH).coerceIn(0f, 1f)
                    )
                }

                val state = determineState(list)

                onResult(
                    DetectionResult(
                        landmarks = list,
                        info = "ok(${list.size}) / state=$state"
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
