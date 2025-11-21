package com.cormo.neulbeot.page.exercise

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.exercise.vm.ExerciseViewModel
import kotlin.getValue

class ExEndActivity: AppCompatActivity(){
    private val vm: ExerciseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_end_fragment)

        val btnBackHome = findViewById<TextView>(R.id.btn_back_home)
        val btnFeedBack = findViewById<TextView>(R.id.btn_go_feedback)

        btnFeedBack.setOnClickListener {
            Toast.makeText(this, "잘했어 굿굿", Toast.LENGTH_SHORT).show()

        }

        btnBackHome.setOnClickListener {
            finish() // ExActivity 자체를 사라지게 함
        }

        // API 통신
        vm.save()

        vm.memberId.observe(this){ memberId ->
            Toast.makeText(this, "기록 저장 성공!!\n당신의 ID는 ${memberId}입니다.", Toast.LENGTH_SHORT).show()
        }
    }

}