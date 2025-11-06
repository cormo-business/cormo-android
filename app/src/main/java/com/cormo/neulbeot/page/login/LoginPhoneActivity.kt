package com.cormo.neulbeot.page.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.page.login.view_model.CheckPhoneViewModel
import com.cormo.neulbeot.page.signup_pages.Step01MethodActivity

class LoginPhoneActivity : AppCompatActivity() {

    private val vm: CheckPhoneViewModel by viewModels()
    private lateinit var btnBack: ImageButton
    private lateinit var etPhone: EditText
    private lateinit var btnLogin: Button
    private lateinit var progress: ProgressBar
    private lateinit var hintText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_login_phone)

        btnBack   = findViewById(R.id.btnBack)
        etPhone   = findViewById(R.id.etPhone)
        btnLogin  = findViewById(R.id.btnLogin)
        progress  = findViewById(R.id.progress)
        hintText  = findViewById(R.id.hintText)

        btnBack.setOnClickListener { finish() }

        etPhone.filters = arrayOf(InputFilter.LengthFilter(13))
        etPhone.addTextChangedListener(object : TextWatcher {
            private var editing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (editing) return
                editing = true
                val formatted = PhoneFormatter.format(s?.toString().orEmpty())
                if (formatted != s.toString()) {
                    etPhone.setText(formatted)
                    etPhone.setSelection(formatted.length)
                }
                btnLogin.isEnabled = PhoneFormatter.isValid010(etPhone.text.toString().onlyDigits())
                editing = false
            }
        })

        btnLogin.setOnClickListener {
            val digits = etPhone.text.toString().onlyDigits()
            if (!PhoneFormatter.isValid010(digits)) {
                toast("올바른 휴대폰 번호(010으로 시작, 11자리)를 입력해주세요.")
                return@setOnClickListener
            }
            vm.checkRegistered(digits)
        }

        progress.visibility = View.GONE
        btnLogin.isEnabled = PhoneFormatter.isValid010(etPhone.text.toString().onlyDigits())
        hintText.text = "로그인을 누르면 비밀번호 확인이 진행됩니다."

        // ViewModel 상태 구독
        vm.loading.observe(this) { loading ->
            progress.visibility = if (loading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !loading
        }
        vm.registered.observe(this) { registered ->
            val digits = etPhone.text.toString().onlyDigits()
            when (registered) {
                true -> {
                    startActivity(
                        Intent(this, LoginPasswordActivity::class.java)
                            .putExtra(LoginPasswordActivity.EXTRA_USERNAME, digits)
                    )
                }
                false -> {
                    showSignupPrompt {
                        startActivity(Intent(this, Step01MethodActivity::class.java))
                    }
                }
                null -> toast("서버 통신 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun showSignupPrompt(onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage("가입되지 않은 전화번호입니다.\n회원가입 하시겠습니까?")
            .setCancelable(false)
            .setPositiveButton("회원가입 하기") { d, _ -> d.dismiss(); onConfirm() }
            .setNegativeButton("닫기") { d, _ -> d.dismiss() }
            .show()
    }

    companion object PhoneFormatter {
        fun format(input: String): String {
            var digits = input.onlyDigits()
            if (digits.length > 11) digits = digits.substring(0, 11)

            return when {
                digits.length >= 8 -> "${digits.substring(0,3)}-${digits.substring(3,7)}-${digits.substring(7)}"
                digits.length >= 4 -> "${digits.substring(0,3)}-${digits.substring(3)}"
                else -> digits
            }
        }

        fun isValid010(digitsOnly: String): Boolean =
            Regex("^010\\d{8}$").matches(digitsOnly)
    }


}

fun String.onlyDigits(): String = replace(Regex("[^0-9]"), "")
