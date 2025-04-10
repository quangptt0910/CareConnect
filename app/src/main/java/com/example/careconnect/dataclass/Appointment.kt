package com.example.careconnect.dataclass

import androidx.compose.ui.graphics.Color

data class Appointment(
    val id: String = "",  // Firestore document ID (optional)
    val patientId: String = "",  // Firebase UID of the patient
    val doctorId: String = "",  // Firebase UID of the doctor

    val appointmentDate: String = "",  // Date of the appointment (e.g., "2025-03-10")
    val startTime: String = "",  // Start time of the appointment (e.g., "09:00 AM")
    val endTime: String = "",    // End time of the appointment (e.g., "09:15 AM")
    val address: String = "",
    val appointmentStatus: AppointmentStatus = AppointmentStatus.PENDING,  // Status of the appointment (e.g., PENDING, COMPLETED, CANCELED)
)

enum class AppointmentStatus(val title: String, val color: Color, val value: Int) {
    PENDING("Pending", Color(0xFFFFC107), 0),    // Appointment is scheduled but not yet confirmed
    COMPLETED("Completed", Color(0xFF00E676), 1),  // Appointment has been completed
    CANCELED("Canceled", Color(0xFFE53935), 2),   // Appointment has been canceled
    NO_SHOW("No Show", Color(0xFF757575), 3)     // Patient did not show up for the appointment
}

data class AppointmentDetails (
    val appointment: Appointment,
    val doctor: Doctor,
    val patient: Patient
)

