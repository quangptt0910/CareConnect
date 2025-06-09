package com.example.careconnect.screens.doctor.patients.medicalreports


import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.screens.patient.profile.MedicalReportUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MedicalReportsViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _medicalReport = MutableStateFlow<List<MedicalReportUiModel>>(emptyList())
    val medicalReport: StateFlow<List<MedicalReportUiModel>> = _medicalReport

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