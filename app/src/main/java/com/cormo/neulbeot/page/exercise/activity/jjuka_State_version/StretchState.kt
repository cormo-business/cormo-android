package com.cormo.neulbeot.page.exercise.activity.jjuka_State_version

class StretchState {

    enum class StretchPhase {
        IDLE,
        HANDS_UP,
        LEFT_HOLD,
        RIGHT_HOLD,
        FINISHED,
        END,
    }

    var phase = StretchPhase.IDLE

    private var holdTimeMs: Long = 0L
    private var lastTimestamp: Long = 0L

    private var handsUpDone = false
    private var leftDone = false
    private var rightDone = false

    fun update(detected: StretchPhase, timestamp: Long) {

        if (phase == StretchPhase.FINISHED) {
            lastTimestamp = timestamp
            return
        }

        if ((detected == StretchPhase.HANDS_UP && handsUpDone) ||
            (detected == StretchPhase.LEFT_HOLD && leftDone) ||
            (detected == StretchPhase.RIGHT_HOLD && rightDone)
        ) {
            lastTimestamp = timestamp
            holdTimeMs = 0L
            return
        }

        if (phase != detected) {
            holdTimeMs = 0L
            phase = detected
        } else {
            if (lastTimestamp != 0L) {
                holdTimeMs += (timestamp - lastTimestamp)
            }
        }

        lastTimestamp = timestamp

        if (holdTimeMs >= 2000L) {
            when (phase) {
                StretchPhase.HANDS_UP -> {
                    handsUpDone = true
                    phase = StretchPhase.LEFT_HOLD
                }
                StretchPhase.LEFT_HOLD -> {
                    leftDone = true
                    phase = StretchPhase.RIGHT_HOLD
                }
                StretchPhase.RIGHT_HOLD -> {
                    rightDone = true
                    phase = StretchPhase.FINISHED
                }
                else -> {}
            }
            holdTimeMs = 0L
        }
    }

    fun progress(): Float {
        return (holdTimeMs / 2000f).coerceIn(0f, 1f)
    }

    fun nextInstruction(): String {
        return when (phase) {
            StretchPhase.IDLE -> "팔을 머리 위로 올리세요"
            StretchPhase.HANDS_UP -> "2초 유지하세요"
            StretchPhase.LEFT_HOLD -> "오른쪽으로 기울이세요"
            StretchPhase.RIGHT_HOLD -> "왼쪽으로 기울이세요"
            StretchPhase.FINISHED -> "스트레칭 완료!"
            StretchPhase.END -> "종료"
        }
    }
}
