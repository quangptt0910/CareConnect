package com.example.careconnect.dataclass

import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.DocumentId

data class Appointment(
    @DocumentId val id: String = "",  // Firestore document ID (optional)
    val patientId: String = "",  // Firebase UID of the patient
    val doctorId: String = "",  // Firebase UID of the doctor
    val patientName: String = "",  // Name of the patient
    val doctorName: String = "",  // Name of the doctor
    val type: String = "",  // Type of the appointment (e.g., "Consultation", "Checkup")
    val appointmentDate: String = "",  // Date of the appointment (e.g., "2025-03-10")
    val startTime: String = "",  // Start time of the appointment (e.g., "09:00")
    val endTime: String = "",    // End time of the appointment (e.g., "09:30")
    val address: String = "",
    val status: AppointmentStatus = AppointmentStatus.PENDING,  // Status of the appointment (e.g., PENDING, COMPLETED, CANCELED)
)

enum class AppointmentStatus(val title: String, val color: Color, val value: Int) {
    PENDING("Pending", Color(0xFFDEB01E), 0),    // Appointment is scheduled but not yet confirmed
    COMPLETED("Completed", Color(0xFF02A552), 1),  // Appointment has been completed
    CANCELED("Canceled", Color(0xFFC61C19), 2),   // Appointment has been canceled
    NO_SHOW("No Show", Color(0xFF4B4848), 3)     // Patient did not show up for the appointment
}

