package com.example.careconnect.screens.patient.chat

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.AuthRepository
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
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository
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

    suspend fun setCurrentUser() {
        _currentUserId.value = authRepository.getCurrentUserId().toString()
        _currentUserRole.value = authRepository.getCurrentUserRole()

        if (_currentUserRole.value == Role.PATIENT) {
            _currentPatient.value = patientRepository.getPatientById(_currentUserId.value)
        } else if (_currentUserRole.value == Role.DOCTOR) {
            _doctor.value = doctorRepository.getDoctorById(_currentUserId.value)
        }
    }

    suspend fun getCurrentUserRole(): Role {
        setCurrentUser()
        return _currentUserRole.value
    }


    fun loadChatRooms() {
        launchCatching {
            val userId = authRepository.getCurrentUserId()
            val userRole = authRepository.getCurrentUserRole()

            println("Loading chat rooms for user ID: $userId and role: $userRole")

            // Load chat rooms based on user role
            _chatRooms.value = when (userRole) {
                Role.PATIENT -> userId?.let { addChatRoomRepository.getChatRoomsByPatientId(it) }!!
                Role.DOCTOR -> userId?.let { addChatRoomRepository.getChatRoomsByDoctorId(it) }!!
                else -> emptyList()
            }
            println("Loaded chat rooms: ${_chatRooms.value.size}")

            // Load chat partners' details
            loadChatPartners()
        }
    }

    private suspend fun loadChatPartners() {
        val partnerMap = mutableMapOf<String, Any>()
        val userRole = _currentUserRole.value

        for (chatRoom in _chatRooms.value) {
            println("Fetching partner for chatRoom: ${chatRoom.chatId}")
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

                else -> { return }
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
