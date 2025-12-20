package com.cormo.neulbeot.page.exercise.activity.squart_version

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class SquartOverlay(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

        private var result: DetectionResult? = null
        private var frameInfo: FrameInfo? = null
        private var squatCount: Int = 0

        private var camW = 0f
        private var camH = 0f

        fun update(result: DetectionResult, frame: FrameInfo, squatCount: Int) {
            this.result = result
            this.frameInfo = frame
            this.squatCount = squatCount

            camW = if (frame.rotation % 180 == 0) frame.width.toFloat() else frame.height.toFloat()
            camH = if (frame.rotation % 180 == 0) frame.height.toFloat() else frame.width.toFloat()

            invalidate()
        }

        private val paintPoint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        private val paintLine = Paint().apply {
            color = Color.CYAN
            strokeWidth = 8f
            isAntiAlias = true
        }

        private val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 170f
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        // MLKit 공식 좌표 인덱스 기반 연결선
        private val pairs = listOf(
            11 to 13, 13 to 15,
            12 to 14, 14 to 16,
            23 to 25, 25 to 27,
            24 to 26, 26 to 28,
            11 to 12, 23 to 24,
            11 to 23, 12 to 24
        )

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val r = result ?: return
            val f = frameInfo ?: return
            if (r.landmarks.isEmpty()) return

            val viewW = width.toFloat()
            val viewH = height.toFloat()

            val camRatio = camH / camW
            val viewRatio = viewH / viewW

            var drawW: Float
            var drawH: Float
            var offsetX = 0f
            var offsetY = 0f

            if (camRatio > viewRatio) {
                drawH = viewH
                drawW = drawH / camRatio
                offsetX = (viewW - drawW) / 2f
            } else {
                drawW = viewW
                drawH = drawW * camRatio
                offsetY = (viewH - drawH) / 2f
            }

            fun map(x: Float, y: Float): PointF {
                var nx = x
                if (f.isFront) nx = 1f - nx

                val px = nx * drawW + offsetX
                val py = y * drawH + offsetY
                return PointF(px, py)
            }

            // 선
            for ((a, b) in pairs) {
                if (a < r.landmarks.size && b < r.landmarks.size) {
                    val p1 = map(r.landmarks[a].x, r.landmarks[a].y)
                    val p2 = map(r.landmarks[b].x, r.landmarks[b].y)
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paintLine)
                }
            }

            // 점
            for (lm in r.landmarks) {
                val p = map(lm.x, lm.y)
                canvas.drawCircle(p.x, p.y, 10f, paintPoint)
            }

            // 스쿼트 카운트
            canvas.drawText("Squat: $squatCount", 50f, 200f, paintText)
        }
    }
