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

    suspend fun updateChatRoom(chatRoom: ChatRoom) {
        addChatRoomRemoteDataSource.updateChatRoom(chatRoom)
    }

    suspend fun getChatRooms(): List<ChatRoom> {
        return addChatRoomRemoteDataSource.getChatRooms()
    }
}