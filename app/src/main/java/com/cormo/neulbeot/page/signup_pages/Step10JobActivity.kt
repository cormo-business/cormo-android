package com.cormo.neulbeot.page.signup_pages

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class Step10JobActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnNext: Button
    private lateinit var chipGroupJobs: ChipGroup

    // 화면 라벨
    private val jobOptions = listOf("초등학생", "중학생", "고등학생", "대학생", "직장인", "무직")

    // 라벨 -> 코드
    private val jobCodeMap = mapOf(
        "초등학생" to "ELEMENTARY",
        "중학생" to "MIDDLE",
        "고등학생" to "HIGH",
        "대학생" to "UNIVERSITY",
        "직장인" to "EMPLOYEE",
        "무직" to "UNEMPLOYED"
    )

    private var selectedJob: String? = null

    private val activeColor = Color.parseColor("#00C8E5")
    private val inactiveStroke = Color.parseColor("#42000000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step10_job)

        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)
        chipGroupJobs = findViewById(R.id.chipGroupJobs)

        btnBack.setOnClickListener { finish() }

        // 칩 생성
        jobOptions.forEach { label ->
            chipGroupJobs.addView(createChoiceChip(label) { checked ->
                if (checked) {
                    selectedJob = label
                    // 단일 선택: 다른 칩은 자동 해제되지만 색상도 맞춰주자
                    applyChipColors()
                } else if (selectedJob == label) {
                    selectedJob = null
                }
                updateNextEnabled()
            })
        }

        btnNext.setOnClickListener {
            val label = selectedJob ?: return@setOnClickListener
            val code = jobCodeMap[label] ?: label
            // 저장 (Flutter의 provider.setJob 대응)
            SignupFormStore.job = code
            // 다음 단계
            startActivity(Intent(this, Step11SedentaryActivity::class.java))
        }

        updateNextEnabled()
    }

    /** 칩 공통 스타일 */
    private fun createChoiceChip(
        label: String,
        onCheckedChange: (Boolean) -> Unit
    ): Chip {
        return Chip(this).apply {
            text = label
            isCheckable = true
            isCheckedIconVisible = false
            chipCornerRadius = resources.displayMetrics.density * 8f
            chipStrokeWidth = resources.displayMetrics.density * 1f
            chipStrokeColor = ColorStateList.valueOf(inactiveStroke)
            setTextColor(Color.BLACK)
            chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = (12 * resources.displayMetrics.density).toInt()
                bottomMargin = (12 * resources.displayMetrics.density).toInt()
            }
            setOnCheckedChangeListener { _, checked ->
                // 색상 토글
                if (checked) {
                    chipBackgroundColor = ColorStateList.valueOf(activeColor)
                    setTextColor(Color.WHITE)
                    chipStrokeColor = ColorStateList.valueOf(activeColor)
                } else {
                    chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
                    setTextColor(Color.BLACK)
                    chipStrokeColor = ColorStateList.valueOf(inactiveStroke)
                }
                onCheckedChange(checked)
            }
        }
    }

    /** 현재 선택 상태에 맞춰 칩 색상 정리(단일 선택일 때 보기 좋게) */
    private fun applyChipColors() {
        for (i in 0 until chipGroupJobs.childCount) {
            val chip = chipGroupJobs.getChildAt(i) as? Chip ?: continue
            val isSelected = chip.text.toString() == selectedJob
            chip.isChecked = isSelected
            if (isSelected) {
                chip.chipBackgroundColor = ColorStateList.valueOf(activeColor)
                chip.setTextColor(Color.WHITE)
                chip.chipStrokeColor = ColorStateList.valueOf(activeColor)
            } else {
                chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
                chip.setTextColor(Color.BLACK)
                chip.chipStrokeColor = ColorStateList.valueOf(inactiveStroke)
            }
        }
    }

    private fun updateNextEnabled() {
        val enabled = selectedJob != null
        btnNext.isEnabled = enabled
        btnNext.setBackgroundColor(if (enabled) activeColor else Color.parseColor("#B3E5FC"))
    }
}
