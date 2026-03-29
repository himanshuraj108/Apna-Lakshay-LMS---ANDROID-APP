package com.example.lms_android.data.models

data class MarkAttendanceRequest(
    val qrToken: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class MarkAttendanceResponse(
    val success: Boolean,
    val message: String?,
    val type: String?,      // "entry", "exit", "already_marked"
    val attendance: AttendanceRecord?
)
