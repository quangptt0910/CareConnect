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

    fun listenToChatRooms(userId: String, onUpdate: (List<ChatRoom>) -> Unit) {
        firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", userId)
            .orderBy("lastUpdated")  // Optional, for sorted updates
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e("Firestore", "Chat room listener error", error)
                    return@addSnapshotListener
                }

                val chatRooms = snapshot.toObjects(ChatRoom::class.java)
                onUpdate(chatRooms)
            }
    }

    suspend fun loadChatRooms() {
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", currentUserId)
            .orderBy("lastUpdated")
            .get()
            .await()
            .toObjects(ChatRoom::class.java)

    }

    suspend fun getChatRoomsByDoctorId(doctorId: String): List<ChatRoom> {
        println("Fetching chat rooms for doctor ID: $doctorId")
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        return firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", doctorId)
            .get()
            .await()
            .toObjects(ChatRoom::class.java)
    }

    suspend fun getChatRoomsByPatientId(patientId: String): List<ChatRoom> {
        println("Fetching chat rooms for patient ID: $patientId")
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        return firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", patientId)
            .get()
            .await()
            .toObjects(ChatRoom::class.java)
    }

    suspend fun getChatRooms(doctorId: String, patientId: String): List<ChatRoom> {
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        return firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", currentUserId)
            .get()
            .await()
            .toObjects(ChatRoom::class.java)
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
                    participants = listOf(patient.id, doctor.id),
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

    suspend fun getCurrentDoctor(): Doctor {
        val userId = auth.currentUser?.uid ?: return Doctor()
        return firestore.collection("doctors").document(userId).get().await().toObject(Doctor::class.java) ?: Doctor()
    }

    companion object {
        private const val CHATROOMS_COLLECTION = "chatrooms"

    }

}