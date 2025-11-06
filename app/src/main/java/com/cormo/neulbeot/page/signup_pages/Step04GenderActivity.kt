package com.cormo.neulbeot.page.signup_pages


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R

class Step04GenderActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnFemale: LinearLayout
    private lateinit var btnMale: LinearLayout
    private lateinit var tvFemale: TextView
    private lateinit var tvMale: TextView
    private lateinit var btnNext: Button

    private var selectedLabel: String? = null  // "여성" or "남성"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step04_gender)

        btnBack = findViewById(R.id.btnBack)
        btnFemale = findViewById(R.id.btnFemale)
        btnMale = findViewById(R.id.btnMale)
        tvFemale = findViewById(R.id.tvFemale)
        tvMale = findViewById(R.id.tvMale)
        btnNext = findViewById(R.id.btnNext)

        btnBack.setOnClickListener { finish() }

        btnFemale.setOnClickListener { select("여성") }
        btnMale.setOnClickListener { select("남성") }

        btnNext.setOnClickListener {
            val code = toGenderCode(selectedLabel!!)
            // Flutter의 provider 대응: 임시 스토어에 저장 (원하면 ViewModel로 교체)
            SignupFormStore.gender = code

            startActivity(Intent(this, Step05PhoneActivity::class.java))
        }

        updateNextEnabled()
    }

    private fun select(label: String) {
        selectedLabel = label

        val selectedBg = R.drawable.bg_gender_selected
        val unselectedBg = R.drawable.bg_gender_unselected

        val white = 0xFFFFFFFF.toInt()
        val black = 0xFF000000.toInt()

        // 배경/글자색 토글
        if (label == "여성") {
            btnFemale.setBackgroundResource(selectedBg)
            tvFemale.setTextColor(white)

            btnMale.setBackgroundResource(unselectedBg)
            tvMale.setTextColor(black)
        } else {
            btnMale.setBackgroundResource(selectedBg)
            tvMale.setTextColor(white)

            btnFemale.setBackgroundResource(unselectedBg)
            tvFemale.setTextColor(black)
        }

        updateNextEnabled()
    }

    private fun updateNextEnabled() {
        val enabled = selectedLabel != null
        btnNext.isEnabled = enabled
        btnNext.setBackgroundColor(if (enabled) 0xFF00C8E5.toInt() else 0xFFB3E5FC.toInt())
    }

    // 라벨 -> 서버 전송용 코드 매핑
    private fun toGenderCode(label: String): String = when (label) {
        "여성" -> "FEMALE"
        "남성" -> "MALE"
        else -> "OTHER"
    }
}
