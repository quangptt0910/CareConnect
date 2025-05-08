package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.PatientRemoteDataSource
import com.example.careconnect.dataclass.Patient
import jakarta.inject.Inject

class PatientRepository @Inject constructor(
    private val patientRemoteDataSource: PatientRemoteDataSource
){
    suspend fun getPatientById(patientId: String): Patient? {
        return patientRemoteDataSource.getPatientById(patientId)
    }
}