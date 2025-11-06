package com.cormo.neulbeot.page.signup_pages

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R

class Step06PhoneVerifyActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnNext: Button
    private lateinit var tvTimer: TextView
    private lateinit var tvResend: TextView
    private lateinit var boxes: List<EditText>

    private var timer: CountDownTimer? = null
    private val totalMillis = 10 * 60 * 1000L // 10분

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step06_verify)

        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)
        tvTimer = findViewById(R.id.tvTimer)
        tvResend = findViewById(R.id.tvResend)

        boxes = listOf(
            findViewById(R.id.etCode0),
            findViewById(R.id.etCode1),
            findViewById(R.id.etCode2),
            findViewById(R.id.etCode3)
        )

        btnBack.setOnClickListener { finish() }

        // 각 칸: 숫자 1글자, 자동 이동/역이동
        boxes.forEachIndexed { index, et ->
            et.filters = arrayOf(InputFilter.LengthFilter(1))
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val text = s?.toString().orEmpty().filter { it.isDigit() }
                    if (text != s.toString()) {
                        et.setText(text)
                        et.setSelection(text.length)
                    }
                    if (text.length == 1 && index < boxes.lastIndex) {
                        boxes[index + 1].requestFocus()
                    }
                    updateNextEnabled()
                }
            })
            // 백스페이스로 이전 칸 이동
            et.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (et.text.isEmpty() && index > 0) {
                        boxes[index - 1].requestFocus()
                        boxes[index - 1].setSelection(boxes[index - 1].text.length)
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }

        btnNext.setOnClickListener {
            // 여기서 실제 서버 검증 호출하면 됨
            startActivity(Intent(this, Step07PasswordActivity::class.java))
        }

        tvResend.setOnClickListener {
            // 서버에 재전송 요청 로직 추가 가능
            restartTimer()
            clearAll()
            boxes.first().requestFocus()
        }

        restartTimer()
        updateNextEnabled()
    }

    private fun restartTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(totalMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val m = (millisUntilFinished / 1000L) / 60
                val s = (millisUntilFinished / 1000L) % 60
                tvTimer.text = String.format("%d:%02d", m, s)
            }
            override fun onFinish() {
                tvTimer.text = "0:00"
                // 타이머 종료 시 버튼을 비활성화하거나 안내를 띄우고 싶다면 여기서 처리
            }
        }.start()
    }

    private fun clearAll() {
        boxes.forEach { it.setText("") }
    }

    private fun updateNextEnabled() {
        val complete = boxes.all { it.text.length == 1 }
        btnNext.isEnabled = complete
        btnNext.setBackgroundColor(if (complete) 0xFF00C8E5.toInt() else 0xFFB3E5FC.toInt())
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
