package com.cormo.neulbeot.page.signup_pages


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Step07PasswordActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirm: TextInputEditText
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step07_password)

        btnBack = findViewById(R.id.btnBack)
        etPassword = findViewById(R.id.etPassword)
        etConfirm = findViewById(R.id.etConfirm)
        btnNext = findViewById(R.id.btnNext)

        btnBack.setOnClickListener { finish() }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { updateNextEnabled() }
        }
        etPassword.addTextChangedListener(watcher)
        etConfirm.addTextChangedListener(watcher)

        btnNext.setOnClickListener {
            val pass = etPassword.text?.toString().orEmpty()
            val confirm = etConfirm.text?.toString().orEmpty()

            if (pass != confirm) {
                showMismatchDialog()
                return@setOnClickListener
            }

            // 저장 (Flutter의 provider.setPassword 대응)
            SignupFormStore.password = pass

            // 다음 단계로 이동
            startActivity(Intent(this, Step08HealthActivity::class.java))
        }

        updateNextEnabled()
    }

    private fun updateNextEnabled() {
        val p = etPassword.text?.length ?: 0
        val c = etConfirm.text?.length ?: 0
        val canProceed = p >= 6 && c >= 6
        btnNext.isEnabled = canProceed
        btnNext.setBackgroundColor(if (canProceed) 0xFF00C8E5.toInt() else 0xFFB3E5FC.toInt())
    }

    private fun showMismatchDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage("비밀번호가 일치하지 않습니다.\n다시 입력해주세요.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .setCancelable(true)
            .show()
    }
}
