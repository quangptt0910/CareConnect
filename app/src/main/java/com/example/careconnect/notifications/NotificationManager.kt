package com.example.careconnect.notifications

import android.util.Log
import com.example.careconnect.dataclass.Appointment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "NotificationManager"
    }

    // This will trigger Firebase Functions via Firestore write
    suspend fun triggerAppointmentNotification(appointment: Appointment, notificationType: String): Boolean {
        return try {

            if (appointment.id.isEmpty() ||
                appointment.patientId.isEmpty() ||
                appointment.doctorId.isEmpty() ||
                appointment.patientName.isEmpty() ||
                appointment.doctorName.isEmpty()) {
                Log.e(TAG, "Cannot trigger notification: missing required appointment data")
                return false
            }

            val validTypes = listOf("PENDING", "CONFIRMED", "COMPLETED", "CANCELED", "NO_SHOW")
            if (notificationType !in validTypes) {
                Log.e(TAG, "Invalid notification type: $notificationType")
                return false
            }

            val notificationTrigger = mapOf(
                "type" to notificationType,
                "appointmentId" to appointment.id,
                "patientId" to appointment.patientId,
                "doctorId" to appointment.doctorId,
                "patientName" to appointment.patientName,
                "doctorName" to appointment.doctorName,
                "appointmentDate" to appointment.appointmentDate.ifEmpty { "N/A" },
                "startTime" to appointment.startTime.ifEmpty { "N/A" },
                "timestamp" to FieldValue.serverTimestamp(),
                "processed" to false
            )

            // This write will trigger the Firebase Function
            val docRef = firestore.collection("notification_triggers")
                .add(notificationTrigger)
                .await()

            Log.d(TAG, "✅ Notification trigger created successfully with ID: ${docRef.id} for type: $notificationType")
            println("DEBUG: ✅ Notification trigger created successfully with ID: ${docRef.id} for type: $notificationType")

            true
        } catch (e: Exception) {
            Log.e("NotificationManager", "Failed to create notification trigger", e)
            false
        }
    }

    suspend fun triggerChatNotification(
        chatId: String,
        message: String,
        senderId: String,
        senderName: String,
        recipientId: String
    ): Boolean {
        return try {
            val notificationTrigger = mapOf(
                "type" to "CHAT_MESSAGE",
                "chatId" to chatId,
                "message" to message,
                "senderId" to senderId,
                "senderName" to senderName,
                "recipientId" to recipientId,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("chat_notifications")
                .add(notificationTrigger)
                .await()

            Log.d(TAG, "✅ Chat notification triggered successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to trigger chat notification", e)
            false
        }
    }

}

