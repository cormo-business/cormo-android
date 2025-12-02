package com.cormo.neulbeot.page.signup_pages.api

data class SmsCheckRequest(
    val phoneNumber: String,
    val code: String
)
