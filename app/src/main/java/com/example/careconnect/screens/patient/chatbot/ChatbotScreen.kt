package com.example.careconnect.screens.patient.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.chat.Author
import com.example.careconnect.dataclass.chat.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatbotScreen(
    //onNavigateBack: () -> Unit,
    viewModel: ChatbotViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val patient by viewModel.patient.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var messageText by remember { mutableStateOf("") }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ChatbotScreenContent(
        //onNavigateBack = onNavigateBack,
        messages = messages,
        patient = patient,
        isLoading = isLoading,
        messageText = messageText,
        onMessageTextChange = { messageText = it },
        onSendMessage = {
            if (messageText.isNotBlank() && !isLoading) {
                viewModel.sendMessage(messageText)
                messageText = ""
                keyboardController?.hide()
            }
        },
        onQuickActionClick = { action -> viewModel.sendQuickAction(action) },
        listState = listState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreenContent(
    //onNavigateBack: () -> Unit,
    messages: List<Message>,
    patient: Patient?,
    isLoading: Boolean,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onQuickActionClick: (String) -> Unit,
    listState: LazyListState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Medical Assistant",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
//            navigationIcon = {
//                IconButton(onClick = onNavigateBack) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Back"
//                    )
//                }
//            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        // Welcome Message
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Hello ${patient?.name ?: "Patient"}!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "I'm your AI medical assistant. I can help you understand symptoms, suggest which specialists to consult, and provide basic health guidance. Remember, I'm here to support you, not replace professional medical advice.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Quick Action Buttons
        QuickActionButtons(onActionClick = onQuickActionClick)

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }

            // Loading indicator
            if (isLoading) {
                item {
                    LoadingBubble()
                }
            }
        }

        // Input Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask me about your health...") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(onSend = { onSendMessage() }),
                    maxLines = 3,
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = onSendMessage,
                    modifier = Modifier.size(48.dp),
                    containerColor = if (isLoading) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                    contentColor = if (isLoading) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isBot = message.author.isBot
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isBot) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                }
            ),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isBot) 4.dp else 20.dp,
                bottomEnd = if (isBot) 20.dp else 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                if (isBot) {
                    Text(
                        text = message.author.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isBot) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isBot) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    },
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun LoadingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 120.dp)
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 4.dp,
                bottomEnd = 20.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thinking...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Quick Action Buttons (Optional - you can add these below the welcome message)
@Composable
fun QuickActionButtons(
    onActionClick: (String) -> Unit
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            listOf(
                "Find a specialist",
                "Symptom checker",
                "Medication info",
                "Emergency signs"
            )
        ) { action ->
            FilterChip(
                onClick = { onActionClick(action) },
                label = { Text(action) },
                selected = false,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}

@Preview
@Composable
fun ChatbotScreenPreview() {
    ChatbotScreenContent(
        //onNavigateBack = {},
        messages = listOf(
            Message(
                text = "Hello! How can I assist you today?",
                author = Author(name = "AI Assistant", isBot = true),
                timestamp = System.currentTimeMillis()
            ),
            Message(
                text = "I have a headache and feel dizzy.",
                author = Author(name = "Patient", isBot = false),
                timestamp = System.currentTimeMillis() + 60000
            )
        ),
        patient = Patient(name = "John Doe"),
        isLoading = false,
        messageText = "",
        onMessageTextChange = {},
        onSendMessage = {},
        onQuickActionClick = {},
        listState = rememberLazyListState()
    )
}