package com.example.careconnect.notifications

// Main settings container
data class NotificationSettings(
    val chatNotifications: ChatNotificationSettings = ChatNotificationSettings(),
    val appointmentNotifications: AppointmentNotificationSettings = AppointmentNotificationSettings()
)

// Chat notification settings
data class ChatNotificationSettings(
    val enabled: Boolean = true,
    val sound: Boolean = true,
    val vibration: Boolean = true,
    val showPreview: Boolean = true // Show message content in notification
)

// Appointment notification settings
data class AppointmentNotificationSettings(
    val enabled: Boolean = true,
    val sound: Boolean = true,
    val vibration: Boolean = true,
    val confirmations: Boolean = true,
    val reminders: Boolean = true,
    val cancellations: Boolean = true,
    val completions: Boolean = true,
    val reminderTimeBefore: Int = 30 // minutes before appointment
)