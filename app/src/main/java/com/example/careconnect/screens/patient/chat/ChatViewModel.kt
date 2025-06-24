package com.example.careconnect.screens.patient.chat

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AddChatRoomRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.ChatMessagesRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.example.careconnect.dataclass.chat.Author
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.example.careconnect.notifications.NotificationManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notificationManager: NotificationManager,
    private val chatMessagesRepository: ChatMessagesRepository,
    private val doctorRepository: DoctorRepository,
    private val patientRepository: PatientRepository,
    private val addChatRoomRepository: AddChatRoomRepository,
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    var messages: StateFlow<List<Message>> = _messages

    private val _currentUser = mutableStateOf<Author?>(null)
    private val currentUser: MutableState<Author?> = _currentUser

    private var messageListenerRegistration: ListenerRegistration? = null

    private var _chatId = mutableStateOf("")
    private var _patientId = mutableStateOf("")
    private var _doctorId = mutableStateOf("")

    val chatId: String get() = _chatId.value
    val patientId: String get() = _patientId.value
    val doctorId: String get() = _doctorId.value

    var showReferralDialog by mutableStateOf(false)

    val me: Author
        get() = currentUser.value ?: Author()

    init {
        loadChat(chatId)
    }

    fun initialize(chatId: String, patientId: String, doctorId: String) {
        _chatId.value = chatId
        _patientId.value = patientId
        _doctorId.value = doctorId

        loadChat(chatId)
        initializeCurrentUser()
    }

    fun observeMessages(chatId: String) {
        messageListenerRegistration?.remove() // Clean up any existing listener

        messageListenerRegistration = chatMessagesRepository.listenToMessages(chatId) { newMessages ->
            println("🔥 observeMessages received ${newMessages.size} messages")
            _messages.value = newMessages
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageListenerRegistration?.remove()
    }

    suspend fun getAllDoctors(): List<Doctor> {
        return doctorRepository.getAllDoctors()
    }


    fun initializeCurrentUser() {
        Firebase.auth.currentUser?.let { user ->
            val name = if (user.uid == patientId) "Patient" else "Doctor"

            val role = if (user.uid == patientId) Role.PATIENT else Role.DOCTOR
            setCurrentUser(user.uid, name, role)
        }
    }

    var chatRoom by mutableStateOf<ChatRoom?>(null)
        private set

    fun setCurrentUser(id: String, name: String, role: Role) {
        _currentUser.value = Author(id = id, name = name, role = role)
    }

    suspend fun getDoctor(doctorId: String): Doctor? {
        return doctorRepository.getDoctorById(doctorId)
    }

    suspend fun getPatient(patientId: String): Patient? {
        return patientRepository.getPatientById(patientId)
    }

    fun loadChat(chatId: String) {
        println("ChatViewModel: Loading chat with ID: $chatId")
        launchCatching {
            chatRoom = chatMessagesRepository.getChatRoomById(chatId) 
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

        launchCatching {
            chatMessagesRepository.sendMessage(chatId, newMessage)

            val recipientId = if (me.id == patientId) doctorId else patientId

            notificationManager.triggerChatNotification(
                chatId = chatId,
                message = text,
                senderId = me.id,
                senderName = me.name,
                recipientId = recipientId
            )
            Log.d("ChatViewModel", "Triggered chat notification with Message sent: $text")
        }

        loadChat(chatId)
    }

    // Function to send an image
    fun sendImage(uri: Uri, message: Message, chatId: String) {
        if (chatId.isEmpty()) return
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
                chatMessagesRepository.sendMessage(chatId, newMessage)

                val recipientId = if (me.id == patientId) doctorId else patientId
                notificationManager.triggerChatNotification(
                    chatId = chatId,
                    message = "${me.name} sent an image",
                    senderId = me.id,
                    senderName = me.name,
                    recipientId = recipientId
                )
            }
        }
    }

    // Function to send a document
    fun sendDocument(uri: Uri, message: Message, chatId: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "chat_documents/${UUID.randomUUID()}.pdf"
        val documentRef = storageRef.child(fileName)

        launchCatching {
            currentUser.let {
                // upload to storage
                documentRef.putFile(uri).await()
                val downloadUrl = documentRef.downloadUrl.await()

                val newMessage = message.copy(documentUrl = downloadUrl.toString())
                _messages.value += newMessage

                // Send message to Firebase
                chatMessagesRepository.sendMessage(chatId, newMessage)

                val recipientId = if (me.id == patientId) doctorId else patientId
                notificationManager.triggerChatNotification(
                    chatId = chatId,
                    message = "${me.name} sent an document",
                    senderId = me.id,
                    senderName = me.name,
                    recipientId = recipientId
                )
            }
        }
    }

    suspend fun handleReferralClick(referredDoctorId: String): Pair<String, String>? {
        val patient = getPatient(patientId) ?: return null
        val referredDoctor = getDoctor(referredDoctorId) ?: return null

        val newChatId = addChatRoomRepository.getOrCreateChatRoomId(patient, referredDoctor)

        val chatRoomRef = FirebaseFirestore.getInstance().collection("chatrooms").document(newChatId)
        chatRoomRef.update("referrerDoctorId", doctorId).await()

        val introMessage = Message(
            text = "Hi, I've been referred to you by Dr. ${getDoctor(doctorId)?.name}.",
            author = Author(patient.id, "Patient"),
            timestamp = System.currentTimeMillis(),
            metadata = mapOf(
                "type" to "referral",
                "referralDoctorId" to doctorId,
                "referralDoctorName" to getDoctor(doctorId)?.name.orEmpty(),
                "referralSpecialization" to getDoctor(doctorId)?.specialization.orEmpty()
            )
        )
        chatMessagesRepository.sendMessage(newChatId, introMessage)

        return newChatId to referredDoctor.id
    }

    fun sendReferralMessage(selectedDoctor: Doctor) {
        val referralMessage = Message(
            text = "Patient referred to Dr. ${selectedDoctor.name} (${selectedDoctor.specialization})",
            author = me, // Use the existing me property
            timestamp = System.currentTimeMillis(),
            metadata = mapOf(
                "referralDoctorId" to selectedDoctor.id,
                "referralDoctorName" to selectedDoctor.name,
                "referralSpecialization" to (selectedDoctor.specialization ?: "Specialist"),
                "messageType" to "referral"
            )
        )

        // Add to local messages first
        _messages.value += referralMessage

        launchCatching {
            // Send to Firebase
            chatMessagesRepository.sendMessage(chatId, referralMessage)

            // Send notification
            val recipientId = if (me.id == patientId) doctorId else patientId
            notificationManager.triggerChatNotification(
                chatId = chatId,
                message = "You've been referred to Dr. ${selectedDoctor.name}",
                senderId = me.id,
                senderName = me.name,
                recipientId = recipientId
            )
            Log.d("ChatViewModel", "Sent referral message for Dr. ${selectedDoctor.name}")
        }

        // Refresh the chat
        loadChat(chatId)
    }


}