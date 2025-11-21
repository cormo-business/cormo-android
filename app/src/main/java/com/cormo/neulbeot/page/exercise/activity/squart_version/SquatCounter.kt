package com.cormo.neulbeot.page.exercise.activity.squart_version

class SquatCounter(
    private val threshold: Float = 0.04f // 엉덩이-무릎 높이 차 허용치
) {
    enum class State {
        UP, DOWN
    }

    var count = 0
        private set

    private var state = State.UP

    fun update(landmarks: List<Landmark>) {
        if (landmarks.size < 30) return

        val leftHip = landmarks[23]
        val rightHip = landmarks[24]
        val leftKnee = landmarks[25]
        val rightKnee = landmarks[26]

        val hipY = (leftHip.y + rightHip.y) / 2f
        val kneeY = (leftKnee.y + rightKnee.y) / 2f

        val isDown = hipY > kneeY + threshold
        val isUp = hipY < kneeY - threshold

        if (isDown && state == State.UP) {
            state = State.DOWN
        }

        if (isUp && state == State.DOWN) {
            count++
            state = State.UP
        }
    }

    fun reset() {
        count = 0
        state = State.UP
    }
}
