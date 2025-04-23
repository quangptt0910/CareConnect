package com.example.careconnect.screens.patient.chat

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.datasource.ChatMessagesRemoteDataSource
import com.example.careconnect.data.repository.ChatMessagesRepository
import com.example.careconnect.dataclass.chat.Author
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatMessagesRepository: ChatMessagesRepository
): MainViewModel() {
    private val _messages = mutableStateListOf<Message>()  // Backing field
//    val messages: List<Message> get() = _messages  // Publicly exposed list

    val me = Author(
        id = Firebase.auth.currentUser?.uid ?: "",
        name = Firebase.auth.currentUser?.displayName ?: "Me"
    )

    private val chatRemoteDataSource = ChatMessagesRemoteDataSource(
        auth = Firebase.auth,
        firestore = Firebase.firestore
    )

    // Simulated user IDs (in a real app, get from authentication)
    val currentUser = Firebase.auth.currentUser?.uid ?: ""
    //private val otherUser = Author(id = "user_1", name = "Alice")
    //private val me = Author(id = MY_ID, name = "Me")

    var messages by mutableStateOf<List<Message>>(emptyList())
        private set

    var chatRoom by mutableStateOf<ChatRoom?>(null)
        private set

    init {
        // Add some dummy messages for testing
//        _messages.addAll(
//            listOf(
//                Message("1","Hello!", otherUser, System.currentTimeMillis()),
//                Message("1","Hey! How are you?", me, System.currentTimeMillis()),
//                Message("1","I'm good, thanks! You?", otherUser, System.currentTimeMillis())
//            )
//        )
    }

    fun getMessages(chatId: String){
        viewModelScope.launch {
            messages = chatMessagesRepository.getMessages(chatId)
        }

    }

    fun loadChat(chatId: String) {
        getMessages(chatId)
    }

    // Function to send a new message
    fun sendMessage(message: Message, chatId: String) {
        val newMessage = message.copy(timestamp = System.currentTimeMillis())
        _messages.add(newMessage)

        viewModelScope.launch {
            chatRemoteDataSource.sendMessage(chatId, newMessage)
        }

        loadChat(chatId)
    }

    // Function to send an image
    fun sendImage(uri: Uri, message: Message, chatId: String) {
        currentUser.let {
            val newMessage = message.copy(imageUri = uri)
            _messages.add(newMessage)

            // Send message to Firebase
            viewModelScope.launch {
                chatRemoteDataSource.sendMessage(chatId, newMessage)
            }
        }
    }



}