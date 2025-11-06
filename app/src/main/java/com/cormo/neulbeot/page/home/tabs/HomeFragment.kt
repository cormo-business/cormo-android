package com.cormo.neulbeot.page.home.tabs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.attendance.AttendanceCheckActivity
import com.cormo.neulbeot.page.exercise.WithFriendsActivity
import com.cormo.neulbeot.page.exercise.WeekChallengeActivity
import com.cormo.neulbeot.store.UserStore

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.home_fragment_home, container, false)

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        // ===== 프로필 섹션 바인딩 =====
        val tvTitle = v.findViewById<TextView>(R.id.tvProfileTitle)
        val tvLevel = v.findViewById<TextView>(R.id.tvLevel)
        val tvCoin = v.findViewById<TextView>(R.id.tvCoin)
        val progress = v.findViewById<ProgressBar>(R.id.levelProgress)
        val btnToday = v.findViewById<Button>(R.id.btnTodayWorkout)
        val btnFriends = v.findViewById<Button>(R.id.btnWithFriends)
        val ivProfile = v.findViewById<ImageView>(R.id.ivProfile)

        val user = UserStore.user
        tvTitle.text = "${user.nickname}님,\n에너지 코인을 모아보세요"
        tvLevel.text = "Lv.${user.level}  다음 승급까지"
        tvCoin.text = user.coin.toString()
        progress.progress = 55 // 예시 55%

        user.profilePath?.let { p ->
            runCatching {
                ivProfile.setImageURI(Uri.parse(p))
            }
        }

        btnToday.setOnClickListener {
            startActivity(Intent(requireContext(), WeekChallengeActivity::class.java))
        }
        btnFriends.setOnClickListener {
            startActivity(Intent(requireContext(), WithFriendsActivity::class.java))
        }

        // ===== 출석 카드 =====
        val btnAttendance = v.findViewById<Button>(R.id.btnAttendance)
        btnAttendance.setOnClickListener {
            startActivity(Intent(requireContext(), AttendanceCheckActivity::class.java))
        }

        // ===== 챌린지/모임 카드 클릭(샘플) =====
        v.findViewById<LinearLayout>(R.id.cardWeeklyChallenge)
            .setOnClickListener { startActivity(Intent(requireContext(), WeekChallengeActivity::class.java)) }
        v.findViewById<LinearLayout>(R.id.cardMyGroup)
            .setOnClickListener { /* TODO: 모임 화면으로 */ }

        // ===== 리포트/기록 카드 클릭(샘플) =====
        v.findViewById<LinearLayout>(R.id.cardReport).setOnClickListener { /* TODO */ }
        v.findViewById<LinearLayout>(R.id.cardHistory).setOnClickListener { /* TODO */ }

        // ===== 커뮤니티 카드 클릭(샘플) =====
        v.findViewById<LinearLayout>(R.id.cardNotice).setOnClickListener { /* TODO */ }
        v.findViewById<LinearLayout>(R.id.cardBoard).setOnClickListener { /* TODO */ }
        v.findViewById<LinearLayout>(R.id.cardInvite).setOnClickListener { /* TODO */ }
    }
}
