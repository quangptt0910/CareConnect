package com.example.careconnect.screens.patient.doctorsoverview

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


/**
 * ViewModel managing the state and business logic of the doctor profile screen,
 * including loading doctor details, managing chat rooms, and patient associations.
 *
 * @property doctorRepository Repository to access doctor data.
 * @property authRepository Repository to manage authentication state.
 * @property addChatRoomRepository Repository to handle chat room creation and retrieval.
 */
@HiltViewModel
class DoctorsProfileViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val authRepository: AuthRepository,
    private val addChatRoomRepository: AddChatRoomRepository
): MainViewModel() {
    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    private val _patientId = MutableStateFlow<String?>(null)
    val patientId: StateFlow<String?> = _patientId

    /**
     * Sets the patient ID from the current authenticated user.
     *
     * @param patientId The patient ID to set (ignored; replaced with current user ID).
     */
    fun setPatientId(patientId: String) {
        launchCatching {
            _patientId.value = authRepository.currentUser?.uid
        }
    }

    /**
     * Adds the current patient to the doctor's patient list.
     *
     * @param doctorId The ID of the doctor to add the patient to.
     */
    fun addPatient(doctorId: String) {
        launchCatching {
            val patientId = authRepository.currentUser?.uid
                ?: throw IllegalStateException("No logged-in user")
            println("DEBUG: addPatient: patientId=$patientId, doctorId=$doctorId")
            doctorRepository.addPatient(doctorId, patientId)
        }
    }

    /**
     * Loads the doctor details for the given doctor ID.
     *
     * @param doctorId The ID of the doctor to load.
     */
    fun setDoctorId(doctorId: String) {
        launchCatching {
            _doctor.value = doctorRepository.getDoctorById(doctorId)
        }
    }

    /**
     * Retrieves or creates a chat room ID for the given patient and doctor.
     *
     * @param patient The current patient.
     * @param doctor The current doctor.
     * @return The chat room ID.
     */
    suspend fun getOrCreateChatRoomId(patient: Patient, doctor: Doctor): String {
        return addChatRoomRepository.getOrCreateChatRoomId(patient, doctor)
    }

    /**
     * Retrieves the current authenticated patient.
     *
     * @return The current patient.
     */
    suspend fun getCurrentPatient(): Patient {
        return addChatRoomRepository.getCurrentPatient()
    }

}