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
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.NotificationSettingsRepository
import com.example.careconnect.dataclass.Role
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


/**
 * Service to handle Firebase Cloud Messaging (FCM) notifications for the CareConnect app.
 *
 * This service:
 * - Listens for new FCM tokens and updates them via [FCMTokenManager].
 * - Receives incoming FCM messages and processes them according to notification settings and user roles.
 * - Supports two main notification categories: Chat messages and Appointment updates.
 * - Creates and manages notification channels for Android O and above.
 * - Builds and displays notifications based on user preferences including sound, vibration, and message preview.
 *
 * Notifications are only shown to users with eligible roles (Doctor or Patient).
 *
 * @see FirebaseMessagingService
 * @see FCMTokenManager
 * @see NotificationSettingsRepository
 * @see AuthRepository
 */
@AndroidEntryPoint
class CareConnectMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FCMTokenManager

    @Inject
    lateinit var notificationRepository: NotificationSettingsRepository

    @Inject
    lateinit var authRepository: AuthRepository

    companion object {
        private const val TAG = "FCMService"
        private const val APPOINTMENT_CHANNEL_ID = "appointment_notifications"
        private const val APPOINTMENT_CHANNEL_NAME = "Appointment Notifications"
        private const val APPOINTMENT_CHANNEL_DESCRIPTION = "Notifications for appointment updates and reminders"
        private const val CHAT_CHANNEL_ID = "chat_notifications"
        private const val CHAT_CHANNEL_NAME = "Chat Messages"
        private const val CHAT_CHANNEL_DESCRIPTION = "Notifications for new chat messages"
    }

    /**
     * Called when a new FCM token is generated.
     * Updates the token on the backend via [FCMTokenManager].
     *
     * @param token The new FCM token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "NEW_TOKEN: $token")
        runBlocking {
            fcmTokenManager.updateFCMToken()
        }
    }

    /**
     * Called when an FCM message is received.
     * Processes the message and displays appropriate notifications based on user role and settings.
     *
     * Supports two types of notifications:
     * - CHAT_MESSAGE: Notifications about new chat messages.
     * - Other types (appointment-related): Notifications about appointment status changes and reminders.
     *
     * @param remoteMessage The incoming Firebase Cloud Message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val role = authRepository.getCurrentUserRole()
                Log.d(TAG, "User role fetched: $role")

                val notificationType = remoteMessage.data["type"] ?: ""

                when (notificationType) {
                    "CHAT_MESSAGE" -> {
                        if (notificationRepository.shouldShowChatNotifications()) {
                            val settings = notificationRepository.getNotificationSettings(role)
                            if (role == Role.DOCTOR || role == Role.PATIENT) {
                                handleChatNotification(remoteMessage, settings.chatNotifications)
                            } else {
                                Log.d(TAG, "User role $role not eligible for chat notifications")
                            }
                        } else {
                            Log.d(TAG, "Chat notifications disabled")
                        }
                    }
                    else -> {
                        if (notificationRepository.shouldShowAppointmentNotifications()) {
                            val settings = notificationRepository.getNotificationSettings(role)
                            if (role == Role.DOCTOR || role == Role.PATIENT) {
                                handleAppointmentNotification(remoteMessage, settings.appointmentNotifications)
                            } else {
                                Log.d(TAG, "User role $role not eligible for appointment notifications")
                            }
                        } else {
                            Log.d(TAG, "Appointment notifications disabled")
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch role or handle notification: ${e.message}", e)
            }
        }
    }

    /**
     * Handles an incoming chat notification message and shows a notification.
     *
     * @param remoteMessage The incoming FCM message.
     * @param chatSettings User's chat notification settings.
     */
    private fun handleChatNotification(remoteMessage: RemoteMessage, chatSettings: ChatNotificationSettings) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "New Message"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val chatId = remoteMessage.data["chatId"] ?: ""
        val senderId = remoteMessage.data["senderId"] ?: ""
        val senderName = remoteMessage.data["senderName"] ?: ""
        val recipientId = remoteMessage.data["recipientId"] ?: ""

        showChatNotification(title, body, chatId, senderId, senderName, recipientId, chatSettings)
    }

    /**
     * Builds and shows a chat notification with user preferences.
     *
     * @param title Notification title.
     * @param body Notification body text.
     * @param chatId Unique chat identifier.
     * @param senderId Sender's user ID.
     * @param senderName Sender's display name.
     * @param recipientId Recipient's user ID.
     * @param chatSettings User's chat notification preferences.
     */
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
            .setContentTitle(if (chatSettings.showPreview) title else "New Message")
            .setContentText(if (chatSettings.showPreview) body else "You have a new message")
            .setStyle(NotificationCompat.BigTextStyle().bigText(if (chatSettings.showPreview) body else "You have a new message"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        if (chatSettings.sound) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        } else {
            notificationBuilder.setSound(null)
        }

        if (chatSettings.vibration) {
            notificationBuilder.setVibrate(longArrayOf(0, 250, 250, 250))
        } else {
            notificationBuilder.setVibrate(null)
        }

        val notification = notificationBuilder.build()
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(chatId.hashCode(), notification)
    }

    /**
     * Handles an incoming appointment notification message and shows a notification if enabled.
     *
     * @param remoteMessage The incoming FCM message.
     * @param appointmentSettings User's appointment notification settings.
     */
    private fun handleAppointmentNotification(remoteMessage: RemoteMessage, appointmentSettings: AppointmentNotificationSettings) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "CareConnect"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val type = remoteMessage.data["type"] ?: ""
        val appointmentId = remoteMessage.data["appointmentId"] ?: ""
        val userType = remoteMessage.data["userType"] ?: ""

        if (!shouldShowAppointmentType(type, appointmentSettings)) {
            Log.d(TAG, "Appointment type $type disabled - skipping notification")
            return
        }

        showAppointmentNotification(title, body, type, appointmentId, userType, appointmentSettings)
    }

    /**
     * Determines whether a specific appointment notification type should be shown based on user settings.
     *
     * @param type Appointment notification type (e.g. "CONFIRMED", "REMINDER").
     * @param settings User's appointment notification preferences.
     * @return `true` if the notification should be shown, `false` otherwise.
     */
    private fun shouldShowAppointmentType(type: String, settings: AppointmentNotificationSettings): Boolean {
        return when (type.uppercase()) {
            "CONFIRMED", "PENDING" -> settings.confirmations
            "REMINDER" -> settings.reminders
            "CANCELED", "CANCELLED" -> settings.cancellations
            "COMPLETED", "NO_SHOW" -> settings.completions
            else -> true
        }
    }

    /**
     * Builds and shows an appointment notification with user preferences.
     *
     * @param title Notification title.
     * @param body Notification body text.
     * @param type Appointment notification type.
     * @param appointmentId Unique appointment identifier.
     * @param userType User type (e.g. Doctor or Patient).
     * @param appointmentSettings User's appointment notification preferences.
     */
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

        if (appointmentSettings.sound) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        } else {
            notificationBuilder.setSound(null)
        }

        if (appointmentSettings.vibration) {
            notificationBuilder.setVibrate(longArrayOf(0, 1000, 500, 1000))
        } else {
            notificationBuilder.setVibrate(null)
        }

        val notification = notificationBuilder.build()
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(appointmentId.hashCode(), notification)
    }

    /**
     * Creates a notification channel if it does not already exist.
     *
     * @param channelId The ID of the channel.
     * @param name The user-visible name of the channel.
     * @param descriptionText The user-visible description of the channel.
     */
    private fun createNotificationChannel(channelId: String, name: String, descriptionText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel != null) return

            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = descriptionText
                enableLights(true)
                lightColor = android.graphics.Color.BLUE

                // Don't set channel-level sound and vibration - let individual notifications control this
                // This allows the per-notification settings to work properly
                enableVibration(false)  // Disable channel vibration
                setSound(null, null)    // Disable channel sound
            }

            notificationManager.createNotificationChannel(channel)
        }
    }
}
