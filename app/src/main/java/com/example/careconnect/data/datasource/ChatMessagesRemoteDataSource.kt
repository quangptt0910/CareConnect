package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class ChatMessagesRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun sendMessage(chatId: String, message: Message) {
        firestore.collection("chatrooms")
            .document(chatId)
            .update("messages", FieldValue.arrayUnion(message))
            .await()
    }

    suspend fun getMessages(chatId: String): List<Message> {
        val chatSnapshot = firestore.collection("chatrooms")
            .document(chatId)
            .get()
            .await()

        return chatSnapshot.toObject(ChatRoom::class.java)?.messages ?: emptyList()
    }

}