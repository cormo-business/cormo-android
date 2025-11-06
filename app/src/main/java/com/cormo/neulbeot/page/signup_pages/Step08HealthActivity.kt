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
import org.json.JSONArray
import org.json.JSONObject
import kotlin.jvm.java

class Step08HealthActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var chipGroupDiseases: ChipGroup
    private lateinit var chipGroupPains: ChipGroup
    private lateinit var btnNext: Button

    // 옵션
    private val diseaseOptions = listOf("당뇨", "고혈압", "없음")
    private val painOptions = listOf("목", "어깨", "등", "손가락", "손목", "팔꿈치", "골반", "무릎", "발목", "발가락", "없음")

    // 라벨 -> 코드
    private val diseaseCodeMap = mapOf(
        "당뇨" to "DIABETES",
        "고혈압" to "HYPERTENSION",
        "없음" to "NONE",
    )
    private val painCodeMap = mapOf(
        "목" to "NECK",
        "어깨" to "SHOULDER",
        "등" to "BACK",
        "손가락" to "FINGER",
        "손목" to "WRIST",
        "팔꿈치" to "ELBOW",
        "골반" to "PELVIS",
        "무릎" to "KNEE",
        "발목" to "ANKLE",
        "발가락" to "TOE",
        "없음" to "NONE",
    )

    // 선택 상태
    private val selectedDiseases = linkedSetOf<String>()
    private val selectedPains = linkedSetOf<String>()

    private val activeColor = Color.parseColor("#00C8E5")
    private val inactiveStroke = Color.parseColor("#42000000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step08_health)

        btnBack = findViewById(R.id.btnBack)
        chipGroupDiseases = findViewById(R.id.chipGroupDiseases)
        chipGroupPains = findViewById(R.id.chipGroupPains)
        btnNext = findViewById(R.id.btnNext)

        btnBack.setOnClickListener { finish() }

        // Chip 생성
        addChips(chipGroupDiseases, diseaseOptions, isDisease = true)
        addChips(chipGroupPains, painOptions, isDisease = false)

        btnNext.setOnClickListener {
            // 선택을 코드로 변환해 JSON 문자열 생성
            val diseaseCodes = JSONArray(selectedDiseases.map { diseaseCodeMap[it] ?: it })
            val painCodes = JSONArray(selectedPains.map { painCodeMap[it] ?: it })

            val payload = JSONObject().apply {
                put("diseases", diseaseCodes)
                put("pains", painCodes)
            }.toString()

            // 저장 (Flutter의 provider.setHealth 대응)
            SignupFormStore.health = payload

            // 다음 화면
            startActivity(Intent(this, Step09ExerciseActivity::class.java))
        }

        updateNextEnabled()
    }

    /** ChipGroup에 라벨 목록을 추가 */
    private fun addChips(group: ChipGroup, labels: List<String>, isDisease: Boolean) {
        labels.forEach { label ->
            group.addView(createChoiceChip(label) { checked ->
                onChipToggled(group, label, checked, isDisease)
            })
        }
    }

    /** ChoiceChip 스타일 생성 (선택 시 컬러 적용) */
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
                ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            ).apply {
                rightMargin = (12 * resources.displayMetrics.density).toInt()
                bottomMargin = (12 * resources.displayMetrics.density).toInt()
            }

            setOnCheckedChangeListener { _, checked ->
                // 선택/해제 시 색상 토글
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

    /** Chip 토글 로직 (“없음” 처리 + 선택 집합 관리) */
    private fun onChipToggled(group: ChipGroup, label: String, checked: Boolean, isDisease: Boolean) {
        val set = if (isDisease) selectedDiseases else selectedPains

        if (label == "없음") {
            // 없음 선택 시: 다른 선택 모두 해제하고 "없음"만 유지
            if (checked) {
                set.clear()
                set.add("없음")
                // 그룹의 다른 칩들 해제
                forEachChip(group) { chip ->
                    if (chip.text.toString() != "없음" && chip.isChecked) chip.isChecked = false
                }
            } else {
                set.remove("없음")
            }
        } else {
            // 다른 항목 선택 시: "없음" 해제
            if (checked) {
                set.remove("없음")
                set.add(label)
                // 그룹에서 "없음" 칩 강제 해제
                forEachChip(group) { chip ->
                    if (chip.text.toString() == "없음" && chip.isChecked) chip.isChecked = false
                }
            } else {
                set.remove(label)
            }
        }

        updateNextEnabled()
    }

    private fun forEachChip(group: ChipGroup, block: (Chip) -> Unit) {
        for (i in 0 until group.childCount) {
            (group.getChildAt(i) as? Chip)?.let(block)
        }
    }

    private fun updateNextEnabled() {
        val enabled = selectedDiseases.isNotEmpty() || selectedPains.isNotEmpty()
        btnNext.isEnabled = enabled
        btnNext.setBackgroundColor(if (enabled) activeColor else Color.parseColor("#B3E5FC"))
    }
}
