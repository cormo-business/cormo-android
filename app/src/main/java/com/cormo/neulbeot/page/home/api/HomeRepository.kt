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
                Log.d("로그", "initHome: ${body.userId}, ${body.checkAttendance}")

                context.getSharedPreferences("auth", MODE_PRIVATE)
                    .edit {
                        putLong("userId", body.userId)
                        putBoolean("attendance", body.checkAttendance)
                    }
                Result.success(body)
            } else {
                // 토큰 재발급해서 다시 실행하기
                Result.failure(
                    HttpException(res)  // 코드/메시지 포함
                )
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    // 출석 체크하기
    suspend fun attendance():Result<Long>{

        return try {

            val res = api.attendance()
            val body = res.body()
            if(res.isSuccessful && body != null){
                Result.success(body)
            }else{
                Result.failure(
                    // 실패 처리
                    HttpException(res)  // 코드/메시지 포함
                )
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}