package com.example.careconnect.dataclass.chat

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId

/**
 * Represents a chat room between a doctor and a patient.
 *
 * @property chatId Unique identifier for the chat room (Firestore document ID).
 * @property doctorId Firebase UID of the doctor.
 * @property patientId Firebase UID of the patient.
 * @property participants List of participant UIDs (typically doctor and patient).
 * @property lastMessage Last message text sent in the chat.
 * @property lastUpdated Timestamp of the last update/message.
 */
data class ChatRoom(
    @DocumentId val chatId: String = "",
    val doctorId: String = "",
    val patientId: String = "",
    val participants: List<String> = listOf(),
    val lastMessage: String="",
    val lastUpdated: Long = 0L
)

/**
 * Represents a single message sent within a chat room.
 *
 * @property id Unique identifier for the message (Firestore document ID).
 * @property text Text content of the message.
 * @property author The author of the message.
 * @property timestamp Time at which the message was sent (in milliseconds).
 * @property imageUrl Optional image URL attached to the message.
 * @property documentUrl Optional document URL attached to the message.
 * @property documentName Optional name of the document if attached.
 * @property metadata Optional map containing additional metadata.
 */
data class Message(
    @DocumentId val id: String = "",
    val text: String = "",
    val author: Author = Author(),
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String? = null,
    val documentUrl: String? = null,
    val documentName: String? = null,
    val metadata: Map<String, String>? = null
) {
    // Empty constructor for Firebase
    constructor() : this("", "", Author(), System.currentTimeMillis(), null)

    val imageUri: Uri?
        get() = imageUrl?.toUri()

    val documentUri: Uri?
        get() = documentUrl?.toUri()

    val isFromMe: Boolean
        get() = author.id == Firebase.auth.currentUser?.uid
}

