package com.example.careconnect.dataclass.chat

import android.net.Uri
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.screens.patient.chat.MY_ID
import com.google.firebase.firestore.DocumentId

data class ChatRoom(
    @DocumentId val chatId: String = "",
    val doctor: Doctor = Doctor(),
    val patient: Patient = Patient(),
    val participants: List<String> = listOf(),
    val lastMessage: String="",
    val lastUpdated: Long = 0L
)


data class Message(
    @DocumentId val id: String = "",
    val text: String,
    val author: Author,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: Uri? = null
) {
    val isFromMe: Boolean
        get() = author.id == MY_ID
}
