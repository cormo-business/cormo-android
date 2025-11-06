package com.cormo.neulbeot.page.signup_pages.api

data class SignupRequest(
    val username: String,
    val password: String,
    val birth: String,
    val gender: String,        // "MALE" / "FEMALE"
    val nickName: String,
    val job: String,           // "EMPLOYEE" 등
    val sedentary: Int,
    val health: List<String>,  // ["DIABETES","NECK",...]
    val exercises: List<String>
)