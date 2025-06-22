package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.DoctorRemoteDataSource
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Task
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

    suspend fun getScheduleForDate(doctorId: String, date: LocalDate): List<TimeSlot> =
        doctorRemoteDataSource.getScheduleForDate(doctorId, date)

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

    suspend fun updateTimeSlotAvailability(doctorId: String, date: String, targetTimeSlot: TimeSlot, newAvailability: Boolean): Boolean =
        doctorRemoteDataSource.updateTimeSlotAvailability(doctorId, date, targetTimeSlot, newAvailability)

    suspend fun addTimeSlots(doctorId: String, date: LocalDate, newTimeSlots: List<TimeSlot>): Boolean =
        doctorRemoteDataSource.addTimeSlots(doctorId, date, newTimeSlots)

    suspend fun saveSlot(doctorId: String, date: LocalDate, slot: TimeSlot) {
        doctorRemoteDataSource.saveSlot(doctorId, date, slot)
    }

    suspend fun deleteSlot(doctorId: String, date: LocalDate, slot: TimeSlot) {
        doctorRemoteDataSource.deleteSlot(doctorId, date, slot)
    }

    suspend fun deleteSlotInRange(doctorId: String, date: LocalDate, startTime: String, endTime: String) {
        doctorRemoteDataSource.deleteSlotInRange(doctorId, date, startTime, endTime)
    }

    suspend fun getAvailableSlots(doctorId: String, date: LocalDate): List<TimeSlot> {
        return doctorRemoteDataSource.getAvailableSlots(doctorId, date)
    }

    suspend fun getAvailableSlotsFlow(doctorId: String, date: LocalDate): Flow<List<TimeSlot>> {
        return doctorRemoteDataSource.getAvailableSlotsFlow(doctorId, date)
    }

    fun clearCache(doctorId: String? = null) {
        doctorRemoteDataSource.clearCache(doctorId)

    }

    fun getTasksFlow(doctorIdFlow: Flow<String?>): Flow<List<Task>> {
        return doctorRemoteDataSource.getTasksFlow(doctorIdFlow)
    }

    suspend fun addTask(doctorId: String,task: Task): String {
        return doctorRemoteDataSource.addTask(doctorId,task)
    }

    fun deleteTask(doctorId: String,task: Task) {
        doctorRemoteDataSource.deleteTask(doctorId,task)
    }

    fun updateTask(doctorId: String,task: Task) {
        doctorRemoteDataSource.updateTask(doctorId,task)
    }

    suspend fun getTasks(doctorId: String): List<Task> {
        return doctorRemoteDataSource.getTasks(doctorId)
    }


}