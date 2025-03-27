package com.example.careconnect.screens.patient.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun ChatMenuScreen(

){

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMenuScreenContent(
    uiState: ChatMenuUiState,
    onDoctorSelected: (Doctor, Boolean) -> Unit,
    onChatClicked: (Doctor) -> Unit,

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

            val chatMessages = listOf(
                ChatMessage(
                    "Dr Salem",
                    "Hello patient A. HOW YOU DOING TODAY",
                    "10:00",
                    R.drawable.carousel_image_1
                ),
                ChatMessage(
                    "Dr Emma",
                    "Your test results are ready",
                    "09:45",
                    R.drawable.carousel_image_2
                ),
                ChatMessage(
                    "Dr John",
                    "Let's schedule a follow-up",
                    "08:30",
                    R.drawable.carousel_image_3
                )
            )

            ChatListScreen(messages = chatMessages)
        }
    }
}

@Composable
fun MessageList(messages: List<Message>) {
    val listState = rememberLazyListState()
    // Remember a CoroutineScope to be able to launch
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(state = listState) {
        // ...
    }

    ListItem(
        headlineContent = { Text(text = "message.text") },
        supportingContent = { Text(text = "message.timestamp") },
    )
}

@Composable
fun HorizontalDividerExample() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.carousel_image_1),
            contentDescription = null,
            modifier = Modifier.width(50.dp).height(50.dp)
        )
        HorizontalDivider(thickness = 2.dp)
        Text("Second item in list")
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
        ChatMenuScreenContent(
            uiState = uiState,
            onDoctorSelected = { _, _ -> },
            onChatClicked = {}
        )
    }
}