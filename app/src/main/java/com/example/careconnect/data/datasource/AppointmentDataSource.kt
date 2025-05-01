package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.toLocalDate
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
){
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

    // Get appointments by status
    suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<Appointment> {
        return firestore
            .collection("appointments")
            .whereEqualTo("appointmentStatus", status)
            .get()
            .await()
            .toObjects(Appointment::class.java)
    }

    // Get doctor appointments by status
    suspend fun getDoctorAppointmentsByStatus(doctorId: String?, status: AppointmentStatus): List<Appointment> {
        return  firestore
                .collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("appointmentStatus", status)
                .get()
                .await()
                .toObjects(Appointment::class.java)

    }

    suspend fun getPatientAppointmentsByStatus(patientId: String?, status: AppointmentStatus): List<Appointment> {
        return firestore
                .collection("appointments")
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("appointmentStatus", status)
                .get()
                .await()
                .toObjects(Appointment::class.java)
    }


    suspend fun getAppointmentById(appointmentId: String): Appointment? {
        return firestore.collection("appointments").document(appointmentId).get().await().toObject(Appointment::class.java)
    }

    suspend fun createAppointment(appointment: Appointment): String {
       return firestore.collection("appointments").add(appointment).await().id
    }

    suspend fun updateAppointment(appointment: Appointment) {
        firestore.collection("appointments").document(appointment.id).set(appointment).await()
    }

    suspend fun deleteAppointment(appointmentId: String) {
        firestore.collection("appointments").document(appointmentId).delete().await()
    }
}