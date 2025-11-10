package com.cormo.neulbeot.auth

import android.content.Context
import androidx.core.content.edit

class TokenStorage(private val context: Context) {
    private val prefs by lazy {
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    }

    fun saveTokens(access: String, refresh: String) {
        prefs.edit {
            putString("accessToken", access)
            putString("refreshToken", refresh)
        }
    }

    fun saveAccessOnly(access: String) {
        prefs.edit { putString("accessToken", access) }
    }

    fun getAccessToken(): String? = prefs.getString("accessToken", null)
    fun getRefreshToken(): String? = prefs.getString("refreshToken", null)

    fun removeAccess(){
        prefs.edit{
            remove("accessToken")
        }
    }
    fun clearTokens() {
        prefs.edit {
            remove("accessToken")
            remove("refreshToken")
        }
    }

    fun saveNickname(nickname: String) {
        prefs.edit { putString("nickname", nickname) }
    }

    fun getNickname(): String? = prefs.getString("nickname", null)

    fun clearNickname() {
        prefs.edit { remove("nickname") }
    }

}