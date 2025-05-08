package com.example.careconnect.screens.patient.chat

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.datasource.AddChatRoomDataSource
import com.example.careconnect.data.datasource.ChatMessagesRemoteDataSource
import com.example.careconnect.data.repository.ChatMessagesRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
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
    private val chatMessagesRepository: ChatMessagesRepository,
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    private val _messages = mutableStateListOf<Message>()  // Backing field
//    val messages: List<Message> get() = _messages  // Publicly exposed list
    private val _currentUser = mutableStateOf<Author?>(null)
    private val currentUser: MutableState<Author?> = _currentUser

    val me : Author
        get() = currentUser.value ?: Author()

    private val chatRemoteDataSource = ChatMessagesRemoteDataSource(
        auth = Firebase.auth,
        firestore = Firebase.firestore,
        addChatRoomDataSource = AddChatRoomDataSource(
            auth = Firebase.auth,
            firestore = Firebase.firestore
        )
    )

    // Simulated user IDs (in a real app, get from authentication)

    //private val otherUser = Author(id = "user_1", name = "Alice")
    //private val me = Author(id = MY_ID, name = "Me")

    var messages by mutableStateOf<List<Message>>(emptyList())
        private set

    var chatRoom by mutableStateOf<ChatRoom?>(null)
        private set

    fun setCurrentUser(id: String, name: String){
        _currentUser.value = Author(id = id, name = name)
    }

    suspend fun getDoctor(doctorId: String): Doctor? {
        return doctorRepository.getDoctorById(doctorId)
    }

    suspend fun getPatient(patientId: String): Patient? {
        return doctorRepository.getPatientById(patientId)
    }

    fun getMessages(chatId: String){
        launchCatching {
            messages = chatMessagesRepository.getMessages(chatId)
        }

    }

    fun loadChat(chatId: String) {
        println("ChatViewModel: Loading chat with ID: $chatId")
        launchCatching {
            chatRoom = chatMessagesRepository.getChatRoomById(chatId) // <-- This sets the chatRoom
            messages = chatMessagesRepository.getMessages(chatId)
            println("ChatViewModel: chatRoom=$chatRoom, messages=$messages")
        }
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
            launchCatching {
                chatRemoteDataSource.sendMessage(chatId, newMessage)
            }
        }
    }



}