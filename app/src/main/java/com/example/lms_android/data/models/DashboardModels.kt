package com.example.lms_android.data.models

data class DashboardResponse(
    val success: Boolean,
    val data: DashboardData?
)

data class DashboardData(
    val registrationSource: String?,
    val studentName: String?,
    val isActive: Boolean?,
    val seat: SeatData?,
    val attendance: AttendanceData?,
    val fee: FeeData?,
    val unreadNotifications: Int?,
    val requestsCount: Int?,
    val doubtCredits: Int?
)

data class SeatData(
    val number: String?,
    val floor: String?,
    val room: String?,
    val shift: String?
)

data class AttendanceData(
    val present: Int?,
    val total: Int?,
    val percentage: Int?,
    val rank: Int?
)

data class FeeData(
    val amount: Int?,
    val status: String?,
    val dueDate: String?,
    val paidDate: String?
)
