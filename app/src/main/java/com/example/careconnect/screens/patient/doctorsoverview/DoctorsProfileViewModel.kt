package com.example.careconnect.screens.patient.doctorsoverview

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DoctorsProfileViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val authRepository: AuthRepository,
    private val addChatRoomRepository: AddChatRoomRepository
): MainViewModel() {
    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    private val _patientId = MutableStateFlow<String?>(null)
    val patientId: StateFlow<String?> = _patientId

    fun setPatientId(patientId: String) {
        launchCatching {
            _patientId.value = authRepository.currentUser?.uid
        }
    }

    fun setDoctorId(doctorId: String) {
        launchCatching {
            _doctor.value = doctorRepository.getDoctorById(doctorId)
        }
    }

    suspend fun getOrCreateChatRoomId(patient: Patient, doctor: Doctor): String {
        return addChatRoomRepository.getOrCreateChatRoomId(patient, doctor)
    }

    suspend fun getCurrentPatient(): Patient {
        return addChatRoomRepository.getCurrentPatient()
    }

}