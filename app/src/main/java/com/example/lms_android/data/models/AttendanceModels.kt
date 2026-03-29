package com.example.lms_android.data.models

import com.google.gson.annotations.SerializedName

data class AttendanceResponse(
    val success: Boolean,
    val myAttendance: List<AttendanceRecord>?,
    val summary: AttendanceSummary?,
    val rankings: List<AttendanceRanking>?,
    val holidays: List<HolidayRecord>?
)

data class AttendanceRecord(
    @SerializedName("_id") val id: String?,
    val student: String?,
    val date: String?,
    val status: String?,           // "present", "absent", "holiday"
    val entryTime: String?,        // "HH:mm"
    val exitTime: String?,         // "HH:mm"
    val duration: Int?,            // in minutes
    val notes: String?,
    val isActive: Boolean?,
    val lateEntry: Boolean?,
    val distanceMeters: Double?
)

data class AttendanceSummary(
    val present: Int,
    val total: Int,
    val percentage: Int
)

data class AttendanceRanking(
    val studentId: String?,
    val name: String?,
    val percentage: Int,
    val rank: Int,
    val isMe: Boolean
)

data class HolidayRecord(
    @SerializedName("_id") val id: String?,
    val name: String?,
    val date: String?
)
