package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.chat.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

/**
 * Data source responsible for managing chat rooms in Firestore.
 *
 * Handles creation, updates, retrieval, and listening to chat rooms involving patients and doctors.
 *
 * @property auth FirebaseAuth instance for user authentication.
 * @property firestore FirebaseFirestore instance for Firestore operations.
 */
class AddChatRoomDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){
    /**
     * Adds a new chat room document to Firestore.
     *
     * @param chatRoom ChatRoom object to be added.
     * @return The generated ID of the newly created chat room.
     */
    suspend fun addChatRoom(chatRoom: ChatRoom): String {
        return firestore.collection(CHATROOMS_COLLECTION).add(chatRoom).await().id
    }

    /**
     * Updates the last message and last updated timestamp for a given chat room.
     *
     * @param chatId ID of the chat room to update.
     * @param lastMessage The last message text to set.
     * @param lastUpdated Timestamp in milliseconds for last update time.
     */
    suspend fun updateChatRoom(chatId: String, lastMessage: String, lastUpdated: Long) {
        firestore.collection(CHATROOMS_COLLECTION).document(chatId).update(
            "lastMessage", lastMessage,
            "lastUpdated", lastUpdated
        ).await()
    }

    /**
     * Sets up a listener to receive real-time updates on chat rooms involving the given user.
     *
     * @param userId User ID to filter chat rooms where the user is a participant.
     * @param onUpdate Callback invoked with the updated list of chat rooms.
     */
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

    /**
     * Loads chat rooms for the current authenticated user.
     *
     * @throws IllegalStateException If the user is not authenticated.
     */
    suspend fun loadChatRooms() {
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", currentUserId)
            .orderBy("lastUpdated")
            .get()
            .await()
            .toObjects(ChatRoom::class.java)

    }

    /**
     * Fetches chat rooms involving a specific doctor.
     *
     * @param doctorId ID of the doctor.
     * @return List of chat rooms involving the doctor.
     * @throws IllegalStateException If the user is not authenticated.
     */
    suspend fun getChatRoomsByDoctorId(doctorId: String): List<ChatRoom> {
        println("Fetching chat rooms for doctor ID: $doctorId")
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        return firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", doctorId)
            .get()
            .await()
            .toObjects(ChatRoom::class.java)
    }

    /**
     * Fetches chat rooms involving a specific patient.
     *
     * @param patientId ID of the patient.
     * @return List of chat rooms involving the patient.
     * @throws IllegalStateException If the user is not authenticated.
     */
    suspend fun getChatRoomsByPatientId(patientId: String): List<ChatRoom> {
        println("Fetching chat rooms for patient ID: $patientId")
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        return firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", patientId)
            .get()
            .await()
            .toObjects(ChatRoom::class.java)
    }

    /**
     * Fetches chat rooms involving both a doctor and a patient.
     *
     * @param doctorId ID of the doctor.
     * @param patientId ID of the patient.
     * @return List of chat rooms involving both doctor and patient.
     * @throws IllegalStateException If the user is not authenticated.
     */
    suspend fun getChatRooms(doctorId: String, patientId: String): List<ChatRoom> {
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        return firestore.collection(CHATROOMS_COLLECTION)
            .whereArrayContains("participants", currentUserId)
            .get()
            .await()
            .toObjects(ChatRoom::class.java)
    }

    /**
     * Retrieves an existing chat room ID between a patient and doctor or creates a new one.
     *
     * @param patient Patient object.
     * @param doctor Doctor object.
     * @return Chat room ID.
     * @throws Exception on Firestore errors or if user not authenticated.
     */
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

    /**
     * Fetches the currently authenticated patient from Firestore.
     *
     * @return Patient object or empty Patient if not authenticated.
     */
    suspend fun getCurrentPatient(): Patient {
        val userId = auth.currentUser?.uid ?: return Patient()
        return firestore.collection("patients").document(userId).get().await().toObject(Patient::class.java) ?: Patient()
    }

    /**
     * Fetches the currently authenticated doctor from Firestore.
     *
     * @return Doctor object or empty Doctor if not authenticated.
     */
    suspend fun getCurrentDoctor(): Doctor {
        val userId = auth.currentUser?.uid ?: return Doctor()
        return firestore.collection("doctors").document(userId).get().await().toObject(Doctor::class.java) ?: Doctor()
    }

    companion object {
        private const val CHATROOMS_COLLECTION = "chatrooms"

    }

}