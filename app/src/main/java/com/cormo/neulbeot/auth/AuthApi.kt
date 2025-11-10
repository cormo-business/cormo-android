package com.cormo.neulbeot.auth

import com.cormo.neulbeot.core.ApiConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @POST(ApiConfig.LOGIN_PATH)
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST(ApiConfig.REISSUE_PATH)
    suspend fun reissue(
        @Header("refreshToken") refreshToken: String
    ): Response<ReissueResponse>
}
