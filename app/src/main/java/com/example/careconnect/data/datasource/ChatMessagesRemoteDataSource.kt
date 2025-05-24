package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
        val chatSnapshot = currentCollection(chatId)
            .orderBy("timestamp")
            .get()
            .await()

        return chatSnapshot.toObjects(Message::class.java)
    }

    suspend fun getChatRoomById(chatId: String): ChatRoom? {
        val snapshot = firestore.collection("chatrooms").document(chatId).get().await()
        return snapshot.toObject(ChatRoom::class.java)
    }

    fun listenToMessages(chatId: String, onMessagesChanged: (List<Message>) -> Unit): ListenerRegistration {
        return currentCollection(chatId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    println("Error listening to messages: $error")
                    return@addSnapshotListener
                }

                val messages = snapshot.toObjects(Message::class.java)
                onMessagesChanged(messages)
            }
    }



    private fun currentCollection(chatId: String): CollectionReference =
        firestore.collection("chatrooms").document(chatId).collection("messages")

}