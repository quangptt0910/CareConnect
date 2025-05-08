package com.example.careconnect.screens.doctor.patients.medicalreports

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CreateMedicalReportViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
): MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patientData = patientRepository.getPatientById(patientId)
            _patient.value = patientData
        }
    }
}