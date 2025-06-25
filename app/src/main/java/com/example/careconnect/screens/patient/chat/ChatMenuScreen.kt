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
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Composable screen displaying the chat menu for a patient or doctor user.
 *
 * This screen shows a searchable list of chat rooms the user is part of,
 * allowing navigation to individual chat conversations.
 * It also includes a top app bar with back navigation and a notifications icon.
 *
 * @param openChatScreen Callback to open a specific chat screen with provided doctorId, patientId, and chatId.
 * @param onBack Callback invoked when the back navigation icon is pressed.
 * @param openNotificationsScreen Optional callback to open the notifications screen (default empty).
 * @param viewModel The [ChatMenuViewModel] providing UI state and data.
 */
@Composable
fun ChatMenuScreen(
    openChatScreen : (doctorId: String, patientId: String, chatId: String) -> Unit,
    onBack: () -> Unit,
    openNotificationsScreen: () -> Unit = {},
    viewModel: ChatMenuViewModel = hiltViewModel()
){

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
        onBack = onBack,
        openNotificationsScreen = openNotificationsScreen
    )
}

/**
 * Composable rendering the content of the chat menu screen.
 *
 * Displays a search input, a list of filtered and sorted chat rooms, and handles user interactions.
 *
 * @param uiState The current UI state including search query and loading status.
 * @param onBack Callback for back navigation.
 * @param chatRoom List of chat rooms available for the user.
 * @param chatPartners Map of chat partner IDs to their respective [Doctor] or [Patient] data.
 * @param userRole The role of the current user ([Role.PATIENT] or [Role.DOCTOR]).
 * @param onSearchQueryChange Callback invoked when the search query changes.
 * @param openChatScreen Callback to open a selected chat room.
 * @param openNotificationsScreen Callback to open the notifications screen.
 */
@Composable
fun ChatMenuScreenContent(
    uiState: ChatMenuUiState,
    onBack: () -> Unit,
    chatRoom: List<ChatRoom>,
    chatPartners: Map<String, Any>,
    userRole: Role,
    onSearchQueryChange: (String) -> Unit,
    openChatScreen: (doctorId: String, patientId: String, chatId: String) -> Unit,
    openNotificationsScreen: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        ChatMenuTopBar(
            onBack = onBack,
            openNotificationsScreen = openNotificationsScreen
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

            val filteredChats = chatRoom
                .sortedByDescending { it.lastUpdated }
                .filter { chat ->
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
                            Role.DOCTOR -> R.drawable.ic_launcher_background.toString()
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

/**
 * Composable for the top app bar of the chat menu screen.
 *
 * Displays a title, a back navigation icon, and a notifications icon.
 *
 * @param onBack Callback when the back button is pressed.
 * @param openNotificationsScreen Callback when the notifications icon is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMenuTopBar(
    onBack: () -> Unit = {},
    openNotificationsScreen: () -> Unit = {}
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
                actions = {
                    Spacer(modifier = Modifier.width(100.dp))  // Add spacing before the icon
                    IconButton(onClick = { openNotificationsScreen() }) {
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

/**
 * Preview composable for the chat menu screen content.
 *
 * Shows a sample list of chat rooms and chat partners with dummy data.
 * Useful for UI design and testing without running the full app.
 */
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
