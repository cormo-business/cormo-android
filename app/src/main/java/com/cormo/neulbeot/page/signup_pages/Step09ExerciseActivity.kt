package com.cormo.neulbeot.page.signup_pages

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONObject

class Step09ExerciseActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnNext: Button

    private lateinit var chipsNone: ChipGroup
    private lateinit var chipsBall: ChipGroup
    private lateinit var chipsCombat: ChipGroup
    private lateinit var chipsFitness: ChipGroup
    private lateinit var chipsSwimSkate: ChipGroup
    private lateinit var chipsTrackCycle: ChipGroup
    private lateinit var chipsLeisureEtc: ChipGroup
    private lateinit var etOther: EditText

    // 라벨 -> 코드
    private val codeMap = mapOf(
        "축구" to "SOCCER", "농구" to "BASKETBALL", "배구" to "VOLLEYBALL", "야구" to "BASEBALL",
        "배드민턴" to "BADMINTON", "테니스" to "TENNIS", "탁구" to "TABLE_TENNIS",
        "태권도" to "TAEKWONDO", "유도" to "JUDO", "복싱" to "BOXING", "합기도" to "HAPKIDO", "검도" to "KENDO",
        "헬스" to "FITNESS", "필라테스" to "PILATES", "요가" to "YOGA", "에어로빅" to "AEROBICS",
        "수영" to "SWIMMING", "스케이트" to "SKATING",
        "육상" to "ATHLETICS", "사이클" to "CYCLING",
        "볼링" to "BOWLING", "골프" to "GOLF",
        "기타" to "OTHER", "없음" to "NONE"
    )

    // 카테고리
    private val ballSports = listOf("축구", "농구", "배구", "야구", "배드민턴", "테니스", "탁구")
    private val combatSports = listOf("태권도", "유도", "복싱", "합기도", "검도")
    private val fitnessSports = listOf("헬스", "필라테스", "요가", "에어로빅")
    private val swimSkate = listOf("수영", "스케이트")
    private val trackCycle = listOf("육상", "사이클")
    private val leisureEtc = listOf("볼링", "골프", "기타")

    // 선택 상태
    private val selected = linkedSetOf<String>()  // 라벨 저장

    private val activeColor = Color.parseColor("#00C8E5")
    private val inactiveStroke = Color.parseColor("#42000000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step09_exercise)

        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)

        chipsNone = findViewById(R.id.chipsNone)
        chipsBall = findViewById(R.id.chipsBall)
        chipsCombat = findViewById(R.id.chipsCombat)
        chipsFitness = findViewById(R.id.chipsFitness)
        chipsSwimSkate = findViewById(R.id.chipsSwimSkate)
        chipsTrackCycle = findViewById(R.id.chipsTrackCycle)
        chipsLeisureEtc = findViewById(R.id.chipsLeisureEtc)
        etOther = findViewById(R.id.etOther)

        btnBack.setOnClickListener { finish() }

        // 칩 구성
        addChips(chipsNone, listOf("없음"))
        addChips(chipsBall, ballSports)
        addChips(chipsCombat, combatSports)
        addChips(chipsFitness, fitnessSports)
        addChips(chipsSwimSkate, swimSkate)
        addChips(chipsTrackCycle, trackCycle)
        addChips(chipsLeisureEtc, leisureEtc)

        // 기타 입력 변화 감지
        etOther.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { updateNextEnabled() }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnNext.setOnClickListener {
            val codes = selected.map { codeMap[it] ?: it }
            val payload = JSONObject()
                .put("exercises", codes)
                .put("other", etOther.text.toString().trim())
                .toString()

            // 저장 (Flutter provider.setExercise 대응)
            SignupFormStore.exercise = payload

            startActivity(Intent(this, Step10JobActivity::class.java))
        }

        updateNextEnabled()
    }

    /** ChipGroup에 라벨 목록을 칩으로 추가 */
    private fun addChips(group: ChipGroup, labels: List<String>) {
        labels.forEach { label ->
            group.addView(createChoiceChip(label) { checked ->
                onChipToggled(label, checked)
            })
        }
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
                // UI 색상 토글
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

    /** 선택 토글 규칙 반영 */
    private fun onChipToggled(label: String, checked: Boolean) {
        if (label == "없음") {
            // '없음'은 단독: 선택 시 나머지 모두 해제
            if (checked) {
                selected.clear()
                selected.add("없음")
                uncheckAllExceptNone()
                hideAndClearOther()
            } else {
                selected.remove("없음")
            }
            updateNextEnabled()
            return
        }

        // 다른 항목 선택 시 '없음' 해제
        if (checked) selected.remove("없음")

        if (checked) selected.add(label) else selected.remove(label)

        // '기타' 선택/해제에 따른 입력 필드
        if (label == "기타") {
            if (checked) {
                etOther.visibility = android.view.View.VISIBLE
            } else {
                hideAndClearOther()
            }
        }

        // '없음' 칩 해제 (보호)
        setCheckedOnGroup(chipsNone, "없음", false)

        updateNextEnabled()
    }

    /** 그룹의 특정 라벨 칩 체크 상태 설정 */
    private fun setCheckedOnGroup(group: ChipGroup, label: String, value: Boolean) {
        for (i in 0 until group.childCount) {
            val chip = group.getChildAt(i) as? Chip ?: continue
            if (chip.text.toString() == label && chip.isChecked != value) {
                chip.isChecked = value
            }
        }
    }

    /** '없음'을 제외한 모든 칩 해제 */
    private fun uncheckAllExceptNone() {
        listOf(chipsBall, chipsCombat, chipsFitness, chipsSwimSkate, chipsTrackCycle, chipsLeisureEtc).forEach { g ->
            for (i in 0 until g.childCount) (g.getChildAt(i) as? Chip)?.isChecked = false
        }
        // none 그룹에서도 '없음'만 유지
        setCheckedOnGroup(chipsNone, "없음", true)
    }

    private fun hideAndClearOther() {
        etOther.setText("")
        etOther.visibility = android.view.View.GONE
    }

    /** 버튼 활성화 규칙:
     * 최소 1개 선택 &&
     * '기타'를 포함하면 etOther 비어있지 않아야 함
     */
    private fun updateNextEnabled() {
        val hasSelection = selected.isNotEmpty()
        val needsOther = selected.contains("기타")
        val ok = hasSelection && (!needsOther || etOther.text.toString().trim().isNotEmpty())
        btnNext.isEnabled = ok
        btnNext.setBackgroundColor(if (ok) activeColor else Color.parseColor("#B3E5FC"))
    }
}
