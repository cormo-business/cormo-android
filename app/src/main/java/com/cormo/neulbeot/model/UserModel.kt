package com.cormo.neulbeot.model

data class UserModel(
    val nickname: String = "",
    val level: Int = 1,
    val coin: Int = 0,
    val profilePath: String? = null
)
