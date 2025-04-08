package com.example.careconnect.screens.patient.chat

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.dataclass.chat.ChatRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatMenuViewModel @Inject constructor(
    private val addChatRoomRepository: AddChatRoomRepository
): MainViewModel() {

    fun addChatRoom(
        doctorId: String,
        patientId: String,
        onSuccess: (String) -> Unit = {}
    ) {
        launchCatching(
        ) {
            val chatRoom = ChatRoom(
                participants = listOf(doctorId, patientId),
                lastMessage = "",
                lastUpdated = System.currentTimeMillis()
            )
            val chatId = addChatRoomRepository.addChatRoom(chatRoom)
            onSuccess(chatId)
        }
    }
}
