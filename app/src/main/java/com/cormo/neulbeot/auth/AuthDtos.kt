package com.cormo.neulbeot.auth

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val accessToken: String, val refreshToken: String?)

data class ReissueRequest(val refreshToken: String)
data class ReissueResponse(val accessToken: String)
