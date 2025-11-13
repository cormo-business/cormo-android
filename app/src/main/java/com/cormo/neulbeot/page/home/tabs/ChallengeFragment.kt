package com.cormo.neulbeot.page.home.tabs

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.exercise.ExActivity

class ChallengeFragment : Fragment(R.layout.challenge_page) {

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val showDialog = v.findViewById<TextView>(R.id.game_start)
        val btnStart = v.findViewById<TextView>(R.id.btn_start)
        val gameStartDialog = v.findViewById<View>(R.id.gameStartDialog)

        showDialog.setOnClickListener {
            val gameStartDialog = v.findViewById<View>(R.id.gameStartDialog)
            gameStartDialog.visibility = View.VISIBLE
        }


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
