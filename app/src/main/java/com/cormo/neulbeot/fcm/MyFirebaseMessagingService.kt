package com.cormo.neulbeot.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cormo.neulbeot.MainActivity
import com.cormo.neulbeot.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.content.edit
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "onNewToken: $token")
        getSharedPreferences("fcm", MODE_PRIVATE).edit {
            putString("token", token)
        }
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)

        // ✅ 서버에서 data만 보내므로 data 우선 사용
        val title = msg.data["title"] ?: msg.notification?.title ?: "알림"
        val body = msg.data["body"] ?: msg.notification?.body ?: ""

        Log.d("FCM", "📩 Received: title=$title, body=$body, data=${msg.data}")

        // ✅ 백그라운드/포그라운드 모두 수동으로 표시
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default"   // 서버 FirebaseConfig와 통일
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 13 이상 권한 체크는 Activity 쪽에서 해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "기본 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            },
            PendingIntent.FLAG_ONE_SHOT or
                    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
