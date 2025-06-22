package com.example.careconnect.screens.doctor.patients

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PatientsProfileViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val addChatRoomRepository: AddChatRoomRepository,
    private val doctorRepository: DoctorRepository,
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _doctorId = MutableStateFlow<String?>(null)
    val doctorId: StateFlow<String?> = _doctorId

    fun loadDoctorId() : String? {
        viewModelScope.launch {
            val doctorId = authRepository.getCurrentUserId()
            _doctorId.value = doctorId
            println("Loaded doctorId: $doctorId")

        }
        return _doctorId.value
    }


    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patientData = patientRepository.getPatientById(patientId)
            println("Loaded patient: $patientData")
            _patient.value = patientData
        }
    }

    suspend fun getOrCreateChatRoomId(patient: Patient, doctor: Doctor): String {
        return addChatRoomRepository.getOrCreateChatRoomId(patient, doctor)
    }

    suspend fun getCurrentDoctor(): Doctor {
        return addChatRoomRepository.getCurrentDoctor()
    }
}