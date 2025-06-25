package com.example.careconnect.screens.patient.profile.medicalreport

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for loading and managing the list of medical reports for the current patient.
 *
 * Fetches the patient ID and loads medical reports from the [PatientRepository].
 *
 * @property patientRepository Repository interface to fetch patient-related data.
 */
@HiltViewModel
class MedicalReportViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _medicalReport = MutableStateFlow<List<MedicalReportUiModel>>(emptyList())
    val medicalReport: StateFlow<List<MedicalReportUiModel>> = _medicalReport

    private val _patientId = MutableStateFlow("")
    val patientId: StateFlow<String> = _patientId

    init {
        viewModelScope.launch {
            _patientId.value = patientRepository.getPatientId()
        }
    }

    /**
     * Fetches and updates the list of medical reports for the given patient ID.
     *
     * @param patientId The ID of the patient whose medical reports are to be fetched.
     */
    fun fetchMedicalReports(patientId: String) {
        viewModelScope.launch {
            val list = patientRepository.getMedicalReports(patientId)
                .map {
                    MedicalReportUiModel(
                        id = it.id ?: "",
                        pdfUrl = it.reportPdfUrl ?: "",
                        diagnosis = it.diagnosis ?: "Unnamed Medical Diagnosis",
                        reportDate = (it.reportDate ?: "").toString(),
                    )
                }
            _medicalReport.value = list
        }
    }
}