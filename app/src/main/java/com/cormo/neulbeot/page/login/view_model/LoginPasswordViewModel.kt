package com.cormo.neulbeot.page.login.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.cormo.neulbeot.api.login.LoginApi
import com.cormo.neulbeot.api.login.LoginRequest
import com.cormo.neulbeot.auth.TokenStorage
import com.cormo.neulbeot.core.ApiClient
import kotlinx.coroutines.launch

class LoginPasswordViewModel(app: Application) : AndroidViewModel(app) {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    val TAG: String = "로그"

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _error.value = "전화번호 또는 비밀번호가 비어 있습니다."
            return
        }

        _loading.value = true
        viewModelScope.launch {
            try {
                val api = ApiClient.retrofit(getApplication()).create(LoginApi::class.java)
                val res = api.login(LoginRequest(username, password))

                if (res.isSuccessful) {
                    val access = res.headers()["accessToken"].orEmpty()
                    val refresh = res.headers()["refreshToken"].orEmpty()

                    Log.d(TAG, "로그인 성공 refresh${refresh}")
                    if (access.isEmpty()) {
                        _error.postValue("로그인 실패: accessToken 누락")
                    } else {
                        val body = res.body()
                        if(body != null){
                            TokenStorage(getApplication()).saveTokens(access, refresh)
                            TokenStorage(getApplication()).saveNickname(body.nickname)

                            _nickname.postValue(body.nickname)
                            _loginSuccess.postValue(true)
                        }

                    }
                } else {
                    _error.postValue("로그인 실패: ${res.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("네트워크 오류: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
