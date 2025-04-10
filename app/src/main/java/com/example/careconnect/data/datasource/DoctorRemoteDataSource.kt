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
    suspend fun createDoctor(doctor: Doctor): String {
        return firestore.collection(DOCTORS_COLLECTION).add(doctor).await().id
    }

    suspend fun updateDoctor(doctor: Doctor) {
        firestore.collection(DOCTORS_COLLECTION).document(doctor.id).set(doctor).await()
    }

    suspend fun signupDoctor(email: String, password: String) {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val userId = auth.currentUser!!.uid
        val doctor = Doctor(id = userId, email = email)
        firestore.collection("doctors").document(userId).set(doctor).await()
    }

    companion object {
        private const val DOCTORS_COLLECTION = "doctors"
    }
}