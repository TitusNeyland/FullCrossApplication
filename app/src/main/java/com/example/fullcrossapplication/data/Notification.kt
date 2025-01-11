package com.example.fullcrossapplication.data

data class Notification(
    val id: String = "",
    val type: NotificationType = NotificationType.FRIEND_REQUEST,
    val fromUserId: String = "",
    val fromUserName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)

enum class NotificationType {
    FRIEND_REQUEST
} 