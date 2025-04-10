package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.chat.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class AddChatRoomDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){
    suspend fun addChatRoom(chatRoom: ChatRoom): String {
        return firestore.collection(CHATROOMS_COLLECTION).add(chatRoom).await().id
    }

    suspend fun updateChatRoom(chatId: String, lastMessage: String, lastUpdated: Long) {
        firestore.collection(CHATROOMS_COLLECTION).document(chatId).update(
            "lastMessage", lastMessage,
            "lastUpdated", lastUpdated
        ).await()
    }

    suspend fun getChatRooms(): List<ChatRoom> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", userId)
            .get()
            .await()
            .toObjects(ChatRoom::class.java)
    }

    companion object {
        private const val CHATROOMS_COLLECTION = "chatrooms"

    }

}