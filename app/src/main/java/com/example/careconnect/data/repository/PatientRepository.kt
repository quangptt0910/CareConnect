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

    suspend fun getPatientId(): String {
        return patientRemoteDataSource.getPatientId()
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

    suspend fun getPrescriptions(patientId: String): List<Prescription> {
        return patientRemoteDataSource.getPrescriptions(patientId)
    }

    suspend fun getMedicalReports(patientId: String): List<MedicalReport> {
        return patientRemoteDataSource.getMedicalReports(patientId)
    }

    suspend fun updateCondition(patientId: String, condition: Condition) {
        return patientRemoteDataSource.updateCondition(patientId, condition)
    }

    suspend fun updateImmunization(patientId: String, immunization: Immunization) {
        return patientRemoteDataSource.updateImmunization(patientId, immunization)
    }

    suspend fun updateMedication(patientId: String, medication: Medication) {
        return patientRemoteDataSource.updateMedication(patientId, medication)
    }

    suspend fun updateSurgery(patientId: String, surgery: Surgery) {
        return patientRemoteDataSource.updateSurgery(patientId, surgery)
    }

    suspend fun updateAllergy(patientId: String, allergy: Allergy) {
        return patientRemoteDataSource.updateAllergy(patientId, allergy)
    }

    suspend fun deleteMedication(patientId: String, medication: Medication) {
        return patientRemoteDataSource.deleteMedication(patientId, medication)
    }

    suspend fun deleteAllergy(patientId: String, allergy: Allergy) {
        return patientRemoteDataSource.deleteAllergy(patientId, allergy)
    }

    suspend fun deleteCondition(patientId: String, condition: Condition) {
        return patientRemoteDataSource.deleteCondition(patientId, condition)
    }

    suspend fun deleteSurgery(patientId: String, surgery: Surgery) {
        return patientRemoteDataSource.deleteSurgery(patientId, surgery)
    }

    suspend fun deleteImmunization(patientId: String, immunization: Immunization) {
        return patientRemoteDataSource.deleteImmunization(patientId, immunization)
    }

}