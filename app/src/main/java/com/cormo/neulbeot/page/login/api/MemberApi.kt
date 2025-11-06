package com.cormo.neulbeot.api.member

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MemberApi {
    // 서버 응답 본문: true 또는 false (JSON boolean)
    @GET("/api/member/exists")
    suspend fun exists(@Query("username") username: String): Response<Boolean>
}
