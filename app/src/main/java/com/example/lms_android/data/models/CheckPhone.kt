package com.example.lms_android.data.models

data class CheckPhoneRequest(
    val mobile: String
)

data class CheckPhoneResponse(
    val success: Boolean,
    val message: String?
)
