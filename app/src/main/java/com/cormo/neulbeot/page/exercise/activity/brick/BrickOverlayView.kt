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
package com.cormo.neulbeot.page.exercise.activity.brick

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class BrickOverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: HandLandmarkerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()
    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1
    private var offsetX = 0f
    private var offsetY = 0f

    private val indexTipPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val targetPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private var targetBitmap: Bitmap? = null

    private var targetXImg: Float = -1f
    private var targetYImg: Float = -1f
    private var targetRadiusImg: Float = 0f
    private var hasTarget = false
    private var gameRunning = false

    private val isFingerInside = BooleanArray(2) { false }
    private var targetHP = 3
    private val maxHP = 3

    private val hpTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private var targetSpawnTime = 0L
    private val hitDelayMs = 200L

    private var onHitListener: (() -> Unit)? = null

    init {
        //initPaints()
    }

    fun clear() {
        results = null
        hasTarget = false
        gameRunning = false
//        initPaints()
        invalidate()
    }

    fun setTargetImage(bitmap: Bitmap) {
        targetBitmap = bitmap
    }

//    private fun initPaints() {
//        linePaint.color = ContextCompat.getColor(context!!, R.color.mp_color_primary)
//        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
//        linePaint.style = Paint.Style.STROKE
//
//        pointPaint.color = Color.YELLOW
//        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
//        pointPaint.style = Paint.Style.FILL
//    }

    fun startGame() {
        gameRunning = true
        hasTarget = false
        invalidate()
    }

    fun stopGame() {
        gameRunning = false
        hasTarget = false
        invalidate()
    }

    fun setOnHitListener(listener: (() -> Unit)?) {
        onHitListener = listener
    }

    private fun spawnTargetIfNeeded() {
        if (!gameRunning || hasTarget) return
        if (imageWidth <= 1 || imageHeight <= 1) return

        val minSide = min(imageWidth, imageHeight).toFloat()
        targetRadiusImg = minSide * 0.06f
        val margin = targetRadiusImg * 2.0f

        val minX = imageWidth * 0.15f + margin
        val maxX = imageWidth * 0.85f - margin
        val minY = imageHeight * 0.15f + margin
        val maxY = imageHeight * 0.85f - margin

        targetXImg = Random.nextFloat() * (maxX - minX) + minX
        targetYImg = Random.nextFloat() * (maxY - minY) + minY

        targetHP = maxHP
        for (i in 0..1) isFingerInside[i] = false

        hasTarget = true
        targetSpawnTime = System.currentTimeMillis()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val handResult = results ?: return

        spawnTargetIfNeeded()

        if (gameRunning && hasTarget) {
            val cx = offsetX + targetXImg * scaleFactor
            val cy = offsetY + targetYImg * scaleFactor
            val rPx = targetRadiusImg * scaleFactor
            canvas.drawCircle(cx, cy, rPx, targetPaint)
            canvas.drawText(targetHP.toString(), cx, cy + (hpTextPaint.textSize / 3), hpTextPaint)
        }

        val now = System.currentTimeMillis()
        val hitEnabled = (now - targetSpawnTime >= hitDelayMs)

        val landmarks = handResult.landmarks()

        for ((handIndex, landmarkList) in landmarks.withIndex()) {

            for (lm in landmarkList) {
                val x = offsetX + lm.x() * imageWidth * scaleFactor
                val y = offsetY + lm.y() * imageHeight * scaleFactor
                canvas.drawPoint(x, y, pointPaint)
            }

            HandLandmarker.HAND_CONNECTIONS.forEach {
                val start = landmarkList[it!!.start()]
                val end = landmarkList[it.end()]
                val startX = offsetX + start.x() * imageWidth * scaleFactor
                val startY = offsetY + start.y() * imageHeight * scaleFactor
                val endX = offsetX + end.x() * imageWidth * scaleFactor
                val endY = offsetY + end.y() * imageHeight * scaleFactor
                //       canvas.drawLine(startX, startY, endX, endY, linePaint)
            }

            if (landmarkList.size > 8) {
                val tip = landmarkList[8]
                val tipX = offsetX + tip.x() * imageWidth * scaleFactor
                val tipY = offsetY + tip.y() * imageHeight * scaleFactor
                //  canvas.drawCircle(tipX, tipY, 20f, indexTipPaint)

                if (gameRunning && hasTarget && hitEnabled) {
                    val cx = offsetX + targetXImg * scaleFactor
                    val cy = offsetY + targetYImg * scaleFactor
                    val rPx = targetRadiusImg * scaleFactor

                    val dx = tipX - cx
                    val dy = tipY - cy
                    val dist = sqrt(dx * dx + dy * dy)

                    val insideNow = dist <= rPx * 1.05f

                    if (insideNow && !isFingerInside[handIndex]) {
                        targetHP--
                        if (targetHP <= 0) {
                            hasTarget = false
                            onHitListener?.invoke()
                        }
                    }

                    isFingerInside[handIndex] = insideNow
                }
            }
        }
    }

    fun setResults(
        handResults: HandLandmarkerResult,
        imgHeight: Int,
        imgWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = handResults
        imageHeight = imgHeight
        imageWidth = imgWidth

        scaleFactor = max(
            width.toFloat() / imageWidth,
            height.toFloat() / imageHeight
        )

        offsetX = (width - imageWidth * scaleFactor) / 2f
        offsetY = (height - imageHeight * scaleFactor) / 2f

        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8f
    }
}
