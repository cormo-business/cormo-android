package com.cormo.neulbeot.page.exercise.activity.sucess_version

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class Overlay(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var result: DetectionResult? = null
    private var frameInfo: FrameInfo? = null

    // 카메라 원본의 회전 적용된 폭/높이 저장
    private var cameraW = 0f
    private var cameraH = 0f

    private val paintPoint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 10f
        isAntiAlias = true
    }

    private val paintLine = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    // MLKit 연결선
    private val pairs = listOf(
        11 to 13, 13 to 15,     // 왼팔
        12 to 14, 14 to 16,     // 오른팔
        23 to 25, 25 to 27,     // 왼다리
        24 to 26, 26 to 28,     // 오른다리
        11 to 12,               // 어깨
        23 to 24,               // 골반
        11 to 23, 12 to 24      // 몸통
    )

    fun update(result: DetectionResult, f: FrameInfo) {
        this.result = result
        this.frameInfo = f

        // 회전이 적용된 실제 입력 영상 크기
        cameraW = if (f.rotation % 180 == 0) f.width.toFloat() else f.height.toFloat()
        cameraH = if (f.rotation % 180 == 0) f.height.toFloat() else f.width.toFloat()

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val r = result ?: return
        val f = frameInfo ?: return
        if (r.landmarks.isEmpty()) return

        //------------------------------------------
        // ① PreviewView에 실제 영상이 어떻게 표시되는지 계산
        //------------------------------------------
        val viewW = width.toFloat()
        val viewH = height.toFloat()
        val cameraRatio = cameraH / cameraW       // 4:3 = 1.3333
        val viewRatio   = viewH / viewW

        var drawW: Float
        var drawH: Float
        var offsetX = 0f
        var offsetY = 0f

        if (cameraRatio > viewRatio) {
            // 카메라가 더 세로로 김 → 위아래 꽉 차고 좌우 잘림
            drawH = viewH
            drawW = drawH / cameraRatio
            offsetX = (viewW - drawW) / 2f
        } else {
            // 카메라가 더 가로로 김 → 좌우 꽉 차고 위아래 잘림
            drawW = viewW
            drawH = drawW * cameraRatio
            offsetY = (viewH - drawH) / 2f
        }

        //------------------------------------------
        // ② MLKit 0~1 좌표 → 화면 좌표로 변환
        //------------------------------------------
        fun mapPoint(x: Float, y: Float): PointF {
            var nx = x
            var ny = y

            if (f.isFront) nx = 1f - nx   // 전면카메라 좌우반전

            val px = nx * drawW + offsetX
            val py = ny * drawH + offsetY

            return PointF(px, py)
        }

        //------------------------------------------
        // ③ 연결선 그리기
        //------------------------------------------
        for ((a, b) in pairs) {
            if (a < r.landmarks.size && b < r.landmarks.size) {
                val p1 = r.landmarks[a]
                val p2 = r.landmarks[b]
                val A = mapPoint(p1.x, p1.y)
                val B = mapPoint(p2.x, p2.y)
                canvas.drawLine(A.x, A.y, B.x, B.y, paintLine)
            }
        }

        //------------------------------------------
        // ④ 점 그리기
        //------------------------------------------
        for (p in r.landmarks) {
            val mapped = mapPoint(p.x, p.y)
            canvas.drawCircle(mapped.x, mapped.y, 8f, paintPoint)
        }
    }
}
