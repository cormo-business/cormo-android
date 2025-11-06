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

class Step11SedentaryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnNext: Button
    private lateinit var chipGroupTime: ChipGroup

    // 라벨 목록 (인덱스 0부터 저장)
    private val timeOptions = listOf(
        "1시간 미만",
        "1시간~2시간",
        "2시간~3시간",
        "3시간~4시간",
        "4시간~5시간",
        "5시간~6시간",
        "6시간~7시간",
        "7시간 이상"
    )

    private var selectedIndex: Int? = null

    private val activeColor = Color.parseColor("#00C8E5")
    private val inactiveStroke = Color.parseColor("#42000000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step11_sedentary)

        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)
        chipGroupTime = findViewById(R.id.chipGroupTime)

        btnBack.setOnClickListener { finish() }

        // 칩 생성
        timeOptions.forEachIndexed { index, label ->
            chipGroupTime.addView(createChoiceChip(label) { checked ->
                if (checked) {
                    selectedIndex = index
                    // 단일 선택이므로 나머지 색상 정리
                    applyChipColors()
                } else if (selectedIndex == index) {
                    selectedIndex = null
                }
                updateNextEnabled()
            })
        }

        btnNext.setOnClickListener {
            val idx = selectedIndex ?: return@setOnClickListener
            // 0부터 시작하는 정수로 저장
            SignupFormStore.sedentary = idx
            // 다음 단계
            startActivity(Intent(this, Step12NicknameActivity::class.java))
        }

        updateNextEnabled()
    }

    /** 공통 칩 스타일 */
    private fun createChoiceChip(label: String, onCheckedChange: (Boolean) -> Unit): Chip {
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

    /** 선택된 칩 하나만 강조되도록 색상 정리 */
    private fun applyChipColors() {
        for (i in 0 until chipGroupTime.childCount) {
            val chip = chipGroupTime.getChildAt(i) as? Chip ?: continue
            val isSelected = i == selectedIndex
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
        val enabled = selectedIndex != null
        btnNext.isEnabled = enabled
        btnNext.setBackgroundColor(if (enabled) activeColor else Color.parseColor("#B3E5FC"))
    }
}
