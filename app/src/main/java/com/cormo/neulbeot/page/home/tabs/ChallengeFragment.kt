package com.cormo.neulbeot.page.home.tabs

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.exercise.ExActivity
import com.cormo.neulbeot.page.exercise.activity.jjuka_State_version.JJukaActivity
import com.cormo.neulbeot.page.exercise.activity.squart_version.SquartActivity

class ChallengeFragment : Fragment(R.layout.challenge_page) {

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
