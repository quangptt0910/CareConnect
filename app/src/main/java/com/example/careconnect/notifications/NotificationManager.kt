package com.example.careconnect.notifications

import android.util.Log
import com.example.careconnect.dataclass.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // This will trigger Firebase Functions via Firestore write
    suspend fun triggerAppointmentNotification(appointment: Appointment, notificationType: String) {
        try {
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
            firestore.collection("notification_triggers")
                .add(notificationTrigger)
                .await()

            Log.d("NotificationManager", "Notification trigger created")
        } catch (e: Exception) {
            Log.e("NotificationManager", "Failed to create notification trigger", e)
        }
    }
}