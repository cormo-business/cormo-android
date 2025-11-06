package com.cormo.neulbeot.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/login")
    suspend fun login(@Body body: LoginRequest): Response<Unit>
}

data class LoginRequest(
    val username: String,
    val password: String
)
