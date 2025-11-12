package com.cormo.neulbeot

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cormo.neulbeot.auth.TokenStorage
import com.cormo.neulbeot.page.login.LoginMethodActivity
import com.cormo.neulbeot.fcm.sendFcmTokenAfterLogin
import com.cormo.neulbeot.page.home.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // 🔹 알림 권한 요청 런처
    private val requestNotifPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d("FCM", "알림 권한 허용됨")
            sendFcmTokenAfterLogin(this)
        } else {
            Log.w("FCM", "알림 권한 거부됨")
            if (Build.VERSION.SDK_INT >= 33 &&
                !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            ) {
                openAppNotificationSettings()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 채널 생성
        ensureNotificationChannel()

        // 권한 요청 (API 33+)
        ensureNotificationPermission()

        val storage = TokenStorage(this)
        val accessToken = storage.getAccessToken()
        if (accessToken != null) {
            lifecycleScope.launch {
                delay(200) // 0.1초 대기
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish() // 현재 액티비티 종료
            }
        }

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            val intent = Intent(this, LoginMethodActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 눌렀을 때 색 진하게 변경
        startButton.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN ->
                    v.setBackgroundColor(android.graphics.Color.parseColor("#0097A7"))
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL ->
                    v.setBackgroundColor(android.graphics.Color.parseColor("#00B8D4"))
            }
            false
        }

    }

    /** 🔸 채널 생성 */
    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chId = "default"
            val nm = getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(chId) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(chId, "일반 알림", NotificationManager.IMPORTANCE_HIGH)
                )
            }
        }
    }

    /** 🔸 알림 권한 요청 (Android 13 이상) */
    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestNotifPerm.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                sendFcmTokenAfterLogin(this)
            }
        } else {
            sendFcmTokenAfterLogin(this)
        }
    }

    /** 🔸 알림 설정 화면 열기 */
    private fun openAppNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }
}
