package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.TimeSlot
import com.example.careconnect.dataclass.toDateString
import com.example.careconnect.dataclass.toLocalDate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class DoctorRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    suspend fun createDoctor(email: String, password: String, doctorData: Map<String, Any>): Pair<String, String> {
        val data = mapOf(
            "email" to email,
            "password" to password,
            "doctorData" to doctorData
        )

        val result = functions.getHttpsCallable("createDoctor")
            .call(data)
            .await()

        @Suppress("UNCHECKED_CAST")
        val success = result.data as Map<String, Any>

        val message = success["message"] as String
        val doctorId = success["doctorId"] as String

        return message to doctorId
    }

    suspend fun updateDoctor(doctor: Doctor) {
        firestore.collection(DOCTORS_COLLECTION).document(doctor.id).set(doctor).await()
    }

    suspend fun getAllDoctors(): List<Doctor> {
        val snapshot = firestore.collection(DOCTORS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
            .get().await()
        return snapshot.documents.mapNotNull { document ->
            try {
                val doctor = document.toObject(Doctor::class.java)
                doctor?.copy(id = document.id)
            } catch (e: Exception) {
                Log.e("DoctorRemoteDataSource", "Error converting document to Doctor", e)
                null
            }
        }
    }

    fun getAllDoctorsFlow(): Flow<List<Doctor>> {
        return firestore.collection(DOCTORS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
            .dataObjects()
    }

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return firestore.collection(DOCTORS_COLLECTION).document(doctorId).get().await().toObject(Doctor::class.java)
    }

    suspend fun saveWorkingDays(doctorId: String, selectedDate: Set<LocalDate>) {
        val db = firestore.collection(DOCTORS_COLLECTION).document(doctorId)
        val workingDays = selectedDate.associate { date ->
            date.toDateString() to listOf(
                TimeSlot("09:00", "12:00"),
                TimeSlot("14:00", "18:00")
            )
        }

        val schedule = hashMapOf("workingDays" to workingDays)

        db.update("schedule", schedule).await()
    }

    /**
     * https://firebase.google.com/docs/firestore/query-data/listen#kotlin
     */
    fun getWorkingDays(doctorId: String): Flow<Set<LocalDate>> = callbackFlow {
        val db = firestore.collection(DOCTORS_COLLECTION).document(doctorId)

        val registration = db.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(emptySet())
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val doctor = snapshot.toObject(Doctor::class.java)
                val workingDays = doctor?.schedule?.workingDays ?: emptyMap()
                val dates = workingDays.keys.map { dateString ->
                    dateString.toLocalDate()
                }.toSet()

                trySend(dates)
            } else {
                trySend(emptySet())
            }
        }

        awaitClose { registration.remove() }
    }

    companion object {
        private const val DOCTORS_COLLECTION = "doctors"
    }
}