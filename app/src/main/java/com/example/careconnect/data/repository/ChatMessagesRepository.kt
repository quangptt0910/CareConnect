package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.ChatMessagesRemoteDataSource
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.firestore.ListenerRegistration
import jakarta.inject.Inject

class ChatMessagesRepository @Inject constructor(
    private val chatMessagesRemoteDataSource: ChatMessagesRemoteDataSource
) {
    suspend fun sendMessage(chatId: String, message: Message) {
        chatMessagesRemoteDataSource.sendMessage(chatId, message)
    }

    suspend fun getMessages(chatId: String): List<Message> {
        return chatMessagesRemoteDataSource.getMessages(chatId)
    }

    fun listenToMessages(chatId: String, onMessagesChanged: (List<Message>) -> Unit): ListenerRegistration {
        return chatMessagesRemoteDataSource.listenToMessages(chatId, onMessagesChanged)
    }

    suspend fun getChatRoomById(chatId: String): ChatRoom? {
        return chatMessagesRemoteDataSource.getChatRoomById(chatId)
    }
}