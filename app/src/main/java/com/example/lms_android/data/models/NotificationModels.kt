package com.example.lms_android.data.models

data class NotificationModel(
    val _id: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)

data class NotificationResponse(
    val success: Boolean,
    val notifications: List<NotificationModel>
)

data class ReadNotificationResponse(
    val success: Boolean,
    val notification: NotificationModel
)
