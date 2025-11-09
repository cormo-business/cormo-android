package com.cormo.neulbeot.fcm

import android.content.Context
import android.util.Log
import com.cormo.neulbeot.core.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun sendFcmTokenAfterLogin(context: Context) {
    val authPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val fcmPrefs = context.getSharedPreferences("fcm", Context.MODE_PRIVATE)

    val userId = authPrefs.getLong("userId", -1L)
    val token = fcmPrefs.getString("token", null)

    if (userId <= 0) {
        Log.w("FCM", "❌ sendFcmTokenAfterLogin: userId not found in SharedPreferences")
        return
    }

    if (token.isNullOrEmpty()) {
        Log.w("FCM", "❌ sendFcmTokenAfterLogin: token not found (onNewToken not yet called)")
        return
    }

    Log.d("FCM", "🚀 Sending FCM token to server (userId=$userId, token=$token)")

    ApiClient.fcm(context).register(RegisterReq(userId, token, "android"))
        .enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("FCM", "✅ Token registered successfully on server")
                } else {
                    Log.w("FCM", "⚠️ Token registration failed: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("FCM", "❌ Token registration failed: ${t.message}", t)
            }
        })
}
