package com.cormo.neulbeot.auth

import android.content.Context
import com.cormo.neulbeot.core.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val storage = TokenStorage(context)
    private val service = ApiClient.retrofit(context).create(AuthService::class.java)

    suspend fun loadSaved(): AuthState {
        val token = storage.getAccessToken()
        return if (!token.isNullOrEmpty()) AuthState.auth(token) else AuthState.unauth()
    }

    suspend fun login(username: String, password: String): AuthState {
        return withContext(Dispatchers.IO) {
            val res = service.login(LoginRequest(username, password))
            if (!res.isSuccessful) throw IllegalStateException("Login failed: ${res.code()}")
            val body = res.body() ?: throw IllegalStateException("Empty body")
            storage.saveTokens(access = body.accessToken, refresh = body.refreshToken)
            AuthState.auth(body.accessToken)
        }
    }

    suspend fun logout() {
        storage.clearTokens()
    }

    /** 외부에서 필요시 액세스 토큰만 갱신 저장 (재발급 성공 시 Authenticator도 호출함) */
    suspend fun setAccessOnly(token: String) = storage.saveAccessOnly(token)
}
