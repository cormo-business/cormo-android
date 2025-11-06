package com.cormo.neulbeot.page.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.api.AuthApi
import com.cormo.neulbeot.api.LoginRequest
import com.cormo.neulbeot.auth.TokenStorage
import com.cormo.neulbeot.core.ApiClient
import kotlinx.coroutines.*
import com.cormo.neulbeot.page.login.LoginWelcomeActivity

class LoginPasswordActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var btnTogglePass: ImageButton
    private lateinit var btnNext: Button
    private lateinit var progress: ProgressBar
    private lateinit var tvHint: TextView

    private var obscure = true
    private var submitting = false

    private val uiScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
        ioScope.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_login_password)

        etPassword = findViewById(R.id.etPassword)
        btnTogglePass = findViewById(R.id.btnTogglePass)
        btnNext = findViewById(R.id.btnNext)
        tvHint = findViewById(R.id.tvHint)

        tvHint.text = "로그인을 누르면 비밀번호 확인이 진행됩니다."

        progress = ProgressBar(this).apply {
            isIndeterminate = true
            visibility = View.GONE
        }

        // 비밀번호 보기/가리기
        btnTogglePass.setOnClickListener {
            obscure = !obscure
            val cursor = etPassword.selectionStart
            etPassword.inputType = if (obscure)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            etPassword.setSelection(cursor)
            btnTogglePass.setImageResource(if (obscure) R.drawable.ic_visibility_off else R.drawable.ic_visibility)
        }

        etPassword.addTextChangedListener(simpleWatcher { updateNextEnabled() })

        btnNext.setOnClickListener {
            if (!btnNext.isEnabled || submitting) return@setOnClickListener
            doLogin()
        }

        updateNextEnabled()
    }

    private fun updateNextEnabled() {
        val pass = etPassword.text?.toString()?.trim().orEmpty()
        btnNext.isEnabled = pass.length >= 6
        btnNext.alpha = if (btnNext.isEnabled) 1f else 0.5f
    }

    private fun doLogin() {
        val username = intent.getStringExtra(EXTRA_USERNAME)?.trim().orEmpty()
        val password = etPassword.text?.toString()?.trim().orEmpty()

        if (username.isEmpty()) {
            toast("전화번호가 없습니다. 처음부터 다시 시도해주세요.")
            finish()
            return
        }
        if (password.length < 6) {
            toast("비밀번호는 6자 이상이어야 합니다.")
            return
        }

        submitting = true
        showLoading(true)

        ioScope.launch {
            try {
                val retrofit = ApiClient.retrofit(applicationContext)
                val api = retrofit.create(AuthApi::class.java)

                val res = api.login(LoginRequest(username = username, password = password))

                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) {
                        val access = res.headers()["accessToken"].orEmpty()
                        val refresh = res.headers()["refreshToken"].orEmpty()

                        if (access.isEmpty()) {
                            showLoading(false)
                            submitting = false
                            toast("로그인 실패: accessToken 누락")
                            return@withContext
                        }

                        // 토큰 저장
                        TokenStorage(applicationContext).saveTokens(access, refresh)
                        toast("[성공] 로그인 완료") // 일시적으로 확인용

                        // 다음 화면으로 이동
                        startActivity(
                            Intent(this@LoginPasswordActivity, LoginWelcomeActivity::class.java).apply {
//                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                putExtra("nickname","코르모")
                            }
                        )
                        finish()
                    } else {
                        showLoading(false)
                        submitting = false
                        toast("로그인 실패: ${res.code()}")
                    }
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    submitting = false
                    toast("네트워크 오류: ${t.message}")
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        // 필요 시 레이아웃에 ProgressBar 추가하여 visibility 토글
        // 여기서는 버튼 비활성화 등만 처리
        btnNext.isEnabled = !show
        etPassword.isEnabled = !show
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun simpleWatcher(onChanged: () -> Unit) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = onChanged()
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    companion object {
        const val EXTRA_USERNAME = "username"
    }
}
