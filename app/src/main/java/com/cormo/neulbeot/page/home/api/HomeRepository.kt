package com.cormo.neulbeot.page.home.api

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.cormo.neulbeot.core.ApiClient
import retrofit2.HttpException
import androidx.core.content.edit


class HomeRepository(
    private val context: Context
) {
    private val api: HomeApi =
        ApiClient.retrofit(context.applicationContext).create(HomeApi::class.java)

    suspend fun initHome(): Result<HomeInitResponse> {
        return try {
            val res = api.init()
            val body = res.body()
            if (res.isSuccessful && body != null) {
                // 유저 ID 저장하기
                Log.d("FCM", "initHome: ${body.userId}")
                context.getSharedPreferences("auth", MODE_PRIVATE)
                    .edit {
                        putLong("userId", body.userId)
                    }

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