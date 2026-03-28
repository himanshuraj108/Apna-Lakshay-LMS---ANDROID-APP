package com.example.lms_android.data.models

data class KioskAttendanceRequest(
    val mobile: String,
    val kioskToken: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class KioskStudentInfo(
    val name: String,
    val seat: String?
)

data class KioskAttendanceResponse(
    val success: Boolean,
    val message: String,
    val type: String?,
    val time: String?,
    val student: KioskStudentInfo?
)
