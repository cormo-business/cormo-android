package com.cormo.neulbeot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cormo.neulbeot.page.login.LoginMethodActivity
import com.cormo.neulbeot.page.signup_pages.Step01MethodActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.startButton)

        // 버튼 클릭 시 LoginMethodActivity로 이동
        startButton.setOnClickListener {
            val intent = Intent(this, LoginMethodActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 눌렀을 때 색 진하게 변경
        startButton.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN ->
                    v.setBackgroundColor(Color.parseColor("#0097A7"))
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL ->
                    v.setBackgroundColor(Color.parseColor("#00B8D4"))
            }
            false
        }
    }
}