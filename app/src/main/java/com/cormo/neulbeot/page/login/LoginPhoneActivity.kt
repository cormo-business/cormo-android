package com.cormo.neulbeot.page.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cormo.neulbeot.R
import com.cormo.neulbeot.api.member.MemberService
import com.cormo.neulbeot.page.signup_pages.Step01MethodActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPhoneActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var etPhone: EditText
    private lateinit var btnLogin: Button
    private lateinit var progress: ProgressBar
    private lateinit var hintText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_login_phone) // 아래 XML 참고

        btnBack = findViewById(R.id.btnBack)
        etPhone = findViewById(R.id.etPhone)
        btnLogin = findViewById(R.id.btnLogin)
        progress = findViewById(R.id.progress)
        hintText = findViewById(R.id.hintText)

        btnBack.setOnClickListener { finish() }

        // 숫자만 입력(최대 13자: "010-1234-5678" 길이 맞춤)
        etPhone.filters = arrayOf(InputFilter.LengthFilter(13))

        // 입력 변화 → 자동 포맷 & 버튼 활성화
        etPhone.addTextChangedListener(object : TextWatcher {
            private var editing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (editing) return
                editing = true
                val formatted = formatPhone(s?.toString().orEmpty())
                if (formatted != s.toString()) {
                    etPhone.setText(formatted)
                    etPhone.setSelection(formatted.length)
                }
                btnLogin.isEnabled = isValid()
                editing = false
            }
        })

        btnLogin.setOnClickListener { checkAndNext() }

        // 초기 상태
        progress.visibility = View.GONE
        btnLogin.isEnabled = isValid()
        hintText.text = "로그인을 누르면 비밀번호 확인이 진행됩니다."
    }

    /** "010-1234-5678" 형태로 포맷 */
    private fun formatPhone(input: String): String {
        var digits = input.replace(Regex("[^0-9]"), "")
        if (digits.length > 11) digits = digits.substring(0, 11)

        return when {
            digits.length >= 8 -> "${digits.substring(0,3)}-${digits.substring(3,7)}-${digits.substring(7)}"
            digits.length >= 4 -> "${digits.substring(0,3)}-${digits.substring(3)}"
            else -> digits
        }
    }

    /** 유효성: 010 + 8자리 = 총 11자리 */
    private fun isValid(): Boolean {
        val digits = etPhone.text.toString().replace(Regex("[^0-9]"), "")
        return Regex("^010\\d{8}$").matches(digits)
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun checkAndNext() {
        if (!isValid()) {
            toast("올바른 휴대폰 번호(010으로 시작, 11자리)를 입력해주세요.")
            return
        }
        val digits = etPhone.text.toString().replace(Regex("[^0-9]"), "")

        // 로딩 on
        btnLogin.isEnabled = false
        progress.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            val isRegistered = try {
                MemberService.isRegistered(this@LoginPhoneActivity, digits)
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    toast("서버 통신 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
                }
                false
            }

            withContext(Dispatchers.Main) {
                // 로딩 off
                btnLogin.isEnabled = true
                progress.visibility = View.GONE

                if (isRegistered) {
                    // 가입됨 → 비밀번호 입력 화면으로
                    startActivity(
                        Intent(this@LoginPhoneActivity, LoginPasswordActivity::class.java)
                            .putExtra(LoginPasswordActivity.EXTRA_USERNAME, digits)
                    )
                } else {
                    // 미가입 → 회원가입 유도 팝업
                    showSignupPrompt(
                        onConfirm = {
                            startActivity(
                                Intent(this@LoginPhoneActivity, Step01MethodActivity::class.java)
                            )
                        }
                    )
                }
            }
        }
    }

    /** 미가입 팝업 (Flutter의 SignupPromptDialog 대응) */
    private fun showSignupPrompt(onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage("가입되지 않은 전화번호입니다.\n회원가입 하시겠습니까?")
            .setCancelable(false)
            .setPositiveButton("회원가입 하기") { d, _ ->
                d.dismiss()
                onConfirm()
            }
            .setNegativeButton("닫기") { d, _ -> d.dismiss() }
            .show()
    }
}
