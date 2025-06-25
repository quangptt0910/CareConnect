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


/**
 * A remote data source for managing patient-related data in Firestore.
 *
 * This class handles CRUD operations for patient profiles, medical reports, prescriptions,
 * and various types of medical history entries such as medications, allergies, conditions,
 * surgeries, and immunizations.
 *
 * @property auth FirebaseAuth instance used to access current user ID.
 * @property firestore FirebaseFirestore instance used for Firestore operations.
 */
class PatientRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){
    /**
     * Retrieves a [Patient] object from Firestore by their ID.
     *
     * @param patientId ID of the patient to fetch.
     * @return The [Patient] object if found, or null.
     */
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

    /**
     * Returns the current authenticated user's ID.
     *
     * @return User ID or an empty string if not authenticated.
     */
    suspend fun getPatientId(): String {
        return auth.currentUser?.uid ?: ""
    }

    /**
     * Creates a new [MedicalReport] for a patient.
     *
     * @param patientId ID of the patient.
     * @param medicalReport The medical report to be added.
     */
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

    /**
     * Creates a new [Prescription] for a patient.
     *
     * @param patientId ID of the patient.
     * @param prescription The prescription to be added.
     */
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

    /**
     * Retrieves a list of [Prescription]s for a given patient.
     *
     * @param patientId ID of the patient.
     * @return List of prescriptions.
     */
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

    /**
     * Retrieves a list of [MedicalReport]s for a given patient.
     *
     * @param patientId ID of the patient.
     * @return List of medical reports.
     */
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

    /**
     * Adds a new [Medication] entry for a patient.
     *
     * @return ID of the newly added document.
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

    /**
     * Adds a new [Allergy] entry for a patient.
     *
     * @return ID of the newly added document.
     */
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

    /**
     * Adds a new [Condition] entry for a patient.
     *
     * @return ID of the newly added document.
     */
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

    /**
     * Adds a new [Surgery] entry for a patient.
     *
     * @return ID of the newly added document.
     */
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

    /**
     * Adds a new [Immunization] entry for a patient.
     *
     * @return ID of the newly added document.
     */
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

    /**
     * Updates an existing [Medication] entry.
     *
     * @return True if successful, false otherwise.
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

    /**
     * Updates an existing [Allergy] entry.
     *
     * @return True if successful, false otherwise.
     */
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

    /**
     * Updates an existing [Condition] entry.
     *
     * @return True if successful, false otherwise.
     */
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

    /**
     * Updates an existing [Surgery] entry.
     *
     * @return True if successful, false otherwise.
     */
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

    /**
     * Updates an existing [Immunization] entry.
     *
     * @return True if successful, false otherwise.
     */
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

    /**
     * Retrieves all patients from the database.
     *
     * @return List of all [Patient]s, or an empty list if retrieval fails.
     */
    suspend fun getAllPatients(): List<Patient> {
        return try {
            firestore.collection("patients")
                .get()
                .await()
                .toObjects(Patient::class.java)
            } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch patients", e)
            emptyList()
        }
    }

    /**
     * Updates the patient profile.
     *
     * @param patient The [Patient] object with updated data.
     */
    suspend fun updatePatient(patient: Patient) {
        firestore.collection("patients")
            .document(patient.id)
            .set(patient)
            .await()
    }

    /**
     * Retrieves a list of [Medication] entries for a patient.
     *
     * @return List of medications or empty list if an error occurs.
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

    /**
     * Retrieves a list of [Allergy] entries for a patient.
     *
     * @return List of allergies or empty list if an error occurs.
     */
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

    /**
     * Retrieves a list of [Condition] entries for a patient.
     *
     * @return List of conditions or empty list if an error occurs.
     */
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

    /**
     * Retrieves a list of [Surgery] entries for a patient.
     *
     * @return List of surgeries or empty list if an error occurs.
     */
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

    /**
     * Retrieves a list of [Immunization] entries for a patient.
     *
     * @return List of immunizations or empty list if an error occurs.
     */
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

    /**
     * Deletes a [Medication] entry.
     *
     * @return True if deletion was successful, false otherwise.
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

    /**
     * Deletes an [Allergy] entry.
     *
     * @return True if deletion was successful, false otherwise.
     */
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

    /**
     * Deletes a [Condition] entry.
     *
     * @return True if deletion was successful, false otherwise.
     */
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

    /**
     * Deletes a [Surgery] entry.
     *
     * @return True if deletion was successful, false otherwise.
     */
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

    /**
     * Deletes an [Immunization] entry.
     *
     * @return True if deletion was successful, false otherwise.
     */
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
