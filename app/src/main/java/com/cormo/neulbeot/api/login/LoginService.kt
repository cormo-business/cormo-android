package com.cormo.neulbeot.api.login

import android.content.Context
import com.cormo.neulbeot.core.ApiClient

object LoginService {
    fun api(context: Context): LoginApi =
        ApiClient.retrofit(context).create(LoginApi::class.java)

    // 홈 처음 들어올 때 API 연동
    suspend fun initHome(context: Context): HomeInitResponse{
        val res = api(context).init()
        return res.body()!!
    }

}