package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
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

    suspend fun getDoctors(): List<Doctor> {
        return firestore.collection(DOCTORS_COLLECTION).get().await().toObjects(Doctor::class.java)
    }

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return firestore.collection(DOCTORS_COLLECTION).document(doctorId).get().await().toObject(Doctor::class.java)
    }

    companion object {
        private const val DOCTORS_COLLECTION = "doctors"
    }
}