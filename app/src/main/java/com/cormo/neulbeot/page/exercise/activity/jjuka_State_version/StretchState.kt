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

        // 성공한거 무시하는 코드
        if ((detected == StretchPhase.HANDS_UP && handsUpDone) ||
            (detected == StretchPhase.LEFT_HOLD && leftDone) ||
            (detected == StretchPhase.RIGHT_HOLD && rightDone)
        ) {
            lastTimestamp = timestamp
            holdTimeMs = 0L
            return
        }

        // 포즈 이상하면 리셋시키기
        if (phase != detected) {
            holdTimeMs = 0L
            phase = detected
        } else {
            if (lastTimestamp != 0L) {
                holdTimeMs += (timestamp - lastTimestamp)
            }
        }

        lastTimestamp = timestamp
//
//        if (holdTimeMs >= 2000L) {
//            when (phase) {
//                StretchPhase.HANDS_UP -> {
//                    handsUpDone = true
//                    phase = StretchPhase.LEFT_HOLD
//                }
//                StretchPhase.LEFT_HOLD -> {
//                    leftDone = true
//                    phase = StretchPhase.RIGHT_HOLD
//                }
//                StretchPhase.RIGHT_HOLD -> {
//                    rightDone = true
//                    phase = StretchPhase.LEFT_HOLD
//                }
//                else -> {}
//            }
//            holdTimeMs = 0L
//            if (handsUpDone && leftDone && rightDone) {
//                phase = StretchPhase.FINISHED
//            }
//        }
        // 2초 유지하면 해당 포즈 완료 처리
        if (holdTimeMs >= 2000L) {
            when (phase) {
                StretchPhase.HANDS_UP -> handsUpDone = true
                StretchPhase.LEFT_HOLD -> leftDone = true
                StretchPhase.RIGHT_HOLD -> rightDone = true
                else -> {}
            }

            holdTimeMs = 0L

            // 세 동작 모두 완료 → FINISHED
            phase = if (handsUpDone && leftDone && rightDone) {
                StretchPhase.FINISHED
            } else {
                StretchPhase.IDLE
            }
        }
    }

    fun progress(): Float {
        return (holdTimeMs / 2000f).coerceIn(0f, 1f)
    }

//    fun nextInstruction(): String {
//        return when (phase) {
//            StretchPhase.IDLE -> "팔을 머리 위로 올리세요"
//            StretchPhase.HANDS_UP -> "2초 유지하세요"
//            StretchPhase.LEFT_HOLD -> "오른쪽으로 기울이세요"
//            StretchPhase.RIGHT_HOLD -> "왼쪽으로 기울이세요"
//            StretchPhase.FINISHED -> "스트레칭 완료!"
//            StretchPhase.END -> "종료"
//        }
//    }

    fun nextInstruction(): String {
        // 1) 이미 끝났으면 고정 메시지
        if (phase == StretchPhase.FINISHED) {
            return "스트레칭 완료!"
        }

        // 2) 지금 하고 있는 포즈에 맞는 안내를 먼저 준다
        when (phase) {
            StretchPhase.HANDS_UP -> {
                return if (!handsUpDone) {
                    "팔을 위로 든 상태로 2초 유지하세요"
                } else {
                    "이제 다른 방향으로 움직여 볼까요?"
                }
            }
            StretchPhase.RIGHT_HOLD -> {
                if (!rightDone) {
                    return "왼쪽으로 기울인 자세를 2초 유지하세요"
                }
            }
            StretchPhase.LEFT_HOLD -> {
                if (!leftDone) {
                    return "오른쪽으로 기울인 자세를 2초 유지하세요"
                }
            }
            else -> { /* IDLE, END 등은 밑에서 처리 */ }
        }

        // 3) 지금은 중립 상태거나, 이미 끝난 포즈 → '추천 순서' 기반 가이드
        return when {
            !handsUpDone -> "먼저 팔을 머리 위로 올려볼까요?"
            !rightDone -> "이번에는 왼쪽으로 몸을 기울여 볼까요?"
            !leftDone -> "마지막으로 오른쪽으로 몸을 기울여 볼까요?"
            else -> "조금만 더! 편하게 자세를 유지해 주세요."
        }
    }

}
