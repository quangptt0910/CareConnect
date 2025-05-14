package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.MedicalReport
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Prescription
import com.example.careconnect.dataclass.Surgery
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

    suspend fun createMedicalReport(patientId: String, medicalReport: MedicalReport) {
        try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medicalReports")
                .add(medicalReport)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to create medical report", e)
            throw e // or handle accordingly
        }
    }

    suspend fun createPrescription(patientId: String, prescription: Prescription) {
        try {
            firestore.collection("patients")
                .document(patientId)
                .collection("prescriptions")
                .add(prescription)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to create prescription", e)
            throw e // or handle accordingly
        }
    }

    suspend fun addMedication(patientId: String, medication: Medication) {
        try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .add(medication)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add medication", e)
            throw e
        }
    }

    suspend fun setMedicines(patientId: String): MutableList<Medication> {
        val medications = mutableListOf<Medication>()
        try {
            val querySnapshot = firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .whereEqualTo("type", "MEDICATION")
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val medication = document.toObject(Medication::class.java)
                medication?.let { medications.add(it) }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to set medicines", e)
            throw e
        }
        return medications
    }

    suspend fun addAllergy(patientId: String, allergy: Allergy) {
        try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .add(allergy)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add allergy", e)
            throw e // or handle accordingly
        }
    }

    suspend fun setAllergies(patientId: String): MutableList<Allergy> {
        val allergies = mutableListOf<Allergy>()
        try {
            val querySnapshot = firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .whereEqualTo("type", "ALLERGY")
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val allergy = document.toObject(Allergy::class.java)
                allergy?.let { allergies.add(it) }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to set allergies", e)
            throw e // or handle accordingly
        }
        return allergies
    }

    suspend fun addConditions(patientId: String, condition: Condition) {
        try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .add(condition)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add condition", e)
            throw e // or handle accordingly
        }
    }

    suspend fun setConditions(patientId: String): MutableList<Condition> {
        val conditions = mutableListOf<Condition>()
        try {
            val querySnapshot = firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .whereEqualTo("type", "CONDITION")
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val condition = document.toObject(Condition::class.java)
                condition?.let { conditions.add(it) }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to set conditions", e)
            throw e // or handle accordingly
        }
        return conditions
    }

    suspend fun addSurgery(patientId: String, surgery: Surgery) {
        try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .add(surgery)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add surgery", e)
            throw e // or handle accordingly
        }
    }

    suspend fun setSurgeries(patientId: String): MutableList<Surgery> {
        val surgeries = mutableListOf<Surgery>()
        try {
            val querySnapshot = firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .whereEqualTo("type", "SURGERY")
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val surgery = document.toObject(Surgery::class.java)
                surgery?.let { surgeries.add(it) }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to set surgeries", e)
            throw e // or handle accordingly
        }
        return surgeries
    }

    suspend fun addImmunization(patientId: String, immunization: Immunization) {
        try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .add(immunization)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add immunization", e)
            throw e // or handle accordingly
        }
    }

    suspend fun setImmunizations(patientId: String): MutableList<Immunization> {
        val immunizations = mutableListOf<Immunization>()

        try {
            val querySnapshot = firestore.collection("patients")
                .document(patientId)
                .collection("medicalHistory")
                .whereEqualTo("type", "IMMUNIZATION")
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val immunization = document.toObject(Immunization::class.java)
                immunization?.let { immunizations.add(it) }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to set immunizations", e)
            throw e // or handle accordingly
            }
        return immunizations
    }
}