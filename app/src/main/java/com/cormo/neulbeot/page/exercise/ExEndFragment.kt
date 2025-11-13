package com.cormo.neulbeot.page.exercise

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.exercise.vm.ExerciseViewModel
import org.w3c.dom.Text
import kotlin.getValue

class ExEndFragment: Fragment(R.layout.activity_end_fragment) {
    private val vm: ExerciseViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBackHome = view.findViewById<TextView>(R.id.btn_back_home)
        val btnFeedBack = view.findViewById<TextView>(R.id.btn_go_feedback)

        btnFeedBack.setOnClickListener {
            Toast.makeText(context, "잘했어 굿굿", Toast.LENGTH_SHORT).show()

        }

        btnBackHome.setOnClickListener {
            requireActivity().finish() // ExActivity 자체를 사라지게 함
        }
        
        // 전해받은 값
        val nickname = requireArguments().getString("nickname", "jaewoo")
        val score = requireArguments().getInt("score", 0)
        
        // API 통신
        vm.save(nickname, score)

        vm.memberId.observe(viewLifecycleOwner){ memberId ->
            Toast.makeText(context, "기록 저장 성공!!\n당신의 ID는 ${memberId}입니다.", Toast.LENGTH_SHORT).show()
        }

    }
}