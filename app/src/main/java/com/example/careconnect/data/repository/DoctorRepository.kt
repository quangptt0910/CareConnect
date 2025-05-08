package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.DoctorRemoteDataSource
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.TimeSlot
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject


class DoctorRepository @Inject constructor(
    private val doctorRemoteDataSource: DoctorRemoteDataSource
) {
    suspend fun createDoctor(email: String, password: String,  doctorData: Map<String, Any>): Pair<String, String> {
        return doctorRemoteDataSource.createDoctor(email, password,  doctorData)
    }
    suspend fun updateDoctor(doctor: Doctor) {
        doctorRemoteDataSource.updateDoctor(doctor)
    }

    suspend fun getAllDoctors(): List<Doctor> {
        return doctorRemoteDataSource.getAllDoctors()
    }

    fun getAllDoctorsFlow(): Flow<List<Doctor>> {
        return doctorRemoteDataSource.getAllDoctorsFlow()
    }

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return doctorRemoteDataSource.getDoctorById(doctorId)
    }

    suspend fun getPatientById(patientId: String): Patient? {
        return doctorRemoteDataSource.getPatientById(patientId)
    }

    suspend fun saveWorkingDays(doctorId: String, selectedDate: Set<LocalDate>) {
        doctorRemoteDataSource.saveWorkingDays(doctorId, selectedDate)
    }

    fun getWorkingDays(doctorId: String): Flow<Set<LocalDate>> {
        return doctorRemoteDataSource.getWorkingDays(doctorId)
    }

    fun getPatientsList(doctorId: Flow<String?>): Flow<List<Patient>> {
        return doctorRemoteDataSource.getPatientsList(doctorId)
    }

    fun addPatient(doctorId: String, patientId: String) {
        doctorRemoteDataSource.addPatient(doctorId, patientId)
    }

    suspend fun getScheduleForDate(doctorId: String, date: LocalDate): List<TimeSlot> {
        return doctorRemoteDataSource.getScheduleForDate(doctorId, date)
    }

    suspend fun saveSlot(doctorId: String, date: LocalDate, slot: TimeSlot) {
        doctorRemoteDataSource.saveSlot(doctorId, date, slot)
    }

    suspend fun deleteSlot(doctorId: String, date: LocalDate, slot: TimeSlot) {
        doctorRemoteDataSource.deleteSlot(doctorId, date, slot)
    }



}