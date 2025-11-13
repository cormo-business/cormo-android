package com.cormo.neulbeot.page.exercise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R

class ExStartFragment: Fragment(R.layout.activity_start_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.bottom_bar_bg)
        val btnStartOK = view.findViewById<TextView>(R.id.btn_start_ok)
        val btnDone = view.findViewById<ImageView>(R.id.btn_done)
        val txtNoti = view.findViewById<TextView>(R.id.txt_noti)
        val person = view.findViewById<ImageView>(R.id.person)
        val startImg = view.findViewById<ConstraintLayout>(R.id.start_img)

        btnStartOK.setOnClickListener { it ->
            startImg.visibility = View.GONE // 숨김
            txtNoti.visibility = View.VISIBLE // 글자 보이게
            person.visibility = View.VISIBLE
        }

        btnDone.setOnClickListener { it ->
            // 1) 스택에서 빼기
            parentFragmentManager.popBackStack()
            // 2) 다시 FirstFragment를 replace로 올리기 (새로 생성)
             parentFragmentManager.beginTransaction()
                 .replace(R.id.activity_main_frame, ExEndFragment())
                 .commit()
        }
    }
}