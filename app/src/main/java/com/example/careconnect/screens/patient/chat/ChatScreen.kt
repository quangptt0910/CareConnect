package com.example.careconnect.screens.patient.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val MY_ID = "user_me"

@Composable
fun ChatScreen(

){

}


@Composable
fun ChatScreenContent(
    model: ChatViewModel = viewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        SmallTopAppBarExample()

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (messages, chatBox) = createRefs()

            // Message List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp)
                    .constrainAs(messages) {
                        top.linkTo(parent.top)
                        bottom.linkTo(chatBox.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
                //reverseLayout = true // Makes chat scroll from bottom up
            ) {
                items(model.messages) { message ->
                    ChatItem(message)
                }
            }

            // ChatBox
            ChatBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(chatBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                onSend = { text ->
                    model.sendMessage(text)
                }
            )
        }
    }
}

@Composable
fun ChatItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom // Ensures timestamp is aligned at bottom
    ) {
        if (message.isFromMe) {
            // Left-aligned timestamp (Other User)
            Text(
                text = formatTimestamp(message.timestamp),
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 4.dp) // Spacing from bubble
            )
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromMe) 16.dp else 0.dp,
                        bottomEnd = if (message.isFromMe) 0.dp else 16.dp
                    )
                )
                .background(if (message.isFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isFromMe) Color.White else Color.Black
            )
        }

        if (!message.isFromMe) {
            // Right-aligned timestamp (You)
            Text(
                text = formatTimestamp(message.timestamp),
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp) // Spacing from bubble
            )
        }
    }
}


// ✅ Helper function to format timestamp
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // e.g., "12:45 PM"
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Quang",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ){
        Box(modifier = Modifier.padding(it))
    }
}


@Composable
fun ChatBox(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Handle adding attachments */ }

        ){
            Icon(Icons.Filled.Add, contentDescription = "Attachments")
        }
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    text = ""
                }
            }
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
        }
    }
}

data class Message(
    val text: String,
    val author: Author,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isFromMe: Boolean
        get() = author.id == MY_ID
}


data class Author(
    val id: String,  // Unique ID for each author
    val name: String // Display name (optional)
)

@Preview
@Composable
fun ChatScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        ChatScreenContent(
        )
    }
}