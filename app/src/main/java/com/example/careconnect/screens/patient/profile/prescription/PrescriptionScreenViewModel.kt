package com.example.careconnect.screens.patient.profile.prescription

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PrescriptionScreenViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _prescriptions = MutableStateFlow<List<PrescriptionUiModel>>(emptyList())
    val prescriptions: StateFlow<List<PrescriptionUiModel>> = _prescriptions

    private val _patientId = MutableStateFlow("")
    val patientId: StateFlow<String> = _patientId

    init {
        viewModelScope.launch {
            _patientId.value = patientRepository.getPatientId()
        }
    }

    fun fetchPrescriptions(patientId: String) {
        viewModelScope.launch {
            val list = patientRepository.getPrescriptions(patientId)
                .map {
                    PrescriptionUiModel(
                        id = it.id ?: "",
                        pdfUrl = it.prescriptionPdfUrl ?: "",
                        medicationName = it.medicationName ?: "Unnamed Prescription",
                        issueDate = (it.issueDate ?: "").toString(),
                    )
                }
            _prescriptions.value = list
        }
    }
}