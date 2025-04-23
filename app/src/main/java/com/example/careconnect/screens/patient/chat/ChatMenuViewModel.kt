package com.example.careconnect.screens.patient.chat

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.chat.ChatRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatMenuViewModel @Inject constructor(
    private val addChatRoomRepository: AddChatRoomRepository,
    private val doctorRepository: DoctorRepository
): MainViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms

    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    private val _currentPatient = MutableStateFlow<Patient?>(null)
    val currentPatient: StateFlow<Patient?> = _currentPatient

    suspend fun getChatRooms(doctorId: String, patientId: String) {
        _chatRooms.value = addChatRoomRepository.getChatRooms(doctorId, patientId)
    }

    fun setDoctorId(doctorId: String) {
        launchCatching {
            _doctor.value = doctorRepository.getDoctorById(doctorId)
        }
    }
}
