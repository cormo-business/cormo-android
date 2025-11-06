package com.cormo.neulbeot.page.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.home.tabs.*
import com.cormo.neulbeot.core.widget.HomeBottomBarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager2
    private lateinit var bottomBar: HomeBottomBarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        pager = findViewById(R.id.homePager)
        bottomBar = findViewById(R.id.homeBottomBar)

        pager.isUserInputEnabled = false // 스와이프 이동 막고 탭으로만 전환
        pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 5
            override fun createFragment(position: Int) = when (position) {
                0 -> HomeFragment()
                1 -> ChallengeFragment()
                2 -> ReportFragment()
                3 -> CommunityFragment()
                else -> ProfileFragment()
            }
        }
        // ▼ 탭 클릭 → 페이지 전환
        bottomBar.onTabSelected = { index ->
            when (index) {
                0 -> pager.setCurrentItem(0, false) // 홈
                1 -> pager.setCurrentItem(1, false) // 챌린지
                3 -> pager.setCurrentItem(3, false) // 마켓
                4 -> pager.setCurrentItem(4, false) // 프로필
            }
            bottomBar.currentIndex = index
        }

        // ▼ 중앙 버튼 → 원하는 화면으로 이동
        findViewById<View?>(R.id.bottomCenterButtom)?.setOnClickListener{
            // 나중에 실제 페이지가 생기면 여기서 startActivity(...)로 교체
            Toast.makeText(this@HomeActivity, "준비 중입니다.", Toast.LENGTH_SHORT).show()

            // 버튼 누르고 원하는 화면 있을시 연결하기
            // ---example---
            // startActivity(Intent(this, WeekChallengeActivity::class.java))

            // pushReplacementNamed 느낌이면 finish() 추가
            // finish()
        }

        // 초기 탭
        bottomBar.currentIndex = 0
        pager.setCurrentItem(0, false)

        lifecycleScope.launch(Dispatchers.IO) {

        }


        // ▼ 페이지 스와이프로 바뀌면 하단바 상태도 동기화
        // pager.isUserInputEnabled = true 바꿔야함
//        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                // 포지션 매핑: 0=홈, 1=챌린지, 2=리포트(중앙은 2가 아님), 3=마켓, 4=프로필
//                val index = when (position) {
//                    0 -> 0
//                    1 -> 1
//                    3 -> 3
//                    4 -> 4
//                    else -> bottomBar.currentIndex  // 리포트(2)는 하단 탭과 직접 매핑 없음 (필요하면 추가)
//                }
//                bottomBar.currentIndex = index
//            }
//        })

    }
}
