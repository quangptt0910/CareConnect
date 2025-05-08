package com.example.careconnect.screens.patient.chat

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.example.careconnect.dataclass.chat.ChatRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatMenuViewModel @Inject constructor(
    private val addChatRoomRepository: AddChatRoomRepository,
    private val doctorRepository: DoctorRepository,
    private val patientRepository: PatientRepository
): MainViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms

    private val _currentUserId = MutableStateFlow<String>("")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    private val _currentPatient = MutableStateFlow<Patient?>(null)
    val currentPatient: StateFlow<Patient?> = _currentPatient

    private val _currentUserRole = MutableStateFlow<Role>(Role.PATIENT)
    val currentUserRole: StateFlow<Role> = _currentUserRole

    private val _chatPartners = MutableStateFlow<Map<String, Any>>(emptyMap())
    val chatPartners: StateFlow<Map<String, Any>> = _chatPartners

    fun loadChatRooms() {
        launchCatching {
            val userId = _currentUserId.value
            val userRole = _currentUserRole.value

            // Load chat rooms based on user role
            _chatRooms.value = when (userRole) {
                Role.PATIENT -> addChatRoomRepository.getChatRoomsByPatientId(userId)
                Role.DOCTOR -> addChatRoomRepository.getChatRoomsByDoctorId(userId)
                else -> emptyList()
            }

            // Load chat partners' details
            loadChatPartners()
        }
    }

    private suspend fun loadChatPartners() {
        val partnerMap = mutableMapOf<String, Any>()
        val userRole = _currentUserRole.value

        for (chatRoom in _chatRooms.value) {
            when (userRole) {
                Role.PATIENT -> {
                    // For patients, load doctor details
                    if (!partnerMap.containsKey(chatRoom.doctorId)) {
                        try {
                            val doctor = doctorRepository.getDoctorById(chatRoom.doctorId)
                            if (doctor != null) {
                                partnerMap[chatRoom.doctorId] = doctor
                            }
                        } catch (e: Exception) {
                            // Handle potential error fetching doctor
                        }
                    }
                }
                Role.DOCTOR -> {
                    // For doctors, load patient details
                    if (!partnerMap.containsKey(chatRoom.patientId)) {
                        try {
                            val patient = patientRepository.getPatientById(chatRoom.patientId)
                            if (patient != null) {
                                partnerMap[chatRoom.patientId] = patient
                            }
                        } catch (e: Exception) {
                            // Handle potential error fetching patient
                        }
                    }
                }
                else -> { /* Handle other roles if needed */ }
            }
        }

        _chatPartners.value = partnerMap
    }

    fun setDoctorId(doctorId: String) {
        launchCatching {
            _doctor.value = doctorRepository.getDoctorById(doctorId)
        }
    }
}
