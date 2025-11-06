package com.cormo.neulbeot.page.login

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.signup_pages.Step01MethodActivity

class LoginMethodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_login_method)

        findViewById<LinearLayout>(R.id.btnLoginPhone).setOnClickListener {
            startActivity(Intent(this, LoginPhoneActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnLoginNaver).setOnClickListener {
            // TODO: 네이버 로그인 연동
        }

        findViewById<LinearLayout>(R.id.btnLoginKakao).setOnClickListener {
            // TODO: 카카오 로그인 연동
        }

        findViewById<TextView>(R.id.textSignupLink).setOnClickListener {
            startActivity(Intent(this, Step01MethodActivity::class.java))
        }
    }
}
