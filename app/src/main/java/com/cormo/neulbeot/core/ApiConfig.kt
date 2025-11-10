package com.cormo.neulbeot.core

object ApiConfig {
    const val BASE_URL = "http://175.123.55.182:8088"
    const val LOGIN_PATH = "/api/login"
    const val REISSUE_PATH = "/api/reissue"

    // 서버가 요구하는 액세스 토큰 헤더 이름
    const val ACCESS_HEADER_NAME = "accessToken"
    const val REFRESH_HEADER_NAME = "refreshToken"
}
