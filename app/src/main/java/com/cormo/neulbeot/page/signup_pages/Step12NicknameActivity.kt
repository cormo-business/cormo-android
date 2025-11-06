package com.cormo.neulbeot.page.signup_pages

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R

class Step12NicknameActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var etNickname: EditText
    private lateinit var tvCount: TextView
    private lateinit var btnNext: Button

    private val activeColor = 0xFF00C8E5.toInt()
    private val inactiveColor = 0xFFB3E5FC.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step12_nickname)

        btnBack = findViewById(R.id.btnBack)
        etNickname = findViewById(R.id.etNickname)
        tvCount = findViewById(R.id.tvCount)
        btnNext = findViewById(R.id.btnNext)

        btnBack.setOnClickListener { finish() }

        etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val len = s?.toString()?.trim()?.length ?: 0
                tvCount.text = "$len/8"
                updateNextEnabled()
            }
        })

        btnNext.setOnClickListener {
            val nick = etNickname.text.toString().trim()
            // 저장 (Flutter의 signupFormProvider.setNickName 대응)
            SignupFormStore.nickName = nick
            // 다음 단계
            startActivity(Intent(this, Step13ProfilePhotoActivity::class.java))
        }

        updateNextEnabled()
    }

    private fun updateNextEnabled() {
        val filled = etNickname.text.toString().trim().isNotEmpty()
        btnNext.isEnabled = filled
        btnNext.setBackgroundColor(if (filled) activeColor else inactiveColor)
    }
}
