package com.cormo.neulbeot.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.cormo.neulbeot.R

/**
 * 완전 커스텀 하단바 (좌2 + 중앙 버튼 + 우2)
 * - currentIndex: 0=홈, 1=챌린지, 2=(중앙 버튼 별도), 3=마켓, 4=프로필
 * - onTabSelected(index) : 좌/우 탭 클릭 콜백
 * - onStart() : 중앙 버튼 클릭
 */
class HomeBottomBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val active = 0xFF00C8E5.toInt()
    private val inactive = ContextCompat.getColor(context, android.R.color.darker_gray)

    // 컨테이너
    private val itemHome by lazy { findViewById<ViewGroup>(R.id.item_home) }
    private val itemChallenge by lazy { findViewById<ViewGroup>(R.id.item_challenge) }
    private val itemMarket by lazy { findViewById<ViewGroup>(R.id.item_market) }
    private val itemProfile by lazy { findViewById<ViewGroup>(R.id.item_profile) }

    // 아이콘/라벨
    private val homeIcon by lazy { itemHome.findViewById<ImageView>(R.id.icon) }
    private val homeLabel by lazy { itemHome.findViewById<TextView>(R.id.label) }

    private val chalIcon by lazy { itemChallenge.findViewById<ImageView>(R.id.icon) }
    private val chalLabel by lazy { itemChallenge.findViewById<TextView>(R.id.label) }

    private val marketIcon by lazy { itemMarket.findViewById<ImageView>(R.id.icon) }
    private val marketLabel by lazy { itemMarket.findViewById<TextView>(R.id.label) }

    private val profileIcon by lazy { itemProfile.findViewById<ImageView>(R.id.icon) }
    private val profileLabel by lazy { itemProfile.findViewById<TextView>(R.id.label) }

    private val centerButton by lazy { findViewById<FrameLayout>(R.id.center_button) }

    var onTabSelected: ((index: Int) -> Unit)? = null
    var onStart: (() -> Unit)? = null

    var currentIndex: Int = 0
        set(value) {
            field = value
            renderActive()
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_bottom_bar, this, true)

        // 라벨 (플러터 명칭과 현재 메뉴 명칭 중 택1 — 여기선 메뉴에 맞춤)
        homeLabel.text = "홈"
        chalLabel.text = "게임/챌린지"
        marketLabel.text = "마켓"
        profileLabel.text = "마이페이지"

        // 아이콘 연결 (프로젝트의 실제 아이콘으로 교체 가능)
        homeIcon.setImageResource(R.drawable.ic_home_24)
        chalIcon.setImageResource(R.drawable.ic_flag_24)
        marketIcon.setImageResource(R.drawable.ic_forum_24)      // 기존 메뉴에 맞춤
        profileIcon.setImageResource(R.drawable.ic_person_24)

        // 클릭 → 인덱스 전달
        itemHome.setOnClickListener { onTabSelected?.invoke(0) }
        itemChallenge.setOnClickListener { onTabSelected?.invoke(1) }
        itemMarket.setOnClickListener { onTabSelected?.invoke(3) }
        itemProfile.setOnClickListener { onTabSelected?.invoke(4) }

//        centerButton.setOnClickListener { onStart?.invoke() }

        renderActive()
    }

    private fun renderActive() {
        fun setActive(icon: ImageView, label: TextView, isActive: Boolean) {
            val color = if (isActive) active else inactive
            label.setTextColor(color)
            icon.setColorFilter(color)
        }
        setActive(homeIcon, homeLabel, currentIndex == 0)
        setActive(chalIcon, chalLabel, currentIndex == 1)
        // 중앙(2)은 별도 onStart. 표시가 필요하면 추가로 구현 가능
        setActive(marketIcon, marketLabel, currentIndex == 3)
        setActive(profileIcon, profileLabel, currentIndex == 4)
    }

}
