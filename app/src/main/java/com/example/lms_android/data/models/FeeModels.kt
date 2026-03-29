package com.example.lms_android.data.models

data class FeesResponse(
    val success: Boolean,
    val fees: List<FeeRecord>?
)

data class FeeRecord(
    val _id: String,
    val amount: Int,
    val month: Int,
    val year: Int,
    val status: String,      // "paid", "pending", "overdue"
    val dueDate: String?,
    val paidDate: String?,
    val registrationFee: Int? = 0,
    val due: Int? = 0,
    val lockerNo: String? = null
)

data class UserProfileResponse(
    val success: Boolean,
    val user: UserProfile?
)

data class UserProfile(
    val id: String?,
    val name: String?,
    val email: String?,
    val mobile: String?,
    val address: String?,
    val fatherName: String?,
    val seatNumber: String?,
    val shift: String?,
    val aadharNo: String? = null,
    val dob: String? = null,
    val lockerNo: String? = null
)
