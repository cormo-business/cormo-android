package com.cormo.neulbeot.page.signup_pages

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.login.LoginMethodActivity

class Step01MethodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step01_method)

        val btnJoinPhone = findViewById<LinearLayout>(R.id.btnJoinPhone)
        val btnJoinNaver = findViewById<LinearLayout>(R.id.btnJoinNaver)
        val btnJoinKakao = findViewById<LinearLayout>(R.id.btnJoinKakao)
        val textLoginLink = findViewById<TextView>(R.id.textLoginLink)

        // 휴대폰 가입 → Step02TermsActivity 이동
        btnJoinPhone.setOnClickListener {
            val intent = Intent(this, Step02TermsActivity::class.java)
            startActivity(intent)
        }

        // 네이버, 카카오 클릭은 아직 구현 안됨
        btnJoinNaver.setOnClickListener {
            // TODO: 네이버 로그인 로직
        }

        btnJoinKakao.setOnClickListener {
            // TODO: 카카오 로그인 로직
        }

        // 로그인으로 이동
        textLoginLink.setOnClickListener {
            val intent = Intent(this, LoginMethodActivity::class.java)
            startActivity(intent)
        }
    }
}
