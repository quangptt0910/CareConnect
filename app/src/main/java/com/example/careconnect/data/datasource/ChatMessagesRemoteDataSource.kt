package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await


/**
 * Remote data source responsible for handling chat-related operations
 * with Firebase Firestore, such as sending and receiving messages.
 *
 * @property auth Firebase authentication instance used to get the current user.
 * @property firestore Firestore database instance.
 * @property addChatRoomDataSource Helper class used to update chat room metadata.
 */
class ChatMessagesRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val addChatRoomDataSource: AddChatRoomDataSource
) {
    /**
     * Sends a message to a specified chat room in Firestore and updates the chat room metadata.
     *
     * @param chatId The unique identifier of the chat room.
     * @param message The message object to send.
     */
    suspend fun sendMessage(chatId: String, message: Message) {
        currentCollection(chatId).add(message).await()
        addChatRoomDataSource.updateChatRoom(chatId, message.text, message.timestamp)
    }

    /**
     * Retrieves all messages for a given chat room, ordered by timestamp.
     *
     * @param chatId The unique identifier of the chat room.
     * @return A list of [Message] objects for the specified chat room.
     */
    suspend fun getMessages(chatId: String): List<Message> {
        val chatSnapshot = currentCollection(chatId)
            .orderBy("timestamp")
            .get()
            .await()

        return chatSnapshot.toObjects(Message::class.java)
    }

    /**
     * Retrieves chat room metadata for a given chat ID.
     *
     * @param chatId The unique identifier of the chat room.
     * @return A [ChatRoom] object or `null` if the room doesn't exist.
     */
    suspend fun getChatRoomById(chatId: String): ChatRoom? {
        val snapshot = firestore.collection("chatrooms").document(chatId).get().await()
        return snapshot.toObject(ChatRoom::class.java)
    }

    /**
     * Listens for real-time message updates in a specific chat room.
     *
     * @param chatId The unique identifier of the chat room.
     * @param onMessagesChanged Callback function invoked with the updated list of messages.
     * @return A [ListenerRegistration] that can be used to stop listening.
     */
    fun listenToMessages(chatId: String, onMessagesChanged: (List<Message>) -> Unit): ListenerRegistration {
        println("Setting up Firestore listener for chatId: $chatId")

        return currentCollection(chatId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    println("Error listening to messages: $error")
                    return@addSnapshotListener
                }

                val messages = snapshot.toObjects(Message::class.java)
                println("📦 Parsed ${messages.size} messages")
                onMessagesChanged(messages)
            }
    }


    /**
     * Returns a Firestore [CollectionReference] for the messages subcollection within the chat room.
     *
     * @param chatId The unique identifier of the chat room.
     * @return A reference to the messages subcollection.
     */
    private fun currentCollection(chatId: String): CollectionReference =
        firestore.collection("chatrooms").document(chatId).collection("messages")

}