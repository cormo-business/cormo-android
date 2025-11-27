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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.exercise.WithFriendsActivity
import com.cormo.neulbeot.page.exercise.WeekChallengeActivity
import com.cormo.neulbeot.page.home.vm.HomeModel
import kotlin.getValue
import androidx.fragment.app.activityViewModels
import com.cormo.neulbeot.fcm.sendFcmTokenAfterLogin
import com.cormo.neulbeot.page.home.HomeActivity
import com.cormo.neulbeot.page.login.LoginMethodActivity
import org.w3c.dom.Text
import java.util.Calendar

class HomeFragment : Fragment() {
    private val vm: HomeModel by activityViewModels()

    // View를 만드는 단계
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.home, container, false)
    }

    // View가 완성된 뒤 호출되는 단계
    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        // ===== 프로필 섹션 바인딩 =====
        val tvTitle = v.findViewById<TextView>(R.id.tvProfileTitle)
        val tvLevel = v.findViewById<TextView>(R.id.tvLevel)
        val tvCoin = v.findViewById<TextView>(R.id.tvCoin)
        val progress = v.findViewById<ProgressBar>(R.id.levelProgress)
        val btnToday = v.findViewById<TextView>(R.id.btnTodayWorkout)
        val btnFriends = v.findViewById<TextView>(R.id.btnWithFriends)
//        val ivProfile = v.findViewById<ImageView>(R.id.ivProfile)
//        val tvAttendance = v.findViewById<TextView>(R.id.tvAttendanceDesc)

        vm.initHome()

//        vm.attendanceNum.observe(viewLifecycleOwner){ num ->
//            val cal = Calendar.getInstance()
//            val currentMonth = cal.get(Calendar.MONTH) + 1 // 0부터 시작하므로 +1
//
//            tvAttendance.text = "${currentMonth}월에 ${num}회 출석했습니다"
//        }

        vm.nickname.observe(viewLifecycleOwner) { name ->
            // vm 인식 이후에 처리하기 위하여
            sendFcmTokenAfterLogin(requireContext())
            tvTitle.text = "${name}님,"
        }

        fun render() {
            val level = vm.level.value ?: 0
            val p = vm.levelProgress.value ?: 0
            tvLevel.text = "Lv.$level"
            var total = 100+50*(level-1)
            progress.progress = (total - p) * 100 / total
        }

        vm.level.observe(viewLifecycleOwner) { render() }
        vm.levelProgress.observe(viewLifecycleOwner) { render() }

        vm.point.observe(viewLifecycleOwner) { pt ->
            tvCoin.text = pt.toString() + "point"
        }

//        vm.profilePath.observe(viewLifecycleOwner) { path ->
//            if (!path.isNullOrBlank()) {
//                runCatching { ivProfile.setImageURI(Uri.parse(path)) }
//            } else {
//                ivProfile.setImageResource(R.drawable.ic_account_circle_96)
//            }
//        }

        btnToday.setOnClickListener {
//            startActivity(Intent(requireContext(), WeekChallengeActivity::class.java))
            Toast.makeText(context, "잠시만 기달려 주세요", Toast.LENGTH_SHORT).show()

        }
        btnFriends.setOnClickListener {
//            startActivity(Intent(requireContext(), WithFriendsActivity::class.java))
            Toast.makeText(context, "잠시만 기달려 주세요", Toast.LENGTH_SHORT).show()
        }

        //출석 카드
        val btnAttendance = v.findViewById<TextView>(R.id.btnAttendance)
        btnAttendance.setOnClickListener {
            vm.attendance()
            // todo
//            startActivity(Intent(requireContext(), AttendanceCheckActivity::class.java))
        }

    }
}
