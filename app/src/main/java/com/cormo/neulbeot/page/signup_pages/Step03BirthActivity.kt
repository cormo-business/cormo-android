package com.cormo.neulbeot.page.signup_pages

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.google.android.material.snackbar.Snackbar

class Step03BirthActivity : AppCompatActivity() {

    private lateinit var etYear: EditText
    private lateinit var etMonth: EditText
    private lateinit var etDay: EditText
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step03_birth)

        etYear = findViewById(R.id.etYear)
        etMonth = findViewById(R.id.etMonth)
        etDay = findViewById(R.id.etDay)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // 입력 변화 감지 + 자동 포커스 이동
        etYear.addTextChangedListener(makeWatcher(etYear, 4, next = etMonth))
        etMonth.addTextChangedListener(makeWatcher(etMonth, 2, next = etDay))
        etDay.addTextChangedListener(makeWatcher(etDay, 2, next = null))

        btnNext.setOnClickListener {
            val birth = validateAndFormat()
            if (birth == null) {
                showSnack("올바른 생년월일을 입력해주세요.")
                return@setOnClickListener
            }

            // Flutter의 provider 저장 대응: 간단 스토어에 저장 (필요시 ViewModel로 교체)
            SignupFormStore.birth = birth

            // 다음 페이지로 이동
            startActivity(Intent(this, Step04GenderActivity::class.java))
        }

        updateNextButtonEnabled()
    }

    private fun makeWatcher(field: EditText, maxLen: Int, next: EditText?): TextWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 숫자만 유지
                val clean = s.toString().filter { it.isDigit() }
                if (clean != s.toString()) {
                    field.setText(clean)
                    field.setSelection(clean.length)
                }
                // 자동 포커스 이동
                if (clean.length == maxLen && next != null) next.requestFocus()
                updateNextButtonEnabled()
            }
        }

    private fun updateNextButtonEnabled() {
        val filled = etYear.text.isNotEmpty() &&
                etMonth.text.isNotEmpty() &&
                etDay.text.isNotEmpty()
        btnNext.isEnabled = filled
        btnNext.setBackgroundColor(if (filled) 0xFF00C8E5.toInt() else 0xFFB3E5FC.toInt())
    }

    // yyyy-MM-dd 포맷 검증/반환
    private fun validateAndFormat(): String? {
        val y = etYear.text.toString().toIntOrNull() ?: return null
        val m = etMonth.text.toString().toIntOrNull() ?: return null
        val d = etDay.text.toString().toIntOrNull() ?: return null

        val nowYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        if (y < 1900 || y > nowYear) return null
        if (m !in 1..12) return null

        // 월의 말일 계산 (다음 달 0일 = 이번달 마지막 날)
        val cal = java.util.Calendar.getInstance()
        cal.set(y, m - 1, 1)
        cal.add(java.util.Calendar.MONTH, 1)
        cal.set(java.util.Calendar.DAY_OF_MONTH, 0)
        val maxDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
        if (d !in 1..maxDay) return null

        val mm = m.toString().padStart(2, '0')
        val dd = d.toString().padStart(2, '0')
        return "$y-$mm-$dd"
    }

    private fun showSnack(msg: String) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show()
    }
}
