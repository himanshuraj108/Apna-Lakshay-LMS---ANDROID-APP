package com.example.lms_android.data.models

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: User?,
    val message: String?
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isActive: Boolean,
    val registrationSource: String?,
    val profileImage: String?,
    val createdAt: String
)

data class ForgotPasswordRequest(val email: String)
data class ForgotPasswordResponse(val success: Boolean, val message: String?)

data class VerifyOtpRequest(val email: String, val otp: String)
data class VerifyOtpResponse(val success: Boolean, val message: String?)

data class ResetPasswordRequest(val email: String, val otp: String, val password: String)
data class ResetPasswordResponse(val success: Boolean, val message: String?)

data class RegisterRequest(
    val name: String,
    val email: String,
    val mobile: String,
    val address: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String?
)
