package com.cormo.neulbeot.page.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cormo.neulbeot.R
import com.cormo.neulbeot.auth.TokenStorage
import com.cormo.neulbeot.page.login.view_model.LoginPasswordViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPasswordActivity : AppCompatActivity() {

    private val vm: LoginPasswordViewModel by viewModels()

    private lateinit var etPassword: EditText
    private lateinit var btnTogglePass: ImageButton
    private lateinit var btnNext: Button
    private lateinit var tvHint: TextView

    private var obscure = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_login_password)

        etPassword = findViewById(R.id.etPassword)
        btnTogglePass = findViewById(R.id.btnTogglePass)
        btnNext = findViewById(R.id.btnNext)
        tvHint = findViewById(R.id.tvHint)

        tvHint.text = "로그인을 누르면 비밀번호 확인이 진행됩니다."

        btnTogglePass.setOnClickListener { togglePassword() }

        etPassword.addTextChangedListener(simpleWatcher { updateNextEnabled() })
        btnNext.setOnClickListener {
            val username = intent.getStringExtra(EXTRA_USERNAME)?.trim().orEmpty()
            val password = etPassword.text.toString().trim()
            vm.login(username, password)
        }

        observeViewModel()
        updateNextEnabled()
    }

    private fun observeViewModel() {
        vm.loading.observe(this) { showLoading(it) }
        vm.error.observe(this) { it?.let { toast(it) } }
        vm.loginSuccess.observe(this) { success ->
            if (success == true) {
                toast("로그인 성공!")

                // ✅ suspend 함수는 코루틴 안에서 호출해야 함
                // todo: 지금은 accessToken 확인위해 이렇게함, 로그인 시 닉네임 전송해야할듯
                CoroutineScope(Dispatchers.Main).launch {
                    val accessToken = withContext(Dispatchers.IO) {
                        TokenStorage(this@LoginPasswordActivity).getAccessToken()
                    }

                    startActivity(
                        Intent(this@LoginPasswordActivity, LoginWelcomeActivity::class.java).apply {
                            putExtra("accessToken", accessToken)
                        }
                    )
                    finish()
                }
            }
        }
    }


    private fun updateNextEnabled() {
        val pass = etPassword.text?.toString()?.trim().orEmpty()
        btnNext.isEnabled = pass.length >= 6
        btnNext.alpha = if (btnNext.isEnabled) 1f else 0.5f
    }

    private fun togglePassword() {
        obscure = !obscure
        val cursor = etPassword.selectionStart
        etPassword.inputType = if (obscure)
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        etPassword.setSelection(cursor)
        btnTogglePass.setImageResource(
            if (obscure) R.drawable.ic_visibility_off else R.drawable.ic_visibility
        )
    }

    private fun showLoading(show: Boolean) {
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
