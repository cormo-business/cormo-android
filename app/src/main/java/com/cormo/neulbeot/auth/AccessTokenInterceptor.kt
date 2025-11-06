package com.cormo.neulbeot.auth

import com.cormo.neulbeot.core.ApiConfig
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor(
    private val storage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        // 저장된 액세스 토큰을 헤더에 붙임
        val token = runBlocking { storage.getAccessToken() }
        if (!token.isNullOrEmpty()) {
            builder.header(ApiConfig.ACCESS_HEADER_NAME, token)
        }
        return chain.proceed(builder.build())
    }
}
