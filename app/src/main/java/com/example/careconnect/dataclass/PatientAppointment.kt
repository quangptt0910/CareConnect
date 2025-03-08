package com.example.careconnect.dataclass

data class PatientAppointment(
    val id: String = "",  // Firestore document ID (optional)
    val patientId: String = "",  // Firebase UID of the patient
    val doctorId: String = "",  // Firebase UID of the doctor
    val patientName: String = "",
    val patientSurname: String = "",
    val doctorName: String = "",
    val doctorSurname: String = "",
    val doctorSpecialty: String = "",

    val appointmentDate: String = "",  // Date of the appointment (e.g., "2025-03-10")
    val startTime: String = "",  // Start time of the appointment (e.g., "09:00 AM")
    val endTime: String = "",    // End time of the appointment (e.g., "09:15 AM")

    val appointmentStatus: AppointmentStatus = AppointmentStatus.PENDING,  // Status of the appointment (e.g., PENDING, COMPLETED, CANCELED)
    val createdAt: com.google.firebase.Timestamp? = null,  // Timestamp of when the appointment was created
    val updatedAt: com.google.firebase.Timestamp? = null,  // Timestamp of when the appointment was last updated
)

enum class AppointmentStatus {
    PENDING,    // Appointment is scheduled but not yet confirmed
    COMPLETED,  // Appointment has been completed
    CANCELED,   // Appointment has been canceled
    NO_SHOW     // Patient did not show up for the appointment
}

