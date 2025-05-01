package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.AppointmentDataSource
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppointmentRepository @Inject constructor(
    private val appointmentDataSource: AppointmentDataSource
) {

    suspend fun getAllAppointmentsByDate(date: String) =
        appointmentDataSource.getAllAppointmentsByDate(date)

    suspend fun getAllAppointmentsByMonth(date: String) =
        appointmentDataSource.getAllAppointmentsByMonth(date)

    fun getAppointmentsByPatientId(patientId: Flow<String?>) =
        appointmentDataSource.getAppointmentsByPatientId(patientId)

    fun getAppointmentsByDoctorId(doctorId: Flow<String?>) =
        appointmentDataSource.getAppointmentsByDoctorId(doctorId)

    fun getAppointments(patientId: Flow<String?>, doctorId: Flow<String?>) =
        appointmentDataSource.getAppointments(patientId, doctorId)

    suspend fun getDoctorAppointmentsByDate(doctorId: String?, date: String) =
        appointmentDataSource.getDoctorAppointmentsByDate(doctorId, date)

    suspend fun getPatientAppointmentsByDate(patientId: String?, date: String) =
        appointmentDataSource.getPatientAppointmentsByDate(patientId, date)

    suspend fun getAppointmentsByStatus(status: AppointmentStatus) =
        appointmentDataSource.getAppointmentsByStatus(status)

    suspend fun getDoctorAppointmentsByStatus(doctorId: String?, status: AppointmentStatus) =
        appointmentDataSource.getDoctorAppointmentsByStatus(doctorId, status)

    suspend fun getPatientAppointmentsByStatus(patientId: String?, status: AppointmentStatus) =
        appointmentDataSource.getPatientAppointmentsByStatus(patientId, status)

    suspend fun getAppointmentById(appointmentId: String) =
        appointmentDataSource.getAppointmentById(appointmentId)

    suspend fun createAppointment(appointment: Appointment): String {
       return appointmentDataSource.createAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: Appointment) {
        appointmentDataSource.updateAppointment(appointment)
    }

    suspend fun deleteAppointment(appointmentId: String) {
        appointmentDataSource.deleteAppointment(appointmentId)
    }
}