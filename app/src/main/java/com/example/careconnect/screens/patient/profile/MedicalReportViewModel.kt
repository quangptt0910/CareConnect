package com.example.careconnect.screens.patient.profile

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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