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


    suspend fun getPrescriptions(patientId: String): List<Prescription> {
        return patientRemoteDataSource.getPrescriptions(patientId)
    }

    suspend fun getMedicalReports(patientId: String): List<MedicalReport> {
        return patientRemoteDataSource.getMedicalReports(patientId)
    }

    suspend fun getAllPatients(): List<Patient> {
        return patientRemoteDataSource.getAllPatients()
    }

    suspend fun updatePatient(patient: Patient) {
        patientRemoteDataSource.updatePatient(patient)
    }

    suspend fun addMedication(patientId: String, medication: Medication): String = patientRemoteDataSource.addMedication(patientId, medication)
    suspend fun addAllergy(patientId: String, allergy: Allergy): String = patientRemoteDataSource.addAllergy(patientId, allergy)
    suspend fun addCondition(patientId: String, condition: Condition): String = patientRemoteDataSource.addCondition(patientId, condition)
    suspend fun addSurgery(patientId: String, surgery: Surgery): String = patientRemoteDataSource.addSurgery(patientId, surgery)
    suspend fun addImmunization(patientId: String, immunization: Immunization): String = patientRemoteDataSource.addImmunization(patientId, immunization)

    suspend fun updateMedication(patientId: String, medication: Medication): Boolean = patientRemoteDataSource.updateMedication(patientId, medication)
    suspend fun updateAllergy(patientId: String, allergy: Allergy): Boolean = patientRemoteDataSource.updateAllergy(patientId, allergy)
    suspend fun updateCondition(patientId: String, condition: Condition): Boolean = patientRemoteDataSource.updateCondition(patientId, condition)
    suspend fun updateSurgery(patientId: String, surgery: Surgery): Boolean = patientRemoteDataSource.updateSurgery(patientId, surgery)
    suspend fun updateImmunization(patientId: String, immunization: Immunization): Boolean = patientRemoteDataSource.updateImmunization(patientId, immunization)

    suspend fun getMedications(patientId: String): List<Medication> = patientRemoteDataSource.getMedications(patientId)
    suspend fun getAllergies(patientId: String): List<Allergy> = patientRemoteDataSource.getAllergies(patientId)
    suspend fun getConditions(patientId: String): List<Condition> = patientRemoteDataSource.getConditions(patientId)
    suspend fun getSurgeries(patientId: String): List<Surgery> = patientRemoteDataSource.getSurgeries(patientId)
    suspend fun getImmunizations(patientId: String): List<Immunization> = patientRemoteDataSource.getImmunizations(patientId)

    suspend fun deleteMedication(patientId: String, medication: Medication): Boolean = patientRemoteDataSource.deleteMedication(patientId, medication)
    suspend fun deleteAllergy(patientId: String, allergy: Allergy): Boolean = patientRemoteDataSource.deleteAllergy(patientId, allergy)
    suspend fun deleteCondition(patientId: String, condition: Condition): Boolean = patientRemoteDataSource.deleteCondition(patientId, condition)
    suspend fun deleteSurgery(patientId: String, surgery: Surgery): Boolean = patientRemoteDataSource.deleteSurgery(patientId, surgery)
    suspend fun deleteImmunization(patientId: String, immunization: Immunization): Boolean = patientRemoteDataSource.deleteImmunization(patientId, immunization)


}