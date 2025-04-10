package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.AddChatRoomDataSource
import com.example.careconnect.dataclass.chat.ChatRoom
import jakarta.inject.Inject

class AddChatRoomRepository @Inject constructor(
    private val addChatRoomRemoteDataSource: AddChatRoomDataSource
)
{
    suspend fun addChatRoom(chatRoom: ChatRoom): String {
        return addChatRoomRemoteDataSource.addChatRoom(chatRoom)
    }

    suspend fun updateChatRoom(chatId: String, lastMessage: String, lastUpdated: Long) {
        addChatRoomRemoteDataSource.updateChatRoom(chatId, lastMessage, lastUpdated)
    }

    suspend fun getChatRooms(): List<ChatRoom> {
        return addChatRoomRemoteDataSource.getChatRooms()
    }
}