package com.example.careconnect.screens.admin.patientsmanage

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing patients data in the admin patients management screen.
 *
 * Fetches the list of patients from [PatientRepository] and exposes it as a [StateFlow].
 *
 * @property patientRepository Repository used to retrieve patient data.
 */
@HiltViewModel
class PatientsManageViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients

    init {
        loadPatients()
    }

    /**
     * Loads all patients from the repository asynchronously.
     */
    private fun loadPatients() {
        viewModelScope.launch {
            _patients.value = patientRepository.getAllPatients()
        }
    }

    suspend fun updatePatient(patient: Patient) {
        patientRepository.updatePatient(patient)
        loadPatients()
    }

    suspend fun deletePatient(patient: Patient) {
        patientRepository.deletePatient(patient)
        loadPatients()
    }
}