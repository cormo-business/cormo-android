package com.cormo.neulbeot.page.home.tabs

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt

class ChallengeFragment : Fragment(R.layout.challenge_fragment_challenge) {

    private var expanded = false
    private var collapsedWidthPx = 0
    private var expandedWidthPx = 0

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        // ===== 레벨/진행상황 (데모 값 세팅 가능) =====
        v.findViewById<TextView>(R.id.tvLevelBadge).text = "Lv.14"
        v.findViewById<TextView>(R.id.tvEnergyText).text = "에너지 코인 80/100"
        v.findViewById<ProgressBar>(R.id.progressEnergy).progress = 80

        v.findViewById<TextView>(R.id.tvDone).text = "2/4 완료"
        v.findViewById<TextView>(R.id.tvPoint).text = "40 포인트 제공"
        v.findViewById<ProgressBar>(R.id.progressToday).progress = 50
        v.findViewById<TextView>(R.id.tvRate).text = "진행률 50%"
        v.findViewById<TextView>(R.id.tvLeft).text = "2개 미션 남음"

        // ===== 미션 노드 세팅 =====
        setupMissionNodes(v)

        // ===== 주간 챌린지 카드 접힘/펼침 =====
        val card = v.findViewById<MaterialCardView>(R.id.weeklyChallengeCard)
        val collapsed = v.findViewById<View>(R.id.collapsedRow)
        val expandedCol = v.findViewById<View>(R.id.expandedCol)
        val dotsRow = v.findViewById<LinearLayout>(R.id.dotsRow)

        // 점 7개(3개 완료)
        dotsRow.removeAllViews()
        repeat(7) { i ->
            val dot = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(12.dp, 12.dp).apply { (this as ViewGroup.MarginLayoutParams).marginEnd = 8.dp }
                setBackgroundResource(if (i < 3) R.drawable.bg_dot_yellow else R.drawable.bg_dot_white_40)
            }
            dotsRow.addView(dot)
        }

        // 초기 가로폭들 계산
        card.post {
            collapsedWidthPx = 160.dp
            expandedWidthPx = (v.width * 0.8f).roundToInt()
        }

        card.setOnClickListener {
            expanded = !expanded
            collapsed.visibility = if (expanded) View.GONE else View.VISIBLE
            expandedCol.visibility = if (expanded) View.VISIBLE else View.GONE
            animateCardWidth(card, if (expanded) expandedWidthPx else collapsedWidthPx)
        }

        v.findViewById<Button>(R.id.btnJoinWeekly).setOnClickListener {
            // TODO: 참여하기 액션
            Toast.makeText(requireContext(), "참여하기", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMissionNodes(root: View) {
        fun bindNode(containerId: Int, text: String, style: NodeStyle) {
            val nodeRoot = root.findViewById<View>(containerId)
            val circle = nodeRoot.findViewById<FrameLayout>(R.id.circle)
            val tv = nodeRoot.findViewById<TextView>(R.id.tvNodeText)
            tv.text = text

            when (style) {
                NodeStyle.LOCKED -> {
                    circle.setBackgroundResource(R.drawable.bg_circle_grey)
                    circle as FrameLayout
                    circle.removeAllViews()
                    val q = TextView(requireContext()).apply {
                        setText("새롭")
                        setTextColor(resources.getColor(android.R.color.white, null))
                        textSize = 18f
                        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
                    }
                    circle.addView(q)
                }
                NodeStyle.NORMAL -> {
                    circle.setBackgroundResource(R.drawable.bg_circle_teal)
                    (circle as FrameLayout).removeAllViews()
                }
                NodeStyle.HIGHLIGHT -> {
                    circle.setBackgroundResource(R.drawable.bg_circle_purple)
                    (circle as FrameLayout).removeAllViews()
                }
            }
        }

        bindNode(R.id.nodeLocked, "?", NodeStyle.LOCKED)
        bindNode(R.id.nodeToday, "오늘의 운동", NodeStyle.NORMAL)
        bindNode(R.id.nodeWeekly, "주간 챌린지", NodeStyle.HIGHLIGHT)
    }

    private fun animateCardWidth(card: MaterialCardView, targetWidth: Int) {
        val start = card.width
        ValueAnimator.ofInt(start, targetWidth).apply {
            duration = 300
            addUpdateListener {
                val w = it.animatedValue as Int
                card.updateLayoutParams<ViewGroup.LayoutParams> { width = w }
            }
            start()
        }
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).roundToInt()

    enum class NodeStyle { LOCKED, NORMAL, HIGHLIGHT }
}
