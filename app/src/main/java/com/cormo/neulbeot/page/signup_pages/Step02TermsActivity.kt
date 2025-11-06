package com.cormo.neulbeot.page.signup_pages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cormo.neulbeot.R

class Step02TermsActivity : AppCompatActivity() {

    private var allAgree = false
    private var agreeTerms = false
    private var agreePrivacy = false

    private lateinit var layoutAllAgree: LinearLayout
    private lateinit var iconAllAgree: ImageView
    private lateinit var cbTerms: CheckBox
    private lateinit var cbPrivacy: CheckBox
    private lateinit var btnAgree: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step02_terms)

        layoutAllAgree = findViewById(R.id.layoutAllAgree)
        iconAllAgree = findViewById(R.id.iconAllAgree)
        cbTerms = findViewById(R.id.cbTerms)
        cbPrivacy = findViewById(R.id.cbPrivacy)
        btnAgree = findViewById(R.id.btnAgree)
        btnBack = findViewById(R.id.btnBack)

        // 뒤로가기
        btnBack.setOnClickListener { finish() }

        // 전체 동의 토글
        layoutAllAgree.setOnClickListener {
            allAgree = !allAgree
            agreeTerms = allAgree
            agreePrivacy = allAgree
            applyStateToViews()
        }

        // 개별 체크
        cbTerms.setOnCheckedChangeListener { _, checked ->
            agreeTerms = checked
            syncAllAgreeFromIndividuals()
            applyStateToViews()
        }
        cbPrivacy.setOnCheckedChangeListener { _, checked ->
            agreePrivacy = checked
            syncAllAgreeFromIndividuals()
            applyStateToViews()
        }

        // 동의 버튼
        btnAgree.setOnClickListener {
            // 다음 단계로 이동
            val intent = Intent(this, Step03BirthActivity::class.java)
            startActivity(intent)
        }

        applyStateToViews()
    }

    private fun syncAllAgreeFromIndividuals() {
        allAgree = agreeTerms && agreePrivacy
    }

    private fun applyStateToViews() {
        // 아이콘 바꿔주기
        iconAllAgree.setImageResource(
            if (allAgree) R.drawable.ic_check_circle_24
            else R.drawable.ic_radio_button_unchecked_24
        )

        // 체크박스 동기화 (전체동의 클릭 시)
        if (cbTerms.isChecked != agreeTerms) cbTerms.isChecked = agreeTerms
        if (cbPrivacy.isChecked != agreePrivacy) cbPrivacy.isChecked = agreePrivacy

        // 버튼 활성/비활성 + 배경 색
        val canProceed = agreeTerms && agreePrivacy
        btnAgree.isEnabled = canProceed
        val color = if (canProceed) 0xFF00C8E5.toInt() else 0xFFB3E5FC.toInt()
        btnAgree.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.transparent)
        btnAgree.setBackgroundColor(color)
    }
}
