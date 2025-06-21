package com.example.careconnect.screens.patient.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun ChatMenuScreen(
    openChatScreen : (doctorId: String, patientId: String, chatId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: ChatMenuViewModel = hiltViewModel()
){

    LaunchedEffect(Unit) {
        viewModel.setCurrentUser()
    }

    val doctor by viewModel.doctor.collectAsState()
    val patient by viewModel.currentPatient.collectAsState()
    val userRole by viewModel.currentUserRole.collectAsState()

    val doctorId = doctor?.id ?: ""
    val patientId = patient?.id ?: ""
    println("LaunchedEffect triggered: doctor=$doctor, patient=$patient")
    println("LaunchedEffect triggered: doctorId=$doctorId, patientId=$patientId")
    println("LaunchedEffect triggered: userRole=$userRole")



    LaunchedEffect(doctorId, patientId) {
        println("LaunchedEffect triggered: doctorId=$doctorId, patientId=$patientId")
        viewModel.loadChatRooms()
    }

    val chatRooms by viewModel.chatRooms.collectAsState()
    val chatPartners by viewModel.chatPartners.collectAsState()

    val uiState by viewModel.uiState.collectAsState()
    val searchQuery = uiState.searchQuery

    ChatMenuScreenContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        openChatScreen = openChatScreen,
        chatRoom = chatRooms,
        chatPartners = chatPartners,
        userRole = userRole,
        onBack = onBack
    )
}

@Composable
fun ChatMenuScreenContent(
    uiState: ChatMenuUiState,
    onBack: () -> Unit,
    chatRoom: List<ChatRoom>,
    chatPartners: Map<String, Any>,
    userRole: Role,
    onSearchQueryChange: (String) -> Unit,
    openChatScreen: (doctorId: String, patientId: String, chatId: String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        ChatMenuTopBar(
            onBack = onBack
        )
        Column(
            modifier = Modifier.padding(top = 90.dp).fillMaxSize()
        ) {

            SearchSectionMenu(
                query = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Latest Chats",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val filteredChats = chatRoom.filter { chat ->
                val chatPartner = when (userRole) {
                    Role.PATIENT -> chatPartners[chat.doctorId] as? Doctor
                    Role.DOCTOR -> chatPartners[chat.patientId] as? Patient
                    else -> null
                }

                val fullName = when (chatPartner) {
                    is Doctor -> "${chatPartner.name} ${chatPartner.surname}"
                    is Patient -> "${chatPartner.name} ${chatPartner.surname}"
                    else -> ""
                }

                fullName.contains(uiState.searchQuery, ignoreCase = true)
            }

            LazyColumn {
                items(filteredChats.size) { index ->
                    val chat = filteredChats[index]

                    val chatPartner = when (userRole) {
                        Role.PATIENT -> chatPartners[chat.doctorId] as? Doctor
                        Role.DOCTOR -> chatPartners[chat.patientId] as? Patient
                        else -> null
                    }


                    chatPartner?.let {
                        val displayName = when (userRole) {
                            Role.PATIENT -> "Dr. ${(it as Doctor).name} ${it.surname}"
                            Role.DOCTOR -> "${(it as Patient).name} ${it.surname}"
                            else -> ""
                        }

                        val profilePhoto = when (userRole) {
                            Role.PATIENT -> (it as Doctor).profilePhoto
                            Role.DOCTOR -> ""  // Patient may not have profile photo in your data model
                            else -> ""
                        }

                        ChatListItem(
                            name = displayName,
                            message = chat.lastMessage,
                            time = formatTimestamp(chat.lastUpdated),
                            imageRes = profilePhoto,
                            onChatClicked = {
                                openChatScreen(
                                    chat.doctorId, chat.patientId, chat.chatId
                                )
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(start = 90.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMenuTopBar(
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(
                        "Chat Now",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {  // <-- Add actions here (right side of the TopAppBar)
                    Spacer(modifier = Modifier.width(100.dp))  // Add spacing before the icon
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
    ){
        Box(modifier = Modifier.padding(it))
    }
}

@Preview
@Composable
fun ChatMenuScreenPreview() {
    CareConnectTheme {
        val uiState = ChatMenuUiState()
        val sampleChatRooms = listOf(
            ChatRoom(
                chatId = "chat_001",
                doctorId = "doc_001",
                patientId = "pat_001",
                lastMessage = "Please remember to take your meds.",
                lastUpdated = System.currentTimeMillis() - 600_000 // 10 min ago
            ),
            ChatRoom(
                chatId = "chat_002",
                doctorId = "doc_002",
                patientId = "pat_002",
                lastMessage = "Your blood test came back normal.",
                lastUpdated = System.currentTimeMillis() - 3_600_000 // 1 hour ago
            )
        )

        ChatMenuScreenContent(
            uiState = uiState,
            chatRoom = sampleChatRooms,
            chatPartners = mapOf(
                "doc_001" to Doctor(id = "doc_001", name = "Dr. John", surname = "Doe"),
                "pat_001" to Patient(id = "pat_001", name = "Jane", surname = ""),
                "doc_002" to Doctor(id = "doc_002", name = "Dr. Alice", surname = "Smith"),
                "pat_002" to Patient(id = "pat_002", name = "Bob", surname = "")
            ),
            userRole = Role.PATIENT,
            openChatScreen = {
                doctorId, patientId, chatId ->
                println("Opening chat with doctor ID: $doctorId and chat ID: $chatId")
            },
            onSearchQueryChange = {},
            onBack = {}
        )
    }
}
