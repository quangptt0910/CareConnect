package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.DoctorRemoteDataSource
import com.example.careconnect.dataclass.Doctor
import javax.inject.Inject


class DoctorRepository @Inject constructor(
    private val addDoctorRemoteDataSource: DoctorRemoteDataSource
) {
    suspend fun createDoctor(doctor: Doctor): String {
        return addDoctorRemoteDataSource.createDoctor(doctor)
    }
    suspend fun updateDoctor(doctor: Doctor) {
        addDoctorRemoteDataSource.updateDoctor(doctor)
    }

    suspend fun signupDoctor(email: String, password: String) {
        addDoctorRemoteDataSource.signupDoctor(email, password)
    }

}