package com.example.careconnect.dataclass.chat

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId

data class ChatRoom(
    @DocumentId val chatId: String = "",
    val doctorId: String = "",
    val patientId: String = "",
    val participants: List<String> = listOf(),
    val lastMessage: String="",
    val messages: List<Message> = listOf(),
    val lastUpdated: Long = 0L
)

data class Message(
    @DocumentId val id: String = "",
    val text: String = "",
    val author: Author = Author(),
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: Uri? = null
) {
    // Empty constructor for Firebase
    constructor() : this("", "", Author(), System.currentTimeMillis(), null)

    val isFromMe: Boolean
        get() = author.id == Firebase.auth.currentUser?.uid
}

