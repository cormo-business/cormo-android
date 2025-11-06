package com.cormo.neulbeot.page.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.home.HomeActivity

class LoginWelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LoginWelcome", "onCreate()")
        setContentView(R.layout.login_activity_login_welcome)

        val accessToken = intent.getStringExtra("accessToken") ?: "사용자"
        val ivProfile: ImageView = findViewById(R.id.ivProfile)
        val tvWelcome: TextView = findViewById(R.id.tvWelcome)
        val btnStart: Button = findViewById(R.id.btnStart)

        ivProfile.setImageResource(R.drawable.ic_account_circle_96)
        tvWelcome.text = "다시 돌아와주셨군요,\n${accessToken}님!"

        btnStart.setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
        }
    }
}
