package com.cormo.neulbeot.data.repository

import android.content.Context
import com.cormo.neulbeot.api.member.MemberService
import com.cormo.neulbeot.data.model.Result

class MemberRepository(private val context: Context) {

    suspend fun isRegistered(username: String): Result<Boolean>{
        return try {
            val res = MemberService.api(context).exists(username)
            if(res.isSuccessful){
                Result.Success(res.body() == true)
            }else{
                Result.Error(IllegalStateException("HTTP Error: ${res.code()}"))
            }
        } catch (t: Throwable){
            Result.Error(t)
        }
    }
}