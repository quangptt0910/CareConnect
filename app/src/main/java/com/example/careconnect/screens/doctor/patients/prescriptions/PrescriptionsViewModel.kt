package com.example.careconnect.screens.doctor.patients.prescriptions

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.screens.patient.profile.prescription.PrescriptionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PrescriptionsViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _prescriptions = MutableStateFlow<List<PrescriptionUiModel>>(emptyList())
    val prescriptions: StateFlow<List<PrescriptionUiModel>> = _prescriptions

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