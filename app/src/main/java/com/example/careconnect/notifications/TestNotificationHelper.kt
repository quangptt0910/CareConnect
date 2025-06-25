package com.example.careconnect.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.careconnect.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestNotificationHelper @Inject constructor() {

    companion object {
        private const val TEST_CHANNEL_ID = "test_notifications"
        private const val TEST_CHANNEL_NAME = "Test Notifications"
        private const val TEST_CHANNEL_DESCRIPTION = "Test notifications for settings verification"
    }

    fun sendTestNotification(context: Context, settings: NotificationSettings) {
        createTestNotificationChannel(context)

        // Send chat test notification if enabled
        if (settings.chatNotifications.enabled) {
            sendTestChatNotification(context, settings.chatNotifications)
        }

        // Send appointment test notification if enabled
        if (settings.appointmentNotifications.enabled) {
            sendTestAppointmentNotification(context, settings.appointmentNotifications)
        }
    }

    private fun sendTestChatNotification(context: Context, chatSettings: ChatNotificationSettings) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = "Test Chat Message"
        val body = if (chatSettings.showPreview) {
            "This is how your chat notifications will appear"
        } else {
            "New message received"
        }

        val builder = NotificationCompat.Builder(context, TEST_CHANNEL_ID)
            .setSmallIcon(R.drawable.groups_24px)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setGroup("test_notifications")

        // Apply sound setting
        if (!chatSettings.sound) {
            builder.setSilent(true)
        }

        // Apply vibration setting
        if (chatSettings.vibration) {
            builder.setVibrate(longArrayOf(0, 250, 250, 250))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        notificationManager.notify(1001, builder.build())
    }

    private fun sendTestAppointmentNotification(context: Context, appointmentSettings: AppointmentNotificationSettings) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = "Test Appointment Notification"
        val body = "This is how your appointment notifications will appear"

        val builder = NotificationCompat.Builder(context, TEST_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setGroup("test_notifications")

        // Apply sound setting
        if (!appointmentSettings.sound) {
            builder.setSilent(true)
        }

        // Apply vibration setting
        if (appointmentSettings.vibration) {
            builder.setVibrate(longArrayOf(0, 1000, 500, 1000))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        notificationManager.notify(1002, builder.build())
    }

    private fun createTestNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Check if channel already exists
            if (notificationManager.getNotificationChannel(TEST_CHANNEL_ID) != null) {
                return
            }

            val channel = NotificationChannel(
                TEST_CHANNEL_ID,
                TEST_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = TEST_CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }
}