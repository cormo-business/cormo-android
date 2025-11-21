package com.cormo.neulbeot.page.exercise.activity.jjuka_State_version

import android.graphics.RectF
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions

data class Landmark(val x: Float, val y: Float)
data class DetectionResult(
    val landmarks: List<Landmark> = emptyList(),
    val boxes: List<RectF> = emptyList(),
    val info: String? = null
)
data class FrameInfo(
    val width: Int,
    val height: Int,
    val rotation: Int,
    val isFront: Boolean
)

class JJukaAnalyzer(
    private val mirrorFrontPreview: Boolean = true
) {
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
        val media = image.image ?: run {
            image.close()
            onResult(DetectionResult(info = "no mediaImage"))
            return
        }

        val input = InputImage.fromMediaImage(media, image.imageInfo.rotationDegrees)

        detector.process(input)
            .addOnSuccessListener { pose: Pose ->
                val orderedTypes = listOf(
                    PoseLandmark.NOSE,
                    PoseLandmark.LEFT_EYE_INNER, PoseLandmark.LEFT_EYE, PoseLandmark.LEFT_EYE_OUTER,
                    PoseLandmark.RIGHT_EYE_INNER, PoseLandmark.RIGHT_EYE, PoseLandmark.RIGHT_EYE_OUTER,
                    PoseLandmark.LEFT_EAR, PoseLandmark.RIGHT_EAR,
                    PoseLandmark.LEFT_MOUTH, PoseLandmark.RIGHT_MOUTH,
                    PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER,
                    PoseLandmark.LEFT_ELBOW, PoseLandmark.RIGHT_ELBOW,
                    PoseLandmark.LEFT_WRIST, PoseLandmark.RIGHT_WRIST,
                    PoseLandmark.LEFT_PINKY, PoseLandmark.RIGHT_PINKY,
                    PoseLandmark.LEFT_INDEX, PoseLandmark.RIGHT_INDEX,
                    PoseLandmark.LEFT_THUMB, PoseLandmark.RIGHT_THUMB,
                    PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP,
                    PoseLandmark.LEFT_KNEE, PoseLandmark.RIGHT_KNEE,
                    PoseLandmark.LEFT_ANKLE, PoseLandmark.RIGHT_ANKLE,
                    PoseLandmark.LEFT_HEEL, PoseLandmark.RIGHT_HEEL,
                    PoseLandmark.LEFT_FOOT_INDEX, PoseLandmark.RIGHT_FOOT_INDEX
                )

                // ✅ 회전 적용된 크기로 정규화
                val rotatedW = if (frameInfo.rotation % 180 == 0)
                    frameInfo.width.toFloat() else frameInfo.height.toFloat()
                val rotatedH = if (frameInfo.rotation % 180 == 0)
                    frameInfo.height.toFloat() else frameInfo.width.toFloat()

                val landmarks = mutableListOf<Landmark>()
                var minX = 1f; var minY = 1f
                var maxX = 0f; var maxY = 0f

                for (t in orderedTypes) {
                    val lm = pose.getPoseLandmark(t) ?: continue
                    var xn = lm.position.x / rotatedW
                    val yn = lm.position.y / rotatedH
                    // if (frameInfo.isFront && mirrorFrontPreview) xn = 1f - xn
                    val xClamped = xn.coerceIn(0f, 1f)
                    val yClamped = yn.coerceIn(0f, 1f)
                    landmarks += Landmark(xClamped, yClamped)
                    minX = kotlin.math.min(minX, xClamped)
                    minY = kotlin.math.min(minY, yClamped)
                    maxX = kotlin.math.max(maxX, xClamped)
                    maxY = kotlin.math.max(maxY, yClamped)
                }

                val result =
                    if (landmarks.isEmpty()) DetectionResult(info = "no pose detected")
                    else DetectionResult(
                        landmarks = landmarks,
                        boxes = listOf(RectF(minX, minY, maxX, maxY)),
                        info = "pose detected (${landmarks.size})"
                    )
                onResult(result)
            }
            .addOnFailureListener { e ->
                onResult(DetectionResult(info = "pose error: ${e.message}"))
            }
            .addOnCompleteListener {
                image.close()
            }
    }

    fun close() = detector.close()
}
