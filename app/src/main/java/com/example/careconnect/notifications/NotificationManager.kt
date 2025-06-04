package com.example.careconnect.notifications

import android.util.Log
import com.example.careconnect.dataclass.Appointment
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
        private const val NOTIFICATION_TRIGGERS_COLLECTION = "notification_triggers"
    }

    // This will trigger Firebase Functions via Firestore write
    suspend fun triggerAppointmentNotification(appointment: Appointment, notificationType: String): Boolean {
        return try {
            if (appointment.id.isEmpty()) {
                Log.e(TAG, "Cannot trigger notification: appointment ID is empty")
                return false
            }
            val notificationTrigger = mapOf(
                "type" to notificationType,
                "appointmentId" to appointment.id,
                "patientId" to appointment.patientId,
                "doctorId" to appointment.doctorId,
                "patientName" to appointment.patientName,
                "doctorName" to appointment.doctorName,
                "appointmentDate" to appointment.appointmentDate,
                "startTime" to appointment.startTime,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "processed" to false
            )

            // This write will trigger the Firebase Function
            val docRef = firestore.collection("notification_triggers")
                .add(notificationTrigger)
                .await()

            Log.d(TAG, "Notification trigger created successfully with ID: ${docRef.id} for type: $notificationType")
            true
        } catch (e: Exception) {
            Log.e("NotificationManager", "Failed to create notification trigger", e)
            false
        }
    }

    //  Method to check if notification was processed
    suspend fun checkNotificationStatus(notificationId: String): NotificationStatus? {
        return try {
            val doc = firestore.collection(NOTIFICATION_TRIGGERS_COLLECTION)
                .document(notificationId)
                .get()
                .await()

            if (doc.exists()) {
                val data = doc.data!!
                NotificationStatus(
                    processed = data["processed"] as? Boolean == true,
                    error = data["error"] as? String,
                    sentAt = data["sentAt"]
                )
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check notification status", e)
            null
        }
    }
}

data class NotificationStatus(
    val processed: Boolean,
    val error: String?,
    val sentAt: Any? // Firestore timestamp
)