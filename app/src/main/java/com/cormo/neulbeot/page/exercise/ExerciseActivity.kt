package com.cormo.neulbeot.page.exercise

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cormo.neulbeot.R

class ExerciseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page)

        val btnStartOK = findViewById<TextView>(R.id.btn_start_ok)
        val btnDone = findViewById<ImageView>(R.id.btn_done)
        val txtNoti = findViewById<TextView>(R.id.txt_noti)
        val person = findViewById<ImageView>(R.id.person)
        val startImg = findViewById<ConstraintLayout>(R.id.start_img)
        btnStartOK.setOnClickListener { it ->
            startImg.visibility = View.GONE // 숨김
            txtNoti.visibility = View.VISIBLE // 글자 보이게
            person.visibility = View.VISIBLE
        }
        btnDone.setOnClickListener { it ->
            startActivity(Intent(this, ExerciseEndActivity::class.java))
        }
    }

}