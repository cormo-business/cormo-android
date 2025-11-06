package com.cormo.neulbeot.api.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApi {
    @POST("/api/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("/api/member/init/info")
    suspend fun init(): Response<HomeInitResponse>
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val nickname: String
)

data class HomeInitResponse (
    val nickname: String,
    val energy: Int,
    val level: Int,
    val levelProgress: Int,
    val point: Int,
    val attendanceNum: Int
)