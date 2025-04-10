package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject

class ChatMessagesRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun sendMessage(chatId: String, message: Message) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.id)
            .set(message)
    }

}