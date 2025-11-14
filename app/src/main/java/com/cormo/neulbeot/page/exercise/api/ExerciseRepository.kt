package com.cormo.neulbeot.page.exercise.api

import android.content.Context
import android.util.Log
import com.cormo.neulbeot.core.ApiClient
import retrofit2.HttpException

class ExerciseRepository(
    private val context: Context
) {

    private val api: ExerciseApi =
        ApiClient.retrofit(context.applicationContext).create(ExerciseApi::class.java)

    suspend fun saveRecord(
        request: SaveRecordRequest
    ): Result<SaveRecordResponse> {
        return try {

            val res = api.saveRecord(body = request)

            val body = res.body()
            if (res.isSuccessful && body != null) {
                // 정상 응답
                Log.d("로그", "저장완료 닉네임?: ${body.memberId}")

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

}