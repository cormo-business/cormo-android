package com.cormo.neulbeot.page.exercise.api

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.cormo.neulbeot.core.ApiClient
import com.cormo.neulbeot.data.model.Result
import retrofit2.HttpException

class ExerciseRepository(
    private val context: Context
) {

    private val api: ExerciseApi =
        ApiClient.retrofit(context.applicationContext).create(ExerciseApi::class.java)

    suspend fun saveRecord(): Result<SaveRecordResponse> {
        return try {
            val authStore = context.getSharedPreferences("auth", MODE_PRIVATE)
            val nickname = authStore.getString("nickname", null)

            if(nickname == null){
                return Result.Error(IllegalStateException("실패!"))
            }

            val res = api.saveRecord(body = SaveRecordRequest(nickname))

            val body = res.body()
            if (res.isSuccessful && body != null) {
                // 정상 응답
                Log.d("로그", "저장완료 닉네임?: ${body.memberId}")

//                context.getSharedPreferences("auth", MODE_PRIVATE)
//                    .edit {
//                        putLong("userId", body.userId)
//                        putBoolean("attendance", body.checkAttendance)
//                    }
//
                Result.Success(body)
            } else {
                // 토큰 재발급해서 다시 실행하기
                Result.Error(
                    HttpException(res)  // 코드/메시지 포함
                )
            }
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

}