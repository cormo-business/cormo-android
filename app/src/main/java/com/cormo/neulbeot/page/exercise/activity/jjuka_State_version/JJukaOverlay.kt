package com.cormo.neulbeot.page.exercise.activity.jjuka_State_version

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class JJukaOverlay(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var result: DetectionResult? = null
    private var frameInfo: FrameInfo? = null

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

    private val pairs = listOf(
        11 to 13, 13 to 15,
        12 to 14, 14 to 16,
        23 to 25, 25 to 27,
        24 to 26, 26 to 28,
        11 to 12,
        23 to 24,
        11 to 23, 12 to 24
    )

    private var gauge: Float = 0f
    private var phaseText: String = ""
    private var instructionText: String = ""

    fun setStretchGauge(g: Float, phase: String) {
        gauge = g
        phaseText = phase
        invalidate()
    }

    fun setInstruction(t: String) {
        instructionText = t
        invalidate()
    }

    fun update(result: DetectionResult, f: FrameInfo) {
        this.result = result
        this.frameInfo = f
        cameraW = if (f.rotation % 180 == 0) f.width.toFloat() else f.height.toFloat()
        cameraH = if (f.rotation % 180 == 0) f.height.toFloat() else f.width.toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val r = result ?: return
        val f = frameInfo ?: return
        if (r.landmarks.isEmpty()) return

        val viewW = width.toFloat()
        val viewH = height.toFloat()
        val cameraRatio = cameraH / cameraW
        val viewRatio = viewH / viewW

        var drawW: Float
        var drawH: Float
        var offsetX = 0f
        var offsetY = 0f

        if (cameraRatio > viewRatio) {
            drawH = viewH
            drawW = drawH / cameraRatio
            offsetX = (viewW - drawW) / 2f
        } else {
            drawW = viewW
            drawH = drawW * cameraRatio
            offsetY = (viewH - drawH) / 2f
        }

        fun mapPoint(x: Float, y: Float): PointF {
            var nx = x
            var ny = y

            if (f.isFront) nx = 1f - nx
            return PointF(nx * drawW + offsetX, ny * drawH + offsetY)
        }

        for ((a, b) in pairs) {
            if (a < r.landmarks.size && b < r.landmarks.size) {
                val A = mapPoint(r.landmarks[a].x, r.landmarks[a].y)
                val B = mapPoint(r.landmarks[b].x, r.landmarks[b].y)
                canvas.drawLine(A.x, A.y, B.x, B.y, paintLine)
            }
        }

        for (p in r.landmarks) {
            val mapped = mapPoint(p.x, p.y)
            canvas.drawCircle(mapped.x, mapped.y, 8f, paintPoint)
        }

        val barW = width * 0.6f
        val barH = 25f
        val barX = (width - barW) / 2f
        val barY = height - 120f

        val borderPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRect(barX, barY, barX + barW, barY + barH, borderPaint)

        val fillPaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }
        canvas.drawRect(barX, barY, barX + barW * gauge, barY + barH, fillPaint)

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 42f
            isAntiAlias = true
        }
        canvas.drawText(phaseText, barX, barY - 25f, textPaint)

        val instPaint = Paint().apply {
            color = Color.YELLOW
            textSize = 50f
            isAntiAlias = true
        }
        canvas.drawText(instructionText, 50f, height - 200f, instPaint)
    }
}
