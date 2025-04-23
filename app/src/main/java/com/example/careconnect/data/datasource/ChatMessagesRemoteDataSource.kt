package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class ChatMessagesRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val addChatRoomDataSource: AddChatRoomDataSource
) {
    suspend fun sendMessage(chatId: String, message: Message) {
        currentCollection(chatId).add(message).await()

        addChatRoomDataSource.updateChatRoom(chatId, message.text, message.timestamp)
    }

    suspend fun getMessages(chatId: String): List<Message> {
        val chatSnapshot = firestore.collection("chatrooms")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .await()

        return chatSnapshot.toObjects(Message::class.java)
    }

    private fun currentCollection(chatId: String): CollectionReference =
        firestore.collection("chatrooms").document(chatId).collection("messages")

}