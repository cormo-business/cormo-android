package com.cormo.neulbeot.api.signup

import com.cormo.neulbeot.api.signup.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupService {
    @POST("/api/member/register")
    suspend fun register(@Body body: SignupRequest): Response<Unit>
}