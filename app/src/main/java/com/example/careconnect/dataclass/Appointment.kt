package com.example.careconnect.dataclass

data class Appointment(
    val id: String = "",  // Firestore document ID (optional)
    val patientId: String = "",  // Firebase UID of the patient
    val doctorId: String = "",  // Firebase UID of the doctor

    val appointmentDate: String = "",  // Date of the appointment (e.g., "2025-03-10")
    val startTime: String = "",  // Start time of the appointment (e.g., "09:00 AM")
    val endTime: String = "",    // End time of the appointment (e.g., "09:15 AM")

    val appointmentStatus: AppointmentStatus = AppointmentStatus.PENDING,  // Status of the appointment (e.g., PENDING, COMPLETED, CANCELED)
)

enum class AppointmentStatus {
    PENDING,    // Appointment is scheduled but not yet confirmed
    COMPLETED,  // Appointment has been completed
    CANCELED,   // Appointment has been canceled
    NO_SHOW     // Patient did not show up for the appointment
}

data class AppointmentDetails (
    val appointment: Appointment,
    val doctor: Doctor, // TODO() change the doctor type as new User class
    val patient: Patient
)