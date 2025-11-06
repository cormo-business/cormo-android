package com.cormo.neulbeot.api.member

import android.content.Context
import com.cormo.neulbeot.core.ApiClient

object MemberService {
    fun api(context: Context): MemberApi =
        ApiClient.retrofit(context).create(MemberApi::class.java)

    // Boolean 결과만 반환하는 편의 함수
    suspend fun isRegistered(context: Context, username: String): Boolean {
        val res = api(context).exists(username)
        return res.isSuccessful && (res.body() == true)
    }
}
