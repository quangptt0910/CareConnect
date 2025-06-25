package com.example.careconnect.notifications

// Main settings container
/**
 * Contains overall notification settings for a user,
 * including chat and appointment notifications.
 *
 * @property chatNotifications Settings related to chat message notifications.
 * @property appointmentNotifications Settings related to appointment notifications.
 */
data class NotificationSettings(
    val chatNotifications: ChatNotificationSettings = ChatNotificationSettings(),
    val appointmentNotifications: AppointmentNotificationSettings = AppointmentNotificationSettings()
)

// Chat notification settings
/**
 * Settings for chat message notifications.
 *
 * @property enabled Whether chat notifications are enabled.
 * @property sound Whether sound is played on receiving a notification.
 * @property vibration Whether the device vibrates on notification.
 * @property showPreview Whether to show message content in the notification.
 */
data class ChatNotificationSettings(
    val enabled: Boolean = true,
    val sound: Boolean = true,
    val vibration: Boolean = true,
    val showPreview: Boolean = true // Show message content in notification
)

// Appointment notification settings
/**
 * Settings for appointment-related notifications.
 *
 * @property enabled Whether appointment notifications are enabled.
 * @property sound Whether sound is played on receiving a notification.
 * @property vibration Whether the device vibrates on notification.
 * @property confirmations Whether to notify on appointment confirmation.
 * @property reminders Whether to notify before upcoming appointments.
 * @property cancellations Whether to notify on appointment cancellation.
 * @property completions Whether to notify when an appointment is marked as completed.
 * @property reminderTimeBefore Minutes before the appointment to send a reminder.
 */
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