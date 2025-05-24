package com.example.careconnect.screens.patient.chat

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.datasource.AddChatRoomDataSource
import com.example.careconnect.data.datasource.ChatMessagesRemoteDataSource
import com.example.careconnect.data.repository.ChatMessagesRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.chat.Author
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatMessagesRepository: ChatMessagesRepository,
    private val doctorRepository: DoctorRepository,
    private val patientRepository: PatientRepository
): MainViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    var messages: StateFlow<List<Message>> = _messages

    private val _currentUser = mutableStateOf<Author?>(null)
    private val currentUser: MutableState<Author?> = _currentUser

    private var messageListenerRegistration: ListenerRegistration? = null

    val scr = LazyListState()

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

    fun observeMessages(chatId: String) {
        messageListenerRegistration?.remove() // Clean up any existing listener

        messageListenerRegistration = chatRemoteDataSource.listenToMessages(chatId) { newMessages ->
            println("🔥 observeMessages received ${newMessages.size} messages")
            _messages.value = newMessages
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageListenerRegistration?.remove()
    }



    fun initializeCurrentUser(patient: Patient?, doctor: Doctor?, patientId: String) {
        Firebase.auth.currentUser?.let { user ->
            val name = if (user.uid == patientId) patient?.name else doctor?.name
            if (name != null) {
                setCurrentUser(user.uid, name)
            }
        }
    }

    var chatRoom by mutableStateOf<ChatRoom?>(null)
        private set

    fun setCurrentUser(id: String, name: String){
        _currentUser.value = Author(id = id, name = name)
    }

    suspend fun getDoctor(doctorId: String): Doctor? {
        return doctorRepository.getDoctorById(doctorId)
    }

    suspend fun getPatient(patientId: String): Patient? {
        return patientRepository.getPatientById(patientId)
    }

//    fun getMessages(chatId: String){
//        launchCatching {
//            messages = chatMessagesRepository.getMessages(chatId)
//        }
//
//    }
//
    fun loadChat(chatId: String) {
        println("ChatViewModel: Loading chat with ID: $chatId")
        launchCatching {
            chatRoom = chatMessagesRepository.getChatRoomById(chatId) // <-- This sets the chatRoom
            observeMessages(chatId)
            println("ChatViewModel: chatRoom=$chatRoom, messages=$messages")
        }
    }


    // Function to send a new message
    fun sendMessage(text: String, chatId: String) {
        val newMessage = Message(
            text = text,
            author = me,
            timestamp = System.currentTimeMillis()
        )
        _messages.value += newMessage

        viewModelScope.launch {
            chatRemoteDataSource.sendMessage(chatId, newMessage)
        }

        loadChat(chatId)
    }

    // Function to send an image
    fun sendImage(uri: Uri, message: Message, chatId: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "chat_images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        launchCatching {
            currentUser.let {
                // upload to storage
                imageRef.putFile(uri).await()
                val downloadUrl = imageRef.downloadUrl.await()


                val newMessage = message.copy(imageUrl = downloadUrl.toString())
                _messages.value += newMessage

                // Send message to Firebase
                chatRemoteDataSource.sendMessage(chatId, newMessage)
            }
        }
    }
}