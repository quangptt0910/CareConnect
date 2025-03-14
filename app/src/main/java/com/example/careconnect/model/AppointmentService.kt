package com.example.careconnect.model

import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import java.time.LocalDateTime

interface AppointmentService {
    suspend fun bookAppointment(appointment: Appointment): Result<String>

    suspend fun cancelAppointment(appointmentId: String): Result<Unit>

    suspend fun rescheduleAppointment(appointmentId: String, newDateTime: LocalDateTime): Result<Unit>

    suspend fun getPatientAppointments(patientId: String): Result<List<Appointment>>

    suspend fun getDoctorAppointments(doctorId: String): Result<List<Appointment>>

    suspend fun getAppointmentDetails(appointmentId: String): Result<Appointment> // or AppointmentDetails?

    suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus): Result<Unit>
}