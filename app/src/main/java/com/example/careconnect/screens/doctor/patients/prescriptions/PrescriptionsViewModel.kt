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

/**
 * ViewModel responsible for retrieving and managing a patient's list of prescriptions.
 *
 * Interacts with the [PatientRepository] to fetch data and expose it as [PrescriptionUiModel]s.
 *
 * @property patientRepository Repository for accessing patient-related data.
 */
@HiltViewModel
class PrescriptionsViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _prescriptions = MutableStateFlow<List<PrescriptionUiModel>>(emptyList())
    val prescriptions: StateFlow<List<PrescriptionUiModel>> = _prescriptions

    /**
     * Fetches all prescriptions for the given patient and maps them to [PrescriptionUiModel].
     *
     * @param patientId ID of the patient whose prescriptions are being fetched.
     */
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