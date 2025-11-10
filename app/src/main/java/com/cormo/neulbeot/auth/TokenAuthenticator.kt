package com.cormo.neulbeot.auth

import android.util.Log
import com.cormo.neulbeot.core.ApiConfig
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit

/**
 * 401 응답 시 한 번만 실행. 새 accessToken을 발급받아 저장하고 동일 요청을 재시도.
 * 실패하면 null 반환(재시도 안 함).
 */
class TokenAuthenticator(
    private val retrofit: Retrofit,
    private val storage: TokenStorage
) : Authenticator {

    val TAG: String = "로그"
    override fun authenticate(route: Route?, response: Response): Request? {
        // 무한루프 방지: 이미 재시도한 요청이면 중단
        if (responseCount(response) > 1) return null

        val refreshToken = runBlocking { storage.getRefreshToken() } ?: return null
        val service = retrofit.create(AuthApi::class.java)

        val newToken = runBlocking {
            try {
                Log.d(TAG, "reissue() called ${refreshToken}")
                val res = service.reissue(refreshToken)
                if (res.isSuccessful) {
                    val access = res.body()?.accessToken
                    Log.d(TAG, "TokenAuthenticator - 성공() called")
                    if (!access.isNullOrEmpty()) {
                        storage.saveAccessOnly(access)
                        access
                    } else null
                } else {
                    Log.d(TAG, "TokenAuthenticator - 실패() called ${res.errorBody()?.string()}")
                    null
                }
            } catch (_: Throwable) {
                null
            }
        } ?: return null

        // 새 토큰으로 원요청 재작성
        return response.request.newBuilder()
            .header(ApiConfig.ACCESS_HEADER_NAME, newToken)
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior: Response? = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
