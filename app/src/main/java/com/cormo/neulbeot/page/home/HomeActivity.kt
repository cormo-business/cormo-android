package com.cormo.neulbeot.page.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cormo.neulbeot.MainActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.auth.TokenStorage
import com.cormo.neulbeot.page.home.tabs.*
import com.cormo.neulbeot.core.widget.HomeBottomBarView
import com.cormo.neulbeot.page.exercise.ExActivity
import com.cormo.neulbeot.page.home.vm.HomeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.getValue

class HomeActivity : AppCompatActivity() {

    private val vm: HomeModel by viewModels()

    private lateinit var pager: ViewPager2
    private lateinit var bottomBar: HomeBottomBarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_frame)

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
            startActivity(Intent(this@HomeActivity, ExActivity::class.java))
        }

        // 초기 탭
        bottomBar.currentIndex = 0
        pager.setCurrentItem(0, false)

        lifecycleScope.launch(Dispatchers.IO) {

        }

        val TAG: String = "로그"
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        val refresh = sharedPreferences.getString("refreshToken", null)
        val acc = sharedPreferences.getString("accessToken", null)

        Log.d(TAG, "HomeActivity - onCreate() called re:${refresh}, acc:${acc}")
        // 토큰없어서
        vm.errorToken.observe(this){ error ->
            if(error != null){
                startActivity(Intent(this, MainActivity::class.java))
                val storage = TokenStorage(this)
                storage.removeAccess()
                finish()
            }
        }

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("로그", "페이지 이동됨: $position")

                // 여기에 원하는 코드 넣으면 됨
                vm.initHome()
            }
        })
    }

}
