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

@HiltViewModel
class PatientsManageViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients

    init {
        loadPatients()
    }

    private fun loadPatients() {
        viewModelScope.launch {
            _patients.value = patientRepository.getAllPatients()
        }
    }
}