package com.example.careconnect.screens.doctor.patients.prescriptions

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Prescription
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


/**
 * ViewModel responsible for handling prescription creation logic for doctors.
 *
 * Handles:
 * - Loading a specific patient's data.
 * - Creating and uploading prescription PDFs.
 * - Uploading to Firebase Storage and storing metadata in Firestore.
 *
 * @property patientRepository Repository for accessing patient data.
 * @property authRepository Repository for accessing authentication/user ID info.
 * @property doctorRepository Repository for accessing doctor details.
 */
@HiltViewModel
class CreatePrescriptionsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository,
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _prescriptionCreated = MutableStateFlow(false)
    val prescriptionCreated: StateFlow<Boolean> = _prescriptionCreated

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
     * Loads the patient details for the given [patientId].
     *
     * @param patientId The ID of the patient whose data is to be fetched.
     */
    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patientData = patientRepository.getPatientById(patientId)
            _patient.value = patientData
        }
    }

    /**
     * Creates a prescription for the patient, generates a PDF, and uploads it to Firebase.
     *
     * @param patientId ID of the patient.
     * @param prescription The prescription data to be created.
     * @param context Android context for file and PDF handling.
     */
    fun createPrescription(patientId: String, prescription: Prescription, context: Context) {
        viewModelScope.launch {
            _prescriptionCreated.value = false
            val doctorId = _currentDoctorId.value
            val patient = _patient.value ?: return@launch
            val doctor = doctorRepository.getDoctorById(doctorId ?: return@launch)

            val completedPrescription = prescription.copy(doctorId = doctorId)

            val pdfFile = doctor?.let {
                CreatePrescriptionPdf(context, patient,
                    it, completedPrescription)
            }
            if (pdfFile != null) {
                Log.d("PDF", "PDF generated at: ${pdfFile.absolutePath}")
            }

            try {
                // Step 1: Upload to Firebase Storage
                val storageRef = FirebaseStorage.getInstance().reference
                val reportRef = storageRef.child("patient_documents/$patientId/prescriptions/${pdfFile?.name}")
                val uri = Uri.fromFile(pdfFile)

                val uploadTask = reportRef.putFile(uri)
                val result = uploadTask.await()

                // Step 2: Get download URL
                val downloadUrl = reportRef.downloadUrl.await().toString()
                Log.d("FirebaseStorage", "File uploaded. URL: $downloadUrl")

                // Step 3: Save report with PDF URL to Firestore
                val finalReport = completedPrescription.copy(prescriptionPdfUrl = downloadUrl)
                patientRepository.createPrescription(patientId, finalReport)
                _prescriptionCreated.value = true

            } catch (e: Exception) {
                Log.e("Prescription", "Error uploading PDF", e)
            }

        }
    }
}