package com.cormo.neulbeot.page.exercise.activity.jjuka_State_version

object StretchEvaluator {

    fun detectPhase(landmarks: List<Landmark>): StretchState.StretchPhase {

        if (landmarks.size < 29) return StretchState.StretchPhase.IDLE

        val ls = landmarks[11]
        val rs = landmarks[12]
        val lw = landmarks[15]
        val rw = landmarks[16]
        val lh = landmarks[23]
        val rh = landmarks[24]

        val centerX = (lh.x + rh.x) / 2f

        val handsUp = lw.y < ls.y && rw.y < rs.y
        if (!handsUp) return StretchState.StretchPhase.IDLE

        if (lw.x < centerX - 0.05f)
            return StretchState.StretchPhase.LEFT_HOLD

        if (rw.x > centerX + 0.05f)
            return StretchState.StretchPhase.RIGHT_HOLD

        return StretchState.StretchPhase.HANDS_UP
    }
}
