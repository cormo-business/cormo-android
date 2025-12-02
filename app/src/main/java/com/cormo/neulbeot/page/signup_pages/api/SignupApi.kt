package com.cormo.neulbeot.page.signup_pages.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupApi {
    @POST("/api/member/register")
    suspend fun register(@Body body: SignupRequest): Response<Unit>

    @POST("/api/sms/send")
    suspend fun sendSms(@Body body: SmsRequest): Response<Unit>

    @POST("/api/sms/check")
    suspend fun checkedSms(@Body body: SmsCheckRequest): Response<Boolean>
}