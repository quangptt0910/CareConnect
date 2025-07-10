package com.example.careconnect.screens.patient.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.chat.Author
import com.example.careconnect.dataclass.chat.Message
import com.example.careconnect.screens.patient.chat.formatTimestamp

/**
 * Composable function displaying a search bar and its results for doctors.
 *
 * @param uiState The UI state holding the current search query, suggestions, selected doctors, loading and error states.
 * @param onDoctorSelected Callback invoked when a doctor is selected or deselected. Provides the doctor and a boolean indicating selection state.
 * @param onSearchQueryChange Callback invoked when the search query changes.
 */
@ExperimentalMaterial3Api
@Composable
fun SearchSection(
    uiState: HomeUiState,
    onDoctorSelected: (Doctor, Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = uiState.searchQuery,
                onQueryChange = { query ->
                    onSearchQueryChange(query)
                    //if (query.isNotEmpty()) onSearchProducts(query)
                },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Search for doctors...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (uiState.searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                } else null
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        SearchResults(
            uiState = uiState,
            onDoctorSelected = onDoctorSelected
        )
    }
}


/**
 * Displays the search results for food products.
 *
 * @param uiState The UI state containing search results and status.
 * @param onProductSelected Callback invoked when a product is selected or deselected.
 */
@Composable
fun SearchResults(
    uiState: HomeUiState,
    onDoctorSelected: (Doctor, Boolean) -> Unit
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.SnackBarMessage != null -> SnackBarMessage(uiState.SnackBarMessage)
            else -> SuggestionsList(
                suggestions = uiState.suggestions,
                selectedDoctors = uiState.selectedDoctors,
                onDoctorSelected = onDoctorSelected
            )
        }
    }
}


/**
 * Displays a loading indicator while search results are being fetched.
 */
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Displays an error message when there is an issue fetching search results.
 *
 * @param message The error message to display.
 */
@Composable
fun SnackBarMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center
    )
}

/**
 * Displays a list of suggested food products based on the search query.
 *
 * @param suggestions List of suggested products retrieved from the search.
 * @param selectedProducts List of products currently selected by the user.
 * @param onProductSelected Callback invoked when a product is selected or deselected.
 */
@Composable
fun SuggestionsList(
    suggestions: List<Doctor>,
    selectedDoctors: List<Doctor>,
    onDoctorSelected: (Doctor, Boolean) -> Unit
) {
    suggestions.forEach { doctor ->
        val isSelected = selectedDoctors.contains(doctor)
        ListItem(
            headlineContent = {
                Text(doctor.name)
            },
            leadingContent = {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { checked ->
                        onDoctorSelected(doctor, checked)
                    }
                )
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    chatMessages: List<Message>, // Uses your Message data class
    onSendMessage: (text: String) -> Unit,
    currentAuthorId: String, // To determine if a message is from "me"
    chatPartnerName: String = "AI Assistant"
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.85f),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetDefaults.DragHandle()
                Text(chatPartnerName, style = MaterialTheme.typography.titleMedium)
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MessageList(
                messages = chatMessages,
                currentAuthorId = currentAuthorId, // Pass currentAuthorId
                modifier = Modifier.weight(1f),
                listState = listState
            )

            UserInput(
                onMessageSent = { text ->
                    onSendMessage(text)
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}

@Composable
fun MessageList(
    messages: List<Message>,
    currentAuthorId: String, // Changed from currentUserId
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
        reverseLayout = false
    ) {
        items(messages, key = { it.id }) { message ->
            MessageBubble(
                message = message,
                isUserMe = message.author.id == currentAuthorId // Compare with author.id
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun MessageBubble(
    message: Message,
    isUserMe: Boolean,
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val bubbleAlignment = if (isUserMe) Alignment.CenterEnd else Alignment.CenterStart
    val horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    val bubbleShape = if (isUserMe) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isUserMe) 32.dp else 0.dp,
                end = if (isUserMe) 0.dp else 32.dp
            ),
        horizontalArrangement = horizontalArrangement
    ) {
        Surface(
            color = backgroundBubbleColor,
            shape = bubbleShape,
            tonalElevation = 1.dp,
            modifier = Modifier.wrapContentWidth(Alignment.Start)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Display author's name if it's not the current user and name is available
                if (!isUserMe && message.author.name.isNotEmpty()) {
                    Text(
                        text = message.author.name, // Use author.name
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                if (message.text.isNotBlank()) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                message.imageUrl?.let { imageUrl ->
                    Spacer(modifier = Modifier.height(4.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Image attachment",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .sizeIn(maxWidth = 200.dp, maxHeight = 200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { /* Handle image click */ }
                    )
                }
                message.documentUrl?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    DocumentChip(
                        documentName = message.documentName ?: "Document",
                        onClick = { /* Handle document click */ }
                    )
                }

                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun DocumentChip(documentName: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                contentDescription = "Document Icon",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = documentName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInput(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }
    val isSendEnabled = text.isNotBlank()

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (isSendEnabled) {
                        onMessageSent(text)
                        text = ""
                    }
                },
                enabled = isSendEnabled,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSendEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = if (isSendEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- Preview ---
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Chat Bottom Sheet Preview")
@Composable
fun ChatBottomSheetWithAuthorPreview() {
    // Define sample Authors based on your Author.kt
    val sampleAuthorMe = Author(id = "authorId1", name = "Me")
    val sampleAuthorOther = Author(id = "authorId2", name = "AI Assistant") // Or a different role

    val sampleMessages = remember {
        mutableStateListOf(
            Message(id = "1", text = "Hello there!", author = sampleAuthorOther, timestamp = System.currentTimeMillis() - 200000),
            Message(id = "2", text = "Hi! How can I help you today regarding your symptoms?", author = sampleAuthorMe, timestamp = System.currentTimeMillis() - 100000),
            Message(id = "3", text = "I have a persistent cough and a slight fever.", author = sampleAuthorOther, timestamp = System.currentTimeMillis() - 50000),
            Message(id = "4", text = "Okay, I can provide some general information. Remember to consult a doctor for diagnosis.", author = sampleAuthorMe, timestamp = System.currentTimeMillis()),
            Message(id = "5", text = "Here is an image for context.", author = sampleAuthorOther, imageUrl = "https://example.com/placeholder_image.jpg", timestamp = System.currentTimeMillis() + 1000),
            Message(id = "6", text = "And a relevant document about general coughs.", author = sampleAuthorOther, documentUrl = "doc_url_example", documentName = "General_Cough_Info.pdf", timestamp = System.currentTimeMillis() + 2000)
        )
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    MaterialTheme { // Replace with your app's theme if different
        Box(modifier = Modifier.fillMaxSize()) {
            ChatBottomSheet(
                sheetState = sheetState,
                onDismiss = { },
                chatMessages = sampleMessages,
                onSendMessage = { newText ->
                    sampleMessages.add(Message(text = newText, author = sampleAuthorMe))
                },
                currentAuthorId = "authorId1", // Current user's Author ID
                chatPartnerName = "AI Symptom Helper"
            )
        }
    }
}

@Preview(name = "Message Bubble - User (Author)")
@Composable
fun MessageBubbleUserMeWithAuthorPreview() {
    MaterialTheme {
        MessageBubble(
            message = Message(text = "This is my message to you, it could be a bit longer to see how it wraps.", author = Author("author1", "Current User",), imageUrl = "https://via.placeholder.com/150"),
            isUserMe = true
        )
    }
}

@Preview(name = "Message Bubble - Other (Author)")
@Composable
fun MessageBubbleOtherUserWithAuthorPreview() {
    MaterialTheme {
        MessageBubble(
            message = Message(
                text = "This is their reply. And it has a document attached too! This document has a very long name that should be truncated.",
                author = Author("author2", "Dr. Bot"), // Or Role.DOCTOR if AI is like a doctor
                documentUrl = "url_example",
                documentName = "SuperExtremelyLongDocumentNameThatWillOverflowAndBeTruncated.pdf"
            ),
            isUserMe = false
        )
    }
}
