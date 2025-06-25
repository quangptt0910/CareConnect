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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


/**
 * ViewModel responsible for managing the chat menu screen state and data.
 *
 * This ViewModel handles loading and updating chat rooms for the current user (patient or doctor),
 * fetching chat partners' details, managing user roles, and search query state.
 *
 * @property addChatRoomRepository Repository to access chat room data.
 * @property doctorRepository Repository to access doctor data.
 * @property patientRepository Repository to access patient data.
 * @property authRepository Repository to handle authentication and user info.
 */
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

    private val _uiState = MutableStateFlow(ChatMenuUiState())
    val uiState = _uiState.asStateFlow()


    /**
     * Updates the current search query used to filter chat rooms.
     *
     * @param query The new search query string.
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    init {
        launchCatching {
            setCurrentUser()
        }
    }

    /**
     * Sets the current user ID and role, then fetches the corresponding user details
     * and starts listening to their chat rooms.
     */
    suspend fun setCurrentUser() {
        _currentUserId.value = authRepository.getCurrentUserId().toString()
        _currentUserRole.value = authRepository.getCurrentUserRole()

        if (_currentUserRole.value == Role.PATIENT) {
            _currentPatient.value = patientRepository.getPatientById(_currentUserId.value)
        } else if (_currentUserRole.value == Role.DOCTOR) {
            _doctor.value = doctorRepository.getDoctorById(_currentUserId.value)
        }

        startListeningToChatRooms()
    }

    /**
     * Begins listening for changes in chat rooms for the current user.
     * When chat rooms update, reloads the chat partners' details.
     */
    suspend fun startListeningToChatRooms() {
        val userId = _currentUserId.value
        if (userId.isEmpty()) return

        addChatRoomRepository.listenToChatRooms(userId) { chatRoomList ->
            _chatRooms.value = chatRoomList
            launchCatching {
                loadChatPartners()
            }
        }
    }

    /**
     * Retrieves the current user role, ensuring the current user data is set.
     *
     * @return The current user's [Role].
     */
    suspend fun getCurrentUserRole(): Role {
        setCurrentUser()
        return _currentUserRole.value
    }

    /**
     * Loads chat rooms from the repository depending on the user's role,
     * and subsequently loads chat partners for those rooms.
     */
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

    /**
     * Loads the details of chat partners (doctors or patients) for all chat rooms
     * currently loaded, based on the current user's role.
     */
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

    /**
     * Fetches and sets the doctor details for the specified doctor ID.
     *
     * @param doctorId The unique ID of the doctor to load.
     */
    fun setDoctorId(doctorId: String) {
        launchCatching {
            _doctor.value = doctorRepository.getDoctorById(doctorId)
        }
    }
}
