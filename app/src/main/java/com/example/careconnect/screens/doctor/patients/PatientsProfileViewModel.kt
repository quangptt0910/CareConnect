package com.example.careconnect.screens.doctor.patients

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PatientsProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
): MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patientData = authRepository.getPatientById(patientId)
            _patient.value = patientData
        }
    }
}