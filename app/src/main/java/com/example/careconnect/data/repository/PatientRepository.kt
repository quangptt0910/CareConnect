package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.PatientRemoteDataSource
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.MedicalReport
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Prescription
import com.example.careconnect.dataclass.Surgery
import jakarta.inject.Inject

class PatientRepository @Inject constructor(
    private val patientRemoteDataSource: PatientRemoteDataSource
){
    suspend fun getPatientById(patientId: String): Patient? {
        return patientRemoteDataSource.getPatientById(patientId)
    }

    suspend fun createMedicalReport(patientId: String, medicalReport: MedicalReport) {
        return patientRemoteDataSource.createMedicalReport(patientId, medicalReport)
    }

    suspend fun createPrescription(patientId: String, prescription: Prescription) {
        return patientRemoteDataSource.createPrescription(patientId, prescription)
    }

    suspend fun addMedication(patientId: String, medication: Medication) {
        return patientRemoteDataSource.addMedication(patientId, medication)
    }

    suspend fun addAllergy(patientId: String, allergy: Allergy) {
        return patientRemoteDataSource.addAllergy(patientId, allergy)
    }

    suspend fun setMedicines(patientId: String): MutableList<Medication> {
        return patientRemoteDataSource.setMedicines(patientId)
    }

    suspend fun setAllergies(patientId: String): MutableList<Allergy> {
        return patientRemoteDataSource.setAllergies(patientId)
    }

    suspend fun addCondition(patientId: String, condition: Condition) {
        return patientRemoteDataSource.addConditions(patientId, condition)
    }

    suspend fun setConditions(patientId: String): MutableList<Condition> {
        return patientRemoteDataSource.setConditions(patientId)
    }

    suspend fun addSurgery(patientId: String, surgery: Surgery) {
        return patientRemoteDataSource.addSurgery(patientId, surgery)
    }

    suspend fun setSurgeries(patientId: String): MutableList<Surgery> {
        return patientRemoteDataSource.setSurgeries(patientId)
    }

    suspend fun addImmunization(patientId: String, immunization: Immunization) {
        return patientRemoteDataSource.addImmunization(patientId, immunization)
    }

    suspend fun setImmunizations(patientId: String): MutableList<Immunization> {
        return patientRemoteDataSource.setImmunizations(patientId)
    }
}