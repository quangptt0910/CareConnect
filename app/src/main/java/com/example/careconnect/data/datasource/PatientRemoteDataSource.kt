package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class PatientRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){
    suspend fun getPatientById(patientId: String): Patient? {
        return try {
            val snapshot = firestore.collection("patients")
                .document(patientId)
                .get()
                .await()
            snapshot.toObject(Patient::class.java)
        } catch (e: Exception) {
            null
        }
    }
}