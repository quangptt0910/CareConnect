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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.chat.ChatRoomPatient
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun ChatMenuScreen(

){
    ChatMenuScreenContent(
        uiState = ChatMenuUiState(),
        onDoctorSelected = { _, _ -> },
        onChatClicked = {},
        chatRoom = listOf(),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMenuScreenContent(
    uiState: ChatMenuUiState,
    chatRoom: List<ChatRoomPatient>,
    onDoctorSelected: (Doctor, Boolean) -> Unit,
    onChatClicked: (ChatRoomPatient) -> Unit,

    ) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        SmallTopAppBarExample1()
        Column(
            modifier = Modifier.padding(top = 80.dp).fillMaxSize()
        ) {

            SearchSectionMenu(
                uiState = ChatMenuUiState(),
                onDoctorSelected = onDoctorSelected,
                onSearchQueryChange = { /* Handle search query change */ },
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Latest Chats",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {
                items(chatRoom.size) { index ->
                    val chat = chatRoom[index]
                    ChatListItem(
                        name = chat.doctor.name,
                        message = chat.lastMessage,
                        time = formatTimestamp(chat.lastUpdated),
                        imageRes = chat.doctor.profilePhoto
                    )

                    HorizontalDivider(modifier = Modifier.padding(start = 90.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample1() {
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
                        "Chat Now\nWith Doctor",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
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
            ChatRoomPatient(
                chatId = "chat_001",
                doctor = Doctor(
                    id = "doc_001",
                    name = "Dr. Emma Thompson",
                    profilePhoto = R.drawable.carousel_image_1.toString()
                ),
                lastMessage = "Please remember to take your meds.",
                lastUpdated = System.currentTimeMillis() - 600_000 // 10 min ago
            ),
            ChatRoomPatient(
                chatId = "chat_002",
                doctor = Doctor(
                    id = "doc_002",
                    name = "Dr. John Miller",
                    profilePhoto = "https://via.placeholder.com/150"
                ),
                lastMessage = "Your blood test came back normal.",
                lastUpdated = System.currentTimeMillis() - 3_600_000 // 1 hour ago
            ),
            ChatRoomPatient(
                chatId = "chat_003",
                doctor = Doctor(
                    id = "doc_003",
                    name = "Dr. Sarah Patel",
                    profilePhoto = "https://via.placeholder.com/150"
                ),
                lastMessage = "Let's schedule your next checkup.",
                lastUpdated = System.currentTimeMillis() - 86_400_000 // 1 day ago
            )
        )

        ChatMenuScreenContent(
            uiState = uiState,
            chatRoom = sampleChatRooms,
            onDoctorSelected = { _, _ -> },
            onChatClicked = {}
        )
    }
}
