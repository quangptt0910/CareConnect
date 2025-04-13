package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.DoctorRemoteDataSource
import com.example.careconnect.dataclass.Doctor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class DoctorRepository @Inject constructor(
    private val addDoctorRemoteDataSource: DoctorRemoteDataSource
) {
    suspend fun createDoctor(email: String, password: String,  doctorData: Map<String, Any>){
        return addDoctorRemoteDataSource.createDoctor(email, password,  doctorData)
    }
    suspend fun updateDoctor(doctor: Doctor) {
        addDoctorRemoteDataSource.updateDoctor(doctor)
    }

    suspend fun getAllDoctors(): List<Doctor> {
        return addDoctorRemoteDataSource.getAllDoctors()
    }

    fun getAllDoctorsFlow(): Flow<List<Doctor>> {
        return addDoctorRemoteDataSource.getAllDoctorsFlow()
    }

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return addDoctorRemoteDataSource.getDoctorById(doctorId)
    }

}