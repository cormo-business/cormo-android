package com.cormo.neulbeot.fcm

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

data class RegisterReq(val userId: Long, val token: String, val platform: String = "android")
data class NotifyReq(val title: String, val body: String, val data: Map<String,String>? = null)

interface FcmApi {
    @POST("/api/fcm/register") fun register(@Body req: RegisterReq): Call<Void>

    @POST("/api/notify/all") fun notifyAll(@Body req: NotifyReq): Call<String>
    @POST("/api/notify/excluding/{userId}") fun notifyOthers(@Path("userId") userId: Long, @Body req: NotifyReq): Call<String>
    @POST("/api/notify/user/{userId}") fun notifyUser(@Path("userId") userId: Long, @Body req: NotifyReq): Call<String>
    @POST("/api/notify/topic/{topic}") fun notifyTopic(@Path("topic") topic: String, @Body req: NotifyReq): Call<String>
}
