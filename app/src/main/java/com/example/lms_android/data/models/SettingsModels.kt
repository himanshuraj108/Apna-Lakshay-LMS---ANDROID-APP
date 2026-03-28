package com.example.lms_android.data.models

data class PublicSettingsResponse(
    val success: Boolean,
    val settings: AppSettings?,
    val locationData: LocationData?
)

data class AppSettings(
    val shiftMode: String?,
    val systemStatus: String?,
    val locationAttendance: Boolean?
)

data class LocationData(
    val lat: String?,
    val lng: String?,
    val mapUrl: String?
)
