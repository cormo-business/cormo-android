package com.cormo.neulbeot.page.home.tabs

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.exercise.ExActivity
import com.cormo.neulbeot.page.exercise.activity.jjuka_State_version.JJukaActivity
import com.cormo.neulbeot.page.exercise.activity.squart_version.SquartActivity
import com.cormo.neulbeot.page.home.vm.HomeViewModel
import kotlin.getValue

class ChallengeFragment : Fragment(R.layout.challenge_page) {

    private val vm: HomeViewModel by activityViewModels()

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

//        val showDialog = v.findViewById<TextView>(R.id.game_start1)
        val btnGameStart1 = v.findViewById<TextView>(R.id.game_start1)
        val btnGameStart2 = v.findViewById<TextView>(R.id.game_start2)
        val btnStart = v.findViewById<TextView>(R.id.btn_start)
        val gameStartDialog = v.findViewById<View>(R.id.gameStartDialog)
        val weeklyChallengeCard = v.findViewById<View>(R.id.WeeklyChallengeCard)
        val btnClose = v.findViewById<ImageView>(R.id.btn_close)
        val btnMore = v.findViewById<ImageView>(R.id.btn_plus)
        val progressEXP = v.findViewById<ProgressBar>(R.id.progressToday)
        val energyCoin = v.findViewById<TextView>(R.id.energy_coin)
        val myLevel = v.findViewById<TextView>(R.id.my_level)
        val progressExerciseNum = v.findViewById<ProgressBar>(R.id.progress_today_exercise_num)
        val tvRate = v.findViewById<TextView>(R.id.tvRate)
        val tvDone = v.findViewById<TextView>(R.id.tvDone)

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
        // 7일 연속으로 운동....todo

        vm.levelProgress.observe(viewLifecycleOwner){
            val level = vm.level.value ?: 0
            val p = vm.levelProgress.value ?: 0
            var total = 100+50*(level-1)
            myLevel.text = "LV.${level}"
            progressEXP.progress = (total - p) * 100 / total
            energyCoin.text = "${total-p}/${total}"

        }

        vm.todayRecordNum.observe(viewLifecycleOwner){ num ->
            var rate = 0
            if(num >= 4){
                rate = 100
                tvDone.text = "오늘의 미션 완료"
            }else{
                rate = num * 25
                tvDone.text = "${num}/4 완료"
            }
            progressExerciseNum.progress = rate
            tvRate.text = "진행률 ${rate}%"

        }

        btnGameStart1.setOnClickListener {
//            startActivity(Intent(context, CameraActivity1st::class.java))
            startActivity(Intent(context, JJukaActivity::class.java))
        }

        btnGameStart2.setOnClickListener {
            startActivity(Intent(context, SquartActivity::class.java))
        }

        // 주간 챌린지 카드 열기
        btnMore.setOnClickListener {
            weeklyChallengeCard.visibility = View.VISIBLE
            btnMore.visibility = View. GONE
        }


        // 주간 챌린지 카드 닫기
        btnClose.setOnClickListener {
            weeklyChallengeCard.visibility = View.GONE
            btnMore.visibility = View.VISIBLE
        }

//        // 게임 시작 모달 창 열기
//        showDialog.setOnClickListener {
//            val gameStartDialog = v.findViewById<View>(R.id.gameStartDialog)
//            gameStartDialog.visibility = View.VISIBLE
//        }


        // 게임 시작 버튼
        btnStart.setOnClickListener {
            startActivity(Intent(requireContext(), ExActivity::class.java))
            gameStartDialog.visibility = View.GONE

        }

        // 뒤로가기 버튼
        v.findViewById<TextView>(R.id.btn_no).setOnClickListener {
            gameStartDialog.visibility = View.GONE
        }

    }
}
