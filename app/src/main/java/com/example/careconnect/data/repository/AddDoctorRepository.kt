package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.AddDoctorRemoteDataSource
import com.example.careconnect.dataclass.Doctor
import javax.inject.Inject


class AddDoctorRepository @Inject constructor(
    private val addDoctorRemoteDataSource: AddDoctorRemoteDataSource
) {
    suspend fun createDoctor(doctor: Doctor): String {
        return addDoctorRemoteDataSource.createDoctor(doctor)
    }
    suspend fun updateDoctor(doctor: Doctor) {
        addDoctorRemoteDataSource.updateDoctor(doctor)
    }


}