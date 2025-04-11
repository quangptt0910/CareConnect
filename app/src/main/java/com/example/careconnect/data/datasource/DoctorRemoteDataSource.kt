package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DoctorRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun createDoctor(email: String, password: String, doctor: Doctor) {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        println("DEBUG createDoctor: create user EMAIL PASSWORD authResult = $authResult")
        val doctorId = authResult.user?.uid ?: throw IllegalStateException("Failed to create user")
        println("DEBUG createDoctor: doctorId = $doctorId")
        val newDoctor = doctor.copy(id = doctorId)
        firestore.collection(DOCTORS_COLLECTION).document(doctorId).set(newDoctor).await()
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