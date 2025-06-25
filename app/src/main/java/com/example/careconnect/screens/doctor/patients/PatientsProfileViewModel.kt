package com.example.careconnect.screens.doctor.patients

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel responsible for managing the state and business logic of the Patient Profile screen.
 *
 * This ViewModel interacts with repositories to load patient and doctor data, manages chat room creation,
 * and provides state flows for UI to observe patient and doctor ID changes.
 *
 * @property patientRepository Repository to fetch patient data.
 * @property addChatRoomRepository Repository to handle chat room creation and retrieval.
 * @property doctorRepository Repository to fetch doctor data.
 * @property authRepository Repository to access authentication data such as current user ID.
 */
@HiltViewModel
class PatientsProfileViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val addChatRoomRepository: AddChatRoomRepository,
    private val doctorRepository: DoctorRepository,
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _doctorId = MutableStateFlow<String?>(null)
    val doctorId: StateFlow<String?> = _doctorId

    /**
     * Loads the current logged-in doctor's ID asynchronously and updates [doctorId] StateFlow.
     *
     * @return The current value of [doctorId] StateFlow, which may be `null` if not loaded yet.
     */
    fun loadDoctorId() : String? {
        viewModelScope.launch {
            val doctorId = authRepository.getCurrentUserId()
            _doctorId.value = doctorId
            println("Loaded doctorId: $doctorId")

        }
        return _doctorId.value
    }

    /**
     * Loads the patient data by the given [patientId] asynchronously and updates [patient] StateFlow.
     *
     * @param patientId The unique identifier of the patient to load.
     */
    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patientData = patientRepository.getPatientById(patientId)
            println("Loaded patient: $patientData")
            _patient.value = patientData
        }
    }

    /**
     * Gets or creates a chat room ID for the chat between the specified [patient] and [doctor].
     *
     * @param patient The patient involved in the chat.
     * @param doctor The doctor involved in the chat.
     * @return The chat room ID as a [String].
     */
    suspend fun getOrCreateChatRoomId(patient: Patient, doctor: Doctor): String {
        return addChatRoomRepository.getOrCreateChatRoomId(patient, doctor)
    }

    /**
     * Retrieves the currently logged-in [Doctor] asynchronously.
     *
     * @return The current [Doctor] instance.
     */
    suspend fun getCurrentDoctor(): Doctor {
        return addChatRoomRepository.getCurrentDoctor()
    }
}