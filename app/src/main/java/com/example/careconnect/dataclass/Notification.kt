package com.example.careconnect.dataclass

// Notification data classes
data class NotificationData(
    val type: NotificationType = NotificationType.APPOINTMENT_REQUEST,
    val appointmentId: String = "",
    val title: String = "",
    val body: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val data: Map<String, String> = emptyMap()
)

enum class NotificationType {
    APPOINTMENT_REQUEST,
    APPOINTMENT_CONFIRMED,
    APPOINTMENT_DECLINED,
    APPOINTMENT_REMINDER
}