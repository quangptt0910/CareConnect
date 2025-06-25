package com.example.careconnect.dataclass

import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.DocumentId

/**
 * Represents a scheduled appointment between a patient and a doctor.
 *
 * @property id Unique identifier for the appointment (Firestore document ID).
 * @property patientId Firebase UID of the patient.
 * @property doctorId Firebase UID of the doctor.
 * @property patientName Name of the patient.
 * @property doctorName Name of the doctor.
 * @property type Type of the appointment (e.g., Consultation, Checkup).
 * @property appointmentDate Date of the appointment (format: YYYY-MM-DD).
 * @property startTime Start time of the appointment (format: HH:mm).
 * @property endTime End time of the appointment (format: HH:mm).
 * @property address Location of the appointment.
 * @property status Current status of the appointment.
 */
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

/**
 * Represents the status of an appointment.
 *
 * @property title Human-readable title of the status.
 * @property color UI color representing the status.
 * @property value Numeric value for comparison or sorting.
 */
enum class AppointmentStatus(val title: String, val color: Color, val value: Int) {
    PENDING("Pending", Color(0xFFDEB01E), 0),    // Appointment is scheduled but not yet confirmed
    CONFIRMED("Confirmed",Color(0xFF1367C1), 1),   // Appointment has been confirmed by the doctor
    COMPLETED("Completed", Color(0xFF02A552), 2),  // Appointment has been completed
    CANCELED("Canceled", Color(0xFFC61C19), 3),   // Appointment has been canceled
    NO_SHOW("No Show", Color(0xFF4B4848), 4)     // Patient did not show up for the appointment
}

