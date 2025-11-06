package com.cormo.neulbeot.page.signup_pages.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupApi {
    @POST("/api/member/register")
    suspend fun register(@Body body: SignupRequest): Response<Unit>
}