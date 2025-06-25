package com.example.careconnect.screens.doctor.patients.medicalreports


import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.screens.patient.profile.medicalreport.MedicalReportUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for retrieving and managing medical reports for a patient.
 *
 * @property patientRepository Repository to fetch patient-related data.
 */
@HiltViewModel
class MedicalReportsViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _medicalReport = MutableStateFlow<List<MedicalReportUiModel>>(emptyList())
    val medicalReport: StateFlow<List<MedicalReportUiModel>> = _medicalReport

    /**
     * Fetches all medical reports for the given [patientId], maps them to UI models,
     * and updates the state.
     *
     * @param patientId The ID of the patient whose reports are being fetched.
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