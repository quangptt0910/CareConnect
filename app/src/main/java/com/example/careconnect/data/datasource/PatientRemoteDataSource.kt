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

    suspend fun getPatientId(): String {
        return auth.currentUser?.uid ?: ""
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
            throw e
        }
    }

    suspend fun createPrescription(patientId: String, prescription: Prescription) {
        try {
            Log.d("Firestore", "Creating prescription for patient: $patientId")
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

    suspend fun getPrescriptions(patientId: String): List<Prescription> {
        val prescriptions = mutableListOf<Prescription>()
        try {
            val querySnapshot = firestore.collection("patients")
                .document(patientId)
                .collection("prescriptions")
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val prescription = document.toObject(Prescription::class.java)
                prescription?.let { prescriptions.add(it) }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch prescriptions", e)
            throw e // or handle accordingly
        }
        return prescriptions
    }

    suspend fun getMedicalReports(patientId: String): List<MedicalReport> {
        val medicalReports = mutableListOf<MedicalReport>()
        try {
            val querySnapshot = firestore.collection("patients")
                .document(patientId)
                .collection("medicalReports")
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val medicalReport = document.toObject(MedicalReport::class.java)
                medicalReport?.let { medicalReports.add(it) }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch medical reports", e)
            throw e // or handle accordingly
        }
        return medicalReports
    }

    /*
     * Adding Function for Medical History Entries
     */
    suspend fun addMedication(patientId: String, medication: Medication): String {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medications")
                .add(medication)
                .await()
                .id
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add medication", e)
            throw e
        }
    }

    suspend fun addAllergy(patientId: String, allergy: Allergy): String {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("allergies")
                .add(allergy)
                .await()
                .id
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add medication", e)
            throw e
        }
    }
    suspend fun addCondition(patientId: String, condition: Condition): String {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("conditions")
                .add(condition)
                .await()
                .id
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add medication", e)
            throw e
        }
    }
    suspend fun addSurgery(patientId: String, surgery: Surgery): String {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("surgeries")
                .add(surgery)
                .await()
                .id
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add medication", e)
            throw e
        }
    }
    suspend fun addImmunization(patientId: String, immunization: Immunization): String {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("immunizations")
                .add(immunization)
                .await()
                .id
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add medication", e)
            throw e
        }
    }

    /*
     * Updating Function for Medical History Entries
     */
    suspend fun updateMedication(patientId: String, medication: Medication): Boolean {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medications")
                .document(medication.id)
                .set(medication)
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to update medication", e)
            false
        }
    }

    suspend fun updateAllergy(patientId: String, allergy: Allergy): Boolean {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("allergies")
                .document(allergy.id)
                .set(allergy)
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to update medication", e)
            false
        }
    }

    suspend fun updateCondition(patientId: String, condition: Condition): Boolean {
        return try {
            firestore
                .collection("patients")
                .document(patientId)
                .collection("conditions")
                .document(condition.id)
                .set(condition)
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to update medication", e)
            false
        }
    }

    suspend fun updateSurgery(patientId: String, surgery: Surgery): Boolean {
        return try {
            firestore
                .collection("patients")
                .document(patientId)
                .collection("surgeries")
                .document(surgery.id)
                .set(surgery)
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to update medication", e)
            false
        }
    }

    suspend fun updateImmunization(patientId: String, immunization: Immunization): Boolean {
        return try {
            firestore
                .collection("patients")
                .document(patientId)
                .collection("immunizations")
                .document(immunization.id)
                .set(immunization)
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to update medication", e)
            false
        }
    }

    /*
     * GET medical history entries
     */
    suspend fun getMedications(patientId: String): List<Medication> {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medications")
                .get()
                .await()
                .toObjects(Medication::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch medications", e)
            emptyList()
        }
    }

    suspend fun getAllergies(patientId: String): List<Allergy> {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("allergies")
                .get()
                .await()
                .toObjects(Allergy::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch allergies", e)
            emptyList()
        }
    }

    suspend fun getConditions(patientId: String): List<Condition> {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("conditions")
                .get()
                .await()
                .toObjects(Condition::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch conditions", e)
            emptyList()
        }
    }

    suspend fun getSurgeries(patientId: String): List<Surgery> {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("surgeries")
                .get()
                .await()
                .toObjects(Surgery::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch surgeries", e)
            emptyList()
        }
    }

    suspend fun getImmunizations(patientId: String): List<Immunization> {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("immunizations")
                .get()
                .await()
                .toObjects(Immunization::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch immunizations", e)
            emptyList()
        }
    }

    /*
     * Deleting Function for Medical History Entries
     */
    suspend fun deleteMedication(patientId: String, medication: Medication): Boolean {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("medications")
                .document(medication.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to delete medication", e)
            false
        }
    }

    suspend fun deleteAllergy(patientId: String, allergy: Allergy): Boolean {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("allergies")
                .document(allergy.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to delete medication", e)
            false
        }
    }

    suspend fun deleteCondition(patientId: String, condition: Condition): Boolean {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("conditions")
                .document(condition.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to delete medication", e)
            false
        }
    }

    suspend fun deleteSurgery(patientId: String, surgery: Surgery): Boolean {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("surgeries")
                .document(surgery.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to delete medication", e)
            false
        }
    }

    suspend fun deleteImmunization(patientId: String, immunization: Immunization): Boolean {
        return try {
            firestore.collection("patients")
                .document(patientId)
                .collection("immunizations")
                .document(immunization.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to delete medication", e)
            false
        }
    }
}
