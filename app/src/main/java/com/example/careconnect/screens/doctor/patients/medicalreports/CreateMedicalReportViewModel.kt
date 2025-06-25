package com.example.careconnect.screens.doctor.patients.medicalreports

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.MedicalReport
import com.example.careconnect.dataclass.Patient
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel responsible for creating and uploading a medical report for a patient.
 * It handles generating a PDF, uploading it to Firebase Storage, and storing metadata in Firestore.
 *
 * @property patientRepository Repository for accessing patient data.
 * @property authRepository Repository for authentication and current user info.
 * @property doctorRepository Repository for accessing doctor data.
 */
@HiltViewModel
class CreateMedicalReportViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository,
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _medicalReportCreated = MutableStateFlow(false)
    val medicalReportCreated: StateFlow<Boolean> = _medicalReportCreated

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

    /**
     * Loads patient information by their [patientId] and sets it to [_patient].
     *
     * @param patientId ID of the patient to be fetched.
     */
    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patientData = patientRepository.getPatientById(patientId)
            _patient.value = patientData
        }
    }

    /**
     * Creates a new medical report and saves it for a patient.
     * Also generates a PDF, uploads it to Firebase Storage, and stores the URL.
     *
     * @param patientId The ID of the patient.
     * @param medicalReport Data to be included in the medical report.
     * @param context Android context used for file operations.
     */
    fun createMedicalReport(patientId: String, medicalReport: MedicalReport, context: Context) {
        viewModelScope.launch {
            _medicalReportCreated.value = false
            val doctorId = _currentDoctorId.value ?: return@launch
            val patient = _patient.value ?: return@launch
            val doctor = doctorRepository.getDoctorById(doctorId)

            val completeReport = medicalReport.copy(doctorId = doctorId)

            // Generate PDF
            val pdfFile = doctor?.let {
                CreateMedicalReportPdf(context, patient,
                    it, completeReport)
            }
            if (pdfFile != null) {
                Log.d("PDF", "PDF generated at: ${pdfFile.absolutePath}")
            }

            // Optionally: Upload the PDF file and get URL, then update reportPdfUrl

            try {
                // Step 1: Upload to Firebase Storage
                val storageRef = FirebaseStorage.getInstance().reference
                val reportRef = storageRef.child("patient_documents/$patientId/reports/${pdfFile?.name}")
                val uri = Uri.fromFile(pdfFile)

                val uploadTask = reportRef.putFile(uri)
                val result = uploadTask.await()

                // Step 2: Get download URL
                val downloadUrl = reportRef.downloadUrl.await().toString()
                Log.d("FirebaseStorage", "File uploaded. URL: $downloadUrl")

                // Step 3: Save report with PDF URL to Firestore
                val finalReport = completeReport.copy(reportPdfUrl = downloadUrl)
                patientRepository.createMedicalReport(patientId, finalReport)
                _medicalReportCreated.value = true

            } catch (e: Exception) {
                Log.e("MedicalReport", "Error uploading PDF", e)
            }
            // patientRepository.createMedicalReport(patientId, completeReport)
        }
    }

}