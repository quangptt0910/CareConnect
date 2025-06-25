package com.example.careconnect.screens.patient.profile

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PatientProfileViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository,
    private val addChatRoomRepository: AddChatRoomRepository
): MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    init {
        getPatient()
    }

    fun getPatient(){
        viewModelScope.launch {
            _patient.value = addChatRoomRepository.getCurrentPatient()
        }
    }

    fun updatePatient(updatePatient: Patient) {
        _patient.value = updatePatient
        viewModelScope.launch {
            patientRepository.updatePatient(updatePatient)
        }
    }
}