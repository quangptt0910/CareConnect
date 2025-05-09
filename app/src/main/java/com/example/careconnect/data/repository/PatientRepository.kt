package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.PatientRemoteDataSource
import com.example.careconnect.dataclass.MedicalReport
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Prescription
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
}