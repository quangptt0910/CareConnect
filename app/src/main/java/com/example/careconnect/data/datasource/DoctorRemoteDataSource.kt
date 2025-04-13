package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DoctorRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    suspend fun createDoctor(email: String, password: String, doctorData: Map<String, Any>) {
        val data = mapOf(
            "email" to email,
            "password" to password,
            "doctorData" to doctorData
        )

        functions.getHttpsCallable("createDoctor")
            .call(data)
            .await()
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

    companion object {
        private const val DOCTORS_COLLECTION = "doctors"
    }
}