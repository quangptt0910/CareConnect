package com.example.careconnect.screens.patient.chat

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.example.careconnect.MainViewModel
import com.example.careconnect.dataclass.chat.Author
import com.example.careconnect.dataclass.chat.Message

class ChatViewModel: MainViewModel() {
    private val _messages = mutableStateListOf<Message>()  // Backing field
    val messages: List<Message> get() = _messages  // Publicly exposed list

    // Simulated user IDs (in a real app, get from authentication)
    private val myId = "user_me"
    private val otherUser = Author(id = "user_1", name = "Alice")
    private val me = Author(id = myId, name = "Me")

    init {
        // Add some dummy messages for testing
        _messages.addAll(
            listOf(
                Message("1","Hello!", otherUser, System.currentTimeMillis()),
                Message("1","Hey! How are you?", me, System.currentTimeMillis()),
                Message("1","I'm good, thanks! You?", otherUser, System.currentTimeMillis())
            )
        )
    }

    // Function to send a new message
    fun sendMessage(text: String) {
        if (text.isNotBlank()) {
            _messages.add(Message("1", text, me, System.currentTimeMillis()))
        }
    }

    // Function to send an image
    fun sendImage(uri: Uri) {
        _messages.add(Message("1", "", me, System.currentTimeMillis(), imageUri = uri))
    }

}