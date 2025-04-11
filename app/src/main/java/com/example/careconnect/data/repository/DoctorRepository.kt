package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.DoctorRemoteDataSource
import com.example.careconnect.dataclass.Doctor
import javax.inject.Inject


class DoctorRepository @Inject constructor(
    private val addDoctorRemoteDataSource: DoctorRemoteDataSource
) {
    suspend fun createDoctor(email: String, password: String, doctor: Doctor){
        return addDoctorRemoteDataSource.createDoctor(email, password, doctor)
    }
    suspend fun updateDoctor(doctor: Doctor) {
        addDoctorRemoteDataSource.updateDoctor(doctor)
    }

    suspend fun getDoctors(): List<Doctor> {
        return addDoctorRemoteDataSource.getDoctors()
    }

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return addDoctorRemoteDataSource.getDoctorById(doctorId)
    }

}