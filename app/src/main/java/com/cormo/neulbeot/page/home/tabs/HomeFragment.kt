package com.cormo.neulbeot.page.home.tabs

import android.content.Intent
import android.graphics.Color
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
import com.cormo.neulbeot.auth.AuthRepository
import com.cormo.neulbeot.fcm.sendFcmTokenAfterLogin
import com.cormo.neulbeot.page.home.HomeActivity
import com.cormo.neulbeot.page.home.api.HomeRepository
import com.cormo.neulbeot.page.login.LoginMethodActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.w3c.dom.Text
import java.time.LocalDate
import java.util.Calendar

class HomeFragment : Fragment() {
    private val vm: HomeModel by activityViewModels()
    private val repository by lazy { AuthRepository(requireContext()) }

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
        val btnMore = v.findViewById<ImageView>(R.id.btn_more)

        val monday = v.findViewById<TextView>(R.id.monday)
        val tuesday = v.findViewById<TextView>(R.id.tuesday)
        val wednesday = v.findViewById<TextView>(R.id.wednesday)
        val thursday = v.findViewById<TextView>(R.id.thursday)
        val friday = v.findViewById<TextView>(R.id.friday)
        val saturday = v.findViewById<TextView>(R.id.saturday)
        val sunday = v.findViewById<TextView>(R.id.sunday)
        val dayViews = listOf(
            monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday
        )

        btnMore.setOnClickListener {
            showMoreMenu()
        }

        vm.initHome()
        vm.weekAttendance.observe(viewLifecycleOwner) { list ->

            val todayDay = LocalDate.now().dayOfMonth

            for (i in list.indices) {
                val item = list[i]
                val textView = dayViews[i]

                textView.text = item.day.toString()

                when {
                    // 1) 오늘 이전 + 출석 O
                    item.check -> {
                        textView.setBackgroundResource(R.drawable.bg_dark_blue_oval)
                        textView.setTextColor(Color.parseColor("#FFFFFF"))
                    }

                    // 2) 오늘 이전 + 출석 X
                    item.day <= todayDay -> {
                        textView.setBackgroundResource(R.drawable.bg_blue_oval)
                        textView.setTextColor(Color.parseColor("#5D5D5D"))
                    }

                    // 오늘 이후
                    item.day > todayDay -> {
                        textView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        textView.setTextColor(Color.parseColor("#5D5D5D"))
                    }

                }
            }
        }



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

        vm.levelProgress.observe(viewLifecycleOwner) { exp ->
            tvCoin.text = exp.toString() + "EXP"
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

    private fun showMoreMenu() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_more_menu, null)
        dialog.setContentView(view)


        val btnLogout = view.findViewById<TextView>(R.id.btnLogout)
        // 로그인 화면 등 다른 메뉴 생기면 여기서도 findViewById 해서 추가

        btnLogout.setOnClickListener {
            // TODO: 실제 로그아웃 로직
            // 예시:
            // TokenStorage.clear(requireContext())
            // vm.clearUserState() 등
            repository.logout()
            val intent = Intent(requireContext(), LoginMethodActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            activity?.finishAffinity()

            dialog.dismiss()
        }

        dialog.show()
    }


}
