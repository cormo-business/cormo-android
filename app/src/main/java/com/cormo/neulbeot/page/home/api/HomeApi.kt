package com.cormo.neulbeot.page.home.api

import retrofit2.Response
import retrofit2.http.GET

interface HomeApi {
    @GET("/api/member/init/info")
    suspend fun init(): Response<HomeInitResponse>
}

data class HomeInitResponse (
    val nickname: String,
    val level: Int,
    val point: Int,
    val levelProgress: Int,
    val attendanceNum: Int,
    val profilePath: String,
    val userId: Long
)