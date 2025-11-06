package com.cormo.neulbeot.auth

import android.content.Context
import androidx.core.content.edit

class TokenStorage(private val context: Context) {
    private val prefs by lazy {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    suspend fun saveTokens(access: String, refresh: String?) {
        prefs.edit {
            putString("accessToken", access)
            if (refresh != null) putString("refreshToken", refresh)
        }
    }

    suspend fun saveAccessOnly(access: String) {
        prefs.edit { putString("accessToken", access) }
    }

    suspend fun getAccessToken(): String? = prefs.getString("accessToken", null)
    suspend fun getRefreshToken(): String? = prefs.getString("refreshToken", null)

    suspend fun clearTokens() {
        prefs.edit {
            remove("accessToken")
            remove("refreshToken")
        }
    }
}