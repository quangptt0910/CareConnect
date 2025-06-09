package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.toLocalDate
import com.example.careconnect.notifications.NotificationManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.time.YearMonth
import javax.inject.Inject

class AppointmentDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notification: NotificationManager
){

    suspend fun getAllAppointments(): List<Appointment> {
        return firestore
            .collection("appointments")
            .get()
            .await()
            .toObjects(Appointment::class.java)
    }

    suspend fun getAllAppointmentsByDate(date: String): List<Appointment> {
        return firestore
            .collection("appointments")
            .whereEqualTo("appointmentDate", date)
            .get()
            .await().toObjects(Appointment::class.java)
    }

    suspend fun getAllAppointmentsByMonth(date: String): List<Appointment> {
        val local = date.toLocalDate()
        val ym = YearMonth.of(local.year, local.month)
        val start = ym.atDay(1)
        val end = ym.plusMonths(1).atDay(1)

        return firestore
            .collection("appointments")
            .whereGreaterThanOrEqualTo("appointmentDate", start)
            .whereLessThan("appointmentDate", end)
            .get()
            .await().toObjects(Appointment::class.java)
    }

    suspend fun getAllPatientAppointments(patientId: String): List<Appointment> {
        return firestore
            .collection("appointments")
            .whereEqualTo("patientId", patientId)
            .get()
            .await().toObjects(Appointment::class.java)
    }

    suspend fun getAllDoctorAppointments(doctorId: String): List<Appointment> {
        return firestore
            .collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .get()
            .await().toObjects(Appointment::class.java)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAppointmentsByPatientId(currentUserIdFlow: Flow<String?>): Flow<List<Appointment>> {
        return currentUserIdFlow.flatMapLatest { userId ->
            firestore
                .collection("appointments")
                .whereEqualTo("patientId", userId)
                .dataObjects()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAppointmentsByDoctorId(currentUserIdFlow: Flow<String?>): Flow<List<Appointment>> {
        return currentUserIdFlow.flatMapLatest { userId ->
            firestore
                .collection("appointments")
                .whereEqualTo("doctorId", userId)
                .dataObjects()
        }
    }

    /**
     * Get appointments by patientId and doctorId
     * @param patientIdFlow Flow of patientId
     * @param doctorIdFlow Flow of doctorId
     * @return Flow of list of appointments
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAppointments(patientIdFlow: Flow<String?>, doctorIdFlow: Flow<String?>): Flow<List<Appointment>> {
        return combine(patientIdFlow, doctorIdFlow) { patientId, doctorId ->
            Pair(patientId, doctorId)
        }.flatMapLatest { (patientId, doctorId) ->
            if (patientId == null || doctorId == null) {
                flowOf(emptyList())
            } else {
                firestore
                    .collection("appointments")
                    .whereEqualTo("patientId", patientId)
                    .whereEqualTo("doctorId", doctorId)
                    .dataObjects()
            }
        }
    }

    // Get doctor appointments by a date
    suspend fun getDoctorAppointmentsByDate(doctorId: String?, date: String): List<Appointment> {
        return if (doctorId == null) {
            emptyList()
        } else {
            firestore
                .collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("appointmentDate", date)
                .get()
                .await()
                .toObjects(Appointment::class.java)
        }
    }

    // Get patient appointments by a date
    suspend fun getPatientAppointmentsByDate(patientId: String?, date: String): List<Appointment> {
        return firestore
                .collection("appointments")
                .whereEqualTo("doctorId", patientId)
                .whereEqualTo("appointmentDate", date)
                .get()
                .await()
                .toObjects(Appointment::class.java)
    }

    suspend fun getPatientAppointmentsByMonth(patientId: String?, date: String): List<Appointment> {
        val local = date.toLocalDate()
        val ym = YearMonth.of(local.year, local.month)
        val start = ym.atDay(1).toString()
        val end = ym.plusMonths(1).atDay(1).toString()

        return firestore
            .collection("appointments")
            .whereEqualTo("patientId", patientId)
            .get()
            .await().toObjects(Appointment::class.java)

    }

    suspend fun getDoctorAppointmentsByMonth(doctorId: String?, date: String): List<Appointment> {
        val local = date.toLocalDate()
        val ym = YearMonth.of(local.year, local.month)
        val start = ym.atDay(1).toString()
        val end = ym.plusMonths(1).atDay(1).toString()
        return firestore
            .collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .get()
            .await().toObjects(Appointment::class.java)
    }
    // Get appointments by status
    suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<Appointment> {
        return firestore
            .collection("appointments")
            .whereEqualTo("status", status.name)
            .get()
            .await()
            .toObjects(Appointment::class.java)
    }

    // Get doctor appointments by status
    suspend fun getDoctorAppointmentsByStatus(doctorId: String?, status: AppointmentStatus): List<Appointment> {
        println("DEBUG: getting appt by status $status for doctor $doctorId")
        return firestore
                .collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("status", status.name)
                .get()
                .await()
                .toObjects(Appointment::class.java)
    }

    suspend fun getDoctorAppointmentsUpcoming(doctorId: String?, date: String): List<Appointment> {
        return firestore
                .collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereGreaterThanOrEqualTo("appointmentDate", date)
                .get()
                .await()
                .toObjects(Appointment::class.java)
    }

    suspend fun getPatientAppointmentsByStatus(patientId: String?, status: AppointmentStatus): List<Appointment> {
        return firestore
                .collection("appointments")
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("status", status)
                .get()
                .await()
                .toObjects(Appointment::class.java)
    }


    suspend fun getAppointmentById(appointmentId: String): Appointment? {
        return firestore.collection("appointments").document(appointmentId).get().await().toObject(Appointment::class.java)
    }

    suspend fun createAppointment(appointment: Appointment): String {
       return try {
           val appointmentForSave = appointment.copy(id = "", status = AppointmentStatus.PENDING)

           val docRef = firestore.collection("appointments").add(appointmentForSave).await()

           val savedAppointment = appointment.copy(id = docRef.id)

           notification.triggerAppointmentNotification(savedAppointment, "PENDING")

           return docRef.id
       }
       catch (e: Exception) {
           Log.e("AppointmentRepository", "Failed to create appointment", e)
           ""
       }
    }

    suspend fun updateAppointment(appointment: Appointment) {
        try {
            firestore.collection("appointments").document(appointment.id)
                .update(
                    mapOf(
                        "patientId" to appointment.patientId,
                        "doctorId" to appointment.doctorId,
                        "patientName" to appointment.patientName,
                        "doctorName" to appointment.doctorName,
                        "type" to appointment.type,
                        "appointmentDate" to appointment.appointmentDate,
                        "startTime" to appointment.startTime,
                        "endTime" to appointment.endTime,
                        "address" to appointment.address,
                        "status" to appointment.status
                    )
                )
                .await()

            notification.triggerAppointmentNotification(appointment, appointment.status.name)

            Log.d("AppointmentRepository", "Appointment updated successfully")
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Failed to update appointment", e)
        }
    }

    suspend fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus): Boolean {
        return try {
            firestore.collection("appointments").document(appointmentId).update("status", newStatus).await()
            val appointment = getAppointmentById(appointmentId)

            appointment?.let {
                val notificationType = when (newStatus) {
                    AppointmentStatus.CONFIRMED -> "CONFIRMED"
                    AppointmentStatus.COMPLETED -> "COMPLETED"
                    AppointmentStatus.CANCELED -> "CANCELED"
                    AppointmentStatus.NO_SHOW -> "NO_SHOW"
                    else -> return@let
                }
                notification.triggerAppointmentNotification(it, notificationType)
            }
            true
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Failed to update appointment status", e)
            false
        }
    }

    suspend fun deleteAppointment(appointmentId: String) {
        firestore.collection("appointments").document(appointmentId).delete().await()
    }
}