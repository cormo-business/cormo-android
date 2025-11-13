package com.cormo.neulbeot.page.exercise

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cormo.neulbeot.R

class ExEndFragment: Fragment(R.layout.activity_end_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBackHome = view.findViewById<TextView>(R.id.btn_back_home)
        btnBackHome.setOnClickListener {
            // 1) 스택에서 빼기
            parentFragmentManager.popBackStack()

            // 2) 다시 FirstFragment를 replace로 올리기 (새로 생성)
            parentFragmentManager.beginTransaction()
                .replace(R.id.activity_main_frame, ExEndFragment())
                .commit()
        }

    }
}