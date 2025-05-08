package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.ChatMessagesRemoteDataSource
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
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

    suspend fun getChatRoomById(chatId: String): ChatRoom? {
        return chatMessagesRemoteDataSource.getChatRoomById(chatId)
    }
}