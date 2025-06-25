package com.example.careconnect.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.careconnect.MainActivity
import com.example.careconnect.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class CareConnectMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FCMTokenManager

    @Inject
    lateinit var notificationSettingsManager: NotificationSettingsManager

    companion object {
        private const val TAG = "FCMService"
        private const val APPOINTMENT_CHANNEL_ID = "appointment_notifications"
        private const val APPOINTMENT_CHANNEL_NAME = "Appointment Notifications"
        private const val APPOINTMENT_CHANNEL_DESCRIPTION = "Notifications for appointment updates and reminders"
        private const val CHAT_CHANNEL_ID = "chat_notifications"
        private const val CHAT_CHANNEL_NAME = "Chat Messages"
        private const val CHAT_CHANNEL_DESCRIPTION = "Notifications for new chat messages"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🔔 FCM Service created")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "NEW_TOKEN: $token")
        CoroutineScope(Dispatchers.IO).launch {
            fcmTokenManager.updateFCMToken()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Get user settings before processing notification
        val settings = runBlocking {
            notificationSettingsManager.getSettings()
        }

        val notificationType = remoteMessage.data["type"] ?: ""

        when (notificationType) {
            "CHAT_MESSAGE" -> {
                if (settings.chatNotifications.enabled) {
                    handleChatNotification(remoteMessage, settings.chatNotifications)
                } else {
                    Log.d(TAG, "Chat notifications disabled - skipping")
                }
            }
            else -> {
                if (settings.appointmentNotifications.enabled) {
                    handleAppointmentNotification(remoteMessage, settings.appointmentNotifications)
                } else {
                    Log.d(TAG, "Appointment notifications disabled - skipping")
                }
            }
        }
    }

    // CHAT NOTIFICATION
    private fun handleChatNotification(remoteMessage: RemoteMessage, chatSettings: ChatNotificationSettings) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "New Message"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val chatId = remoteMessage.data["chatId"] ?: ""
        val senderId = remoteMessage.data["senderId"] ?: ""
        val senderName = remoteMessage.data["senderName"] ?: ""
        val recipientId = remoteMessage.data["recipientId"] ?: ""

        showChatNotification(title, body, chatId, senderId, senderName, recipientId, chatSettings)
    }

    private fun showChatNotification(
        title: String,
        body: String,
        chatId: String,
        senderId: String,
        senderName: String,
        recipientId: String,
        chatSettings: ChatNotificationSettings
    ) {
        createNotificationChannel(CHAT_CHANNEL_ID, CHAT_CHANNEL_NAME, CHAT_CHANNEL_DESCRIPTION)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", "CHAT_MESSAGE")
            putExtra("chat_id", chatId)
            putExtra("sender_id", senderId)
            putExtra("recipient_id", recipientId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            chatId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHAT_CHANNEL_ID)
            .setSmallIcon(R.drawable.groups_24px)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        // Apply user settings
        if (chatSettings.showPreview) {
            // Keep title and body as is
        } else {
            // Hide message content
            notificationBuilder
                .setContentTitle("New Message")
                .setContentText("You have a new message")
                .setStyle(NotificationCompat.BigTextStyle().bigText("You have a new message"))
        }

        if (chatSettings.sound) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        if (chatSettings.vibration) {
            notificationBuilder.setVibrate(longArrayOf(0, 250, 250, 250))
        }

        val notification = notificationBuilder.build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(chatId.hashCode(), notification)
    }

    // APPOINTMENT NOTIFICATION
    private fun handleAppointmentNotification(remoteMessage: RemoteMessage, appointmentSettings: AppointmentNotificationSettings) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "CareConnect"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val notificationType = remoteMessage.data["type"] ?: ""
        val appointmentId = remoteMessage.data["appointmentId"] ?: ""
        val userType = remoteMessage.data["userType"] ?: ""

        showAppointmentNotification(title, body, notificationType, appointmentId, userType, appointmentSettings)
    }

    private fun showAppointmentNotification(
        title: String,
        body: String,
        type: String,
        appointmentId: String,
        userType: String,
        appointmentSettings: AppointmentNotificationSettings
    ) {
        createNotificationChannel(APPOINTMENT_CHANNEL_ID, APPOINTMENT_CHANNEL_NAME, APPOINTMENT_CHANNEL_DESCRIPTION)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
            putExtra("appointment_id", appointmentId)
            putExtra("user_type", userType)
            putExtra("from_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            appointmentId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, APPOINTMENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Apply user settings
        if (appointmentSettings.sound) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        if (appointmentSettings.vibration) {
            notificationBuilder.setVibrate(longArrayOf(0, 1000, 500, 1000))
        }

        val notification = notificationBuilder.build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = appointmentId.hashCode()
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String, channelDescription: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Check if channel already exists
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel != null) {
                Log.d(TAG, "📱 channel $channelId already exists")
                return
            }

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                enableVibration(true)
                vibrationPattern = when (channelId) {
                    APPOINTMENT_CHANNEL_ID -> longArrayOf(0, 1000, 500, 1000)
                    CHAT_CHANNEL_ID -> longArrayOf(0, 250, 250, 250)
                    else -> longArrayOf(0, 500)
                }
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            }

            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel $channelId created")
        }
    }
}