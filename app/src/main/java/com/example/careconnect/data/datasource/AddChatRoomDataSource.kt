package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
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
        return try {
            Log.d("FirestoreDebug", "Fetching chat rooms for user $userId")
            val result = firestore.collection(CHATROOMS_COLLECTION)
                .whereArrayContains("participants", userId)
                .get()
                .await()
            Log.d("FirestoreDebug", "Chat rooms fetched: ${result.size()}")
            result.toObjects(ChatRoom::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error fetching chat rooms: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getOrCreateChatRoomId(patient: Patient, doctor: Doctor): String {

        try {
            val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

            Log.d("FirestoreDebug", "Checking for existing chat between ${patient.id} and ${doctor.id}")
            val existingChat = firestore.collection(CHATROOMS_COLLECTION)
                .whereArrayContains("participants", currentUserId)
                .whereEqualTo("patientId", patient.id)
                .whereEqualTo("doctorId", doctor.id)
                .get()
                .await()

            if (!existingChat.isEmpty) {
                Log.d("FirestoreDebug", "Existing chat found: ${existingChat.documents.first().id}")
                return existingChat.documents.first().id
            } else {
                Log.d("FirestoreDebug", "Creating new chat room")
                val newChatRoom = ChatRoom(
                    doctorId = doctor.id,
                    patientId = patient.id,
                    doctor = doctor,
                    patient = patient,
                    participants = listOf(patient.id, doctor.id),
                    messages = emptyList(),
                    lastMessage = "",
                    lastUpdated = System.currentTimeMillis()
                )
                val newChatRef = firestore.collection(CHATROOMS_COLLECTION).add(newChatRoom).await()
                Log.d("FirestoreDebug", "New chat room created: ${newChatRef.id}")
                return newChatRef.id
            }
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error in getOrCreateChatRoomId: ${e.message}", e)
            throw e // or return "" / some fallback
        }
    }

    suspend fun getCurrentPatient(): Patient {
        val userId = auth.currentUser?.uid ?: return Patient()
        return firestore.collection("patients").document(userId).get().await().toObject(Patient::class.java) ?: Patient()
    }

    companion object {
        private const val CHATROOMS_COLLECTION = "chatrooms"

    }

}