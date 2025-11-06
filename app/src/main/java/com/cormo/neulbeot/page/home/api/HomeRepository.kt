package com.cormo.neulbeot.page.home.api

import android.content.Context
import com.cormo.neulbeot.api.login.LoginApi
import com.cormo.neulbeot.core.ApiClient
import retrofit2.HttpException

class HomeRepository(
    context: Context
) {
    private val api: HomeApi =
        ApiClient.retrofit(context.applicationContext).create(HomeApi::class.java)

    suspend fun initHome(): Result<HomeInitResponse> {
        return try {
            val res = api.init()
            val body = res.body()
            if (res.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(
                    HttpException(res)  // 코드/메시지 포함
                )
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}