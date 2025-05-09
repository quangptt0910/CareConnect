package com.example.careconnect.screens.doctor.patients.prescriptions

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Prescription
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@HiltViewModel
class CreatePrescriptionsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _currentDoctorId = MutableStateFlow<String?>(null)
    val currentDoctorId: StateFlow<String?> = _currentDoctorId

    init {
        // Get the doctor ID when ViewModel is created
        viewModelScope.launch {
            try {
                _currentDoctorId.value = authRepository.currentUserIdFlow.firstOrNull()
                Log.d("MedicalReportViewModel", "Initialized doctor ID: ${_currentDoctorId.value}")
            } catch (e: Exception) {
                Log.e("MedicalReportViewModel", "Failed to get current user ID", e)
            }
        }
    }

    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patientData = patientRepository.getPatientById(patientId)
            _patient.value = patientData
        }
    }

    fun createPrescription(patientId: String, prescription: Prescription) {
        viewModelScope.launch {
            val doctorId = _currentDoctorId.value
            val prescriptionWithDoctor = doctorId?.let {
                prescription.copy(doctorId = it)
            }

            Log.d("CreatePrescriptionVM", "Doctor ID: $doctorId")
            Log.d("CreatePrescriptionVM", "Prescription: $prescriptionWithDoctor")

            if (prescriptionWithDoctor != null) {
                patientRepository.createPrescription(patientId, prescriptionWithDoctor)
            }
        }
    }
}