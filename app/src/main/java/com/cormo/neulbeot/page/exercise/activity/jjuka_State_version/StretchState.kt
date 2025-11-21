package com.cormo.neulbeot.page.exercise.activity.jjuka_State_version

class StretchState {
    enum class Phase {
        NONE, HANDS_UP, LEFT_STRETCH, RIGHT_STRETCH
    }

    var phase: Phase = Phase.NONE
    var holdTimeMs: Long = 0L
    var lastTimestamp: Long = 0L

    fun updatePhase(newPhase: Phase, timestamp: Long) {
        if (phase == newPhase) {
            // 같은 자세 유지 → 시간 증가
            if (lastTimestamp != 0L) {
                holdTimeMs += (timestamp - lastTimestamp)
            }
        } else {
            // 다른 자세로 변경 → 게이지 초기화
            phase = newPhase
            holdTimeMs = 0L
        }
        lastTimestamp = timestamp
    }

    fun progress(): Float {
        return (holdTimeMs / 2000f).coerceIn(0f, 1f)
    }
}
