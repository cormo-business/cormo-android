package com.cormo.neulbeot.page.exercise.activity.jjuka_State_version

object StretchEvaluator {

    /**
     * landmarks = 0~1 normalized 좌표
     * return = StretchState.Phase
     */
    fun evaluate(landmarks: List<Landmark>): StretchState.Phase {
        if (landmarks.size < 29) return StretchState.Phase.NONE

        val leftShoulder = landmarks[11]
        val rightShoulder = landmarks[12]
        val leftWrist = landmarks[15]
        val rightWrist = landmarks[16]
        val leftHip = landmarks[23]
        val rightHip = landmarks[24]

        // 몸 중간 x
        val centerX = (leftHip.x + rightHip.x) / 2f

        // ===== 0) 팔 위로 올리기 조건 =====
        val handsUp =
            leftWrist.y < leftShoulder.y &&
                    rightWrist.y < rightShoulder.y

        if (!handsUp) return StretchState.Phase.NONE

        // ===== 1) 좌측 사이드 스트레칭 =====
        if (leftWrist.x < centerX - 0.05f) {
            return StretchState.Phase.LEFT_STRETCH
        }

        // ===== 2) 우측 사이드 스트레칭 =====
        if (rightWrist.x > centerX + 0.05f) {
            return StretchState.Phase.RIGHT_STRETCH
        }

        // 기본: 팔만 올린 상태
        return StretchState.Phase.HANDS_UP
    }
}
