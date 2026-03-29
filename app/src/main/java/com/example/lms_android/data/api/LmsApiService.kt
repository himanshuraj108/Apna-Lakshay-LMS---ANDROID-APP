package com.example.lms_android.data.api

import com.example.lms_android.data.models.LoginRequest
import com.example.lms_android.data.models.LoginResponse
import com.example.lms_android.data.models.AttendanceResponse
import com.example.lms_android.data.models.MySeatResponse
import com.example.lms_android.data.models.DashboardResponse
import com.example.lms_android.data.models.CheckPhoneRequest
import com.example.lms_android.data.models.CheckPhoneResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LmsApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: com.example.lms_android.data.models.ForgotPasswordRequest): com.example.lms_android.data.models.ForgotPasswordResponse

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: com.example.lms_android.data.models.VerifyOtpRequest): com.example.lms_android.data.models.VerifyOtpResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: com.example.lms_android.data.models.ResetPasswordRequest): com.example.lms_android.data.models.ResetPasswordResponse

    @POST("auth/check-phone")
    suspend fun checkPhone(@Body request: CheckPhoneRequest): CheckPhoneResponse

    @POST("auth/kiosk-attendance")
    suspend fun markKioskAttendance(@Body request: com.example.lms_android.data.models.KioskAttendanceRequest): com.example.lms_android.data.models.KioskAttendanceResponse

    @GET("public/seats")
    suspend fun getPublicSeats(): com.example.lms_android.data.models.PublicSeatsResponse

    @GET("public/shifts")
    suspend fun getPublicShifts(): com.example.lms_android.data.models.PublicShiftsResponse

    @GET("public/settings")
    suspend fun getPublicSettings(): com.example.lms_android.data.models.PublicSettingsResponse
    
    @POST("public/register")
    suspend fun register(@Body request: com.example.lms_android.data.models.RegisterRequest): com.example.lms_android.data.models.RegisterResponse

    @GET("student/dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("student/attendance")
    suspend fun getAttendance(): AttendanceResponse

    @GET("student/seat")
    suspend fun getMySeat(): MySeatResponse

    @POST("student/attendance/qr-scan")
    suspend fun markAttendanceByQr(@Body request: com.example.lms_android.data.models.MarkAttendanceRequest): com.example.lms_android.data.models.MarkAttendanceResponse

    @POST("student/attendance/mark-self")
    suspend fun markSelfAttendance(@Body request: com.example.lms_android.data.models.MarkAttendanceRequest): com.example.lms_android.data.models.MarkAttendanceResponse
}
