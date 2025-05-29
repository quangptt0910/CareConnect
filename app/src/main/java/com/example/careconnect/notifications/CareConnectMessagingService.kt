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
import javax.inject.Inject

@AndroidEntryPoint
class CareConnectMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FCMTokenManager

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "appointment_notifications"
        private const val CHANNEL_NAME = "Appointment Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for appointment updates and reminders"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
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

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "CareConnect"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val notificationType = remoteMessage.data["type"] ?: ""
        val appointmentId = remoteMessage.data["appointmentId"] ?: ""
        val userType = remoteMessage.data["userType"] ?: ""

        showNotification(title, body, notificationType, appointmentId, userType)
    }

    private fun showNotification(title: String, body: String, type: String, appointmentId: String, userType: String) {
        val channelId = "appointment_notifications"
        createNotificationChannel()

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

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notifications_24px)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = appointmentId.hashCode()
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }
}