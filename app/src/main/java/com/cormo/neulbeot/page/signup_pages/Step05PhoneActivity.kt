package com.cormo.neulbeot.page.signup_pages


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.login.LoginMethodActivity
import com.cormo.neulbeot.page.login.LoginPasswordActivity
import com.cormo.neulbeot.page.login.onlyDigits
import com.cormo.neulbeot.page.login.view_model.CheckPhoneViewModel
import com.cormo.neulbeot.page.signup_pages.api.SignupService
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class Step05PhoneActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageButton

    private val vm: CheckPhoneViewModel by viewModels()

    private var selfChanging = false  // 포맷팅 시 재귀 방지

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_pages_activity_step05_phone)

        etPhone = findViewById(R.id.etPhone)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // 입력 변화를 듣고 자동 포맷 + 버튼 활성화 갱신
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (selfChanging) return
                val raw = s?.toString().orEmpty()
                val formatted = formatPhone(raw)
                if (raw != formatted) {
                    selfChanging = true
                    etPhone.setText(formatted)
                    etPhone.setSelection(formatted.length)
                    selfChanging = false
                }
                updateNextEnabled()
            }
        })


        // 키보드 완료(엔터) 눌렀을 때 검증
        etPhone.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!isValidPhone(etPhone.text.toString())) {
                    showSnack("올바른 휴대폰 번호(010으로 시작, 11자리)를 입력해주세요.")
                }

                true
            } else false
        }

        btnNext.setOnClickListener {
            val entered = etPhone.text.toString()
            if (!isValidPhone(entered)) {
                showSnack("올바른 휴대폰 번호(010으로 시작, 11자리)를 입력해주세요.")
                return@setOnClickListener
            }

            val digits = etPhone.text.toString().onlyDigits()
            vm.checkRegistered(digits)

            // 숫자만 추출해 username으로 저장
            SignupFormStore.username = digitsOnly(entered)

            // 다음 단계로 이동 (인증 페이지)
//            startActivity(Intent(this, Step06PhoneVerifyActivity::class.java))
        }

        // 로그인 여부
        vm.registered.observe(this) { registered ->
            val digits = etPhone.text.toString().onlyDigits()
            when (registered) {
                false -> {

                    startActivity(
                        Intent(this, Step06PhoneVerifyActivity::class.java)
                            .putExtra(LoginPasswordActivity.EXTRA_USERNAME, digits)
                    )
//                    val username = SignupFormStore.username.orEmpty()

//                    SignupService.sendSms(this,username){
//                        ok, error ->
//                        if(ok){
//                            Toast.makeText(this, "문자가 전송되었습니다", Toast.LENGTH_SHORT).show()
//                            startActivity(
//                                Intent(this, Step06PhoneVerifyActivity::class.java)
//                                    .putExtra(LoginPasswordActivity.EXTRA_USERNAME, digits)
//                            )
//                            finish()
//                        }else{
//                            Toast.makeText(this, "휴대폰 인증 실패: $error", Toast.LENGTH_LONG).show()
//
//                        }
//                    }
                }
                true -> {
                    showSignupPrompt {
                        startActivity(Intent(this, LoginMethodActivity::class.java))
                    }
                }
                null -> toast("서버 통신 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
        }

        updateNextEnabled()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    private fun showSignupPrompt(onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage("이미 가입되어있습니다")
            .setCancelable(false)
            .setPositiveButton("로그인 하러가기") { d, _ -> d.dismiss(); onConfirm() }
            .setNegativeButton("닫기") { d, _ -> d.dismiss() }
            .show()
    }

    /** 숫자만 남기기 */
    private fun digitsOnly(input: String): String = input.filter { it.isDigit() }.take(11)

    /** "01012345678" -> "010-1234-5678" (11자 초과는 잘라냄) */
    private fun formatPhone(input: String): String {
        val digits = digitsOnly(input)
        return when {
            digits.length >= 8 -> "${digits.substring(0,3)}-${digits.substring(3,7)}-${digits.substring(7)}"
            digits.length >= 4 -> "${digits.substring(0,3)}-${digits.substring(3)}"
            else -> digits
        }
    }

    /** 유효성: 010으로 시작 & 총 11자리 */
    private fun isValidPhone(input: String): Boolean {
        val d = digitsOnly(input)
        return Regex("^010\\d{8}$").matches(d)
    }

    private fun updateNextEnabled() {
        val enabled = isValidPhone(etPhone.text.toString())
        btnNext.isEnabled = enabled
        btnNext.setBackgroundColor(if (enabled) 0xFF00C8E5.toInt() else 0xFFB3E5FC.toInt())
    }

    private fun showSnack(msg: String) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show()
    }
}
