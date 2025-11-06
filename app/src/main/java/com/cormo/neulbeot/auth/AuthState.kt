package com.cormo.neulbeot.auth

data class AuthState(
    val isAuthenticated: Boolean,
    val accessToken: String?
) {
    companion object {
        fun unauth() = AuthState(false, null)
        fun auth(token: String) = AuthState(true, token)
    }
}
