package com.example.careconnect.screens.patient.chat

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a selected media item with its URI, type, and optional name.
 *
 * @property uri The content URI of the selected media.
 * @property type The type of the media (IMAGE or DOCUMENT).
 * @property name The optional name of the media, used primarily for documents.
 */
data class SelectedMedia(
    val uri: Uri,
    val type: MediaType,
    val name: String = ""
)

/**
 * Enum representing supported media types for sending in chat.
 */
enum class MediaType {
    IMAGE, DOCUMENT
}

/**
 * Composable that displays the chat screen UI for a conversation between a patient and a doctor.
 *
 * It handles loading and displaying doctor, patient, and chat room data, manages notifications,
 * and integrates chat message list and message input UI.
 *
 * @param viewModel The [ChatViewModel] managing chat state and business logic.
 * @param chatId The unique identifier for the chat room.
 * @param patientId The ID of the patient participant in the chat.
 * @param doctorId The ID of the doctor participant in the chat.
 * @param openChatScreen Lambda function to open another chat screen with provided chatId, patientId, and doctorId.
 * @param goBack Lambda function to handle back navigation.
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    chatId: String,
    patientId: String,
    doctorId: String,
    openChatScreen: (String, String, String) -> Unit,
    goBack: () -> Unit = {}
){
    val context = LocalContext.current

    var doctor by remember { mutableStateOf<Doctor?>(null) }
    var patient by remember { mutableStateOf<Patient?>(null) }

    println("ChatScreen: chatId=$chatId, patientId=$patientId, doctorId=$doctorId")

    LaunchedEffect(patientId, doctorId, chatId) {
        doctor = viewModel.getDoctor(doctorId)
        println("ChatScreen: doctor=$doctor")
        patient = viewModel.getPatient(patientId)
        println("ChatScreen: patient=$patient")

        viewModel.initialize(chatId, patientId, doctorId)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(chatId.hashCode())
    }

    val chatRoom = viewModel.chatRoom

    // Pass only the necessary data to ChatScreenContent
    if (doctor != null && patient != null && chatRoom != null) {
        ChatScreenContent(
            model = viewModel,
            chatRoom = chatRoom,
            patient = patient!!,
            doctor = doctor!!,
            openChatScreen = openChatScreen,
            goBack = goBack
        )
    } else {
        println("ChatScreen: doctor=$doctor, patient=$patient, chatRoom=$chatRoom")
    }
}

/**
 * Composable displaying the main content of the chat screen, including the message list,
 * chat input box with media preview, and optional referral dialog.
 *
 * @param model The [ChatViewModel] containing chat data and actions.
 * @param chatRoom The [ChatRoom] data representing the current chat.
 * @param doctor The [Doctor] participant data.
 * @param patient The [Patient] participant data.
 * @param openChatScreen Lambda to open another chat screen.
 * @param goBack Lambda to navigate back.
 */
@Composable
fun ChatScreenContent(
    model: ChatViewModel,
    chatRoom: ChatRoom,
    doctor: Doctor,
    patient: Patient,
    openChatScreen: (String, String, String) -> Unit,
    goBack: () -> Unit = {}
) {

    val listState = rememberLazyListState()
    val messages by model.messages.collectAsStateWithLifecycle()

    println("🟡 Composable sees ${messages.size} messages")

    LaunchedEffect(messages){
        println("🟢 LaunchedEffect triggered with ${messages.size} messages")

        if (messages.isNotEmpty()){
            println("There are messages")
            listState.animateScrollToItem(messages.lastIndex)
        }
        else (println("No messages"))
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        val currentUserId = model.me.id
        val chatName = model.chatRoom?.let {
            if (it.doctorId == currentUserId) {
                patient.name
            } else {
                doctor.name
            }
        }

        if (chatName != null) {
            SmallTopAppBarExample(
                name = chatName,
                goBack = goBack
            )
        }

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (messageListRef, chatBox) = createRefs()

            if (model.showReferralDialog) {
                DoctorReferralDialog(
                    onDismiss = { model.showReferralDialog = false },
                    onDoctorSelected = { selectedDoctor ->
                        model.sendReferralMessage(selectedDoctor)
                        model.showReferralDialog = false
                    },
                    viewModel = model
                )
            }

            // Message List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp)
                    .constrainAs(messageListRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(chatBox.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
                state = listState
            ) {
                items(messages) { message ->
                    ChatItem(
                        message = message,
                        openNewChat = { newChatId, doctorId ->
                            openChatScreen(newChatId, patient.id, doctorId)
                        },
                        handleReferralClick = { referredDoctorId -> model.handleReferralClick(referredDoctorId) }
                    )
                }
            }

            // ChatBox with media preview
            ChatBoxWithPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(chatBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                onSend = { text -> model.sendMessage(text, chatRoom.chatId) },
                onSendWithMedia = { text, media ->
                    when (media.type) {
                        MediaType.IMAGE -> {
                            model.sendImage(
                                media.uri,
                                message = Message(
                                    text = text.ifEmpty { "Image" },
                                    author = model.me,
                                    imageUrl = null
                                ),
                                chatId = chatRoom.chatId
                            )
                        }
                        MediaType.DOCUMENT -> {
                            model.sendDocument(
                                media.uri,
                                message = Message(
                                    text = text.ifEmpty { "Document" },
                                    author = model.me,
                                    documentUrl = null,
                                    documentName = media.name
                                ),
                                chatId = chatRoom.chatId
                            )
                        }
                    }
                },
                viewModel = model
            )
        }
    }
}

/**
 * Composable that provides the chat input box with optional media attachment preview.
 *
 * Supports sending text messages, images, and documents. Manages media picking via
 * activity results and dropdown menu for media selection and referrals.
 *
 * @param modifier Modifier to apply to this composable.
 * @param onSend Callback invoked with the typed text when sending a plain message.
 * @param onSendWithMedia Callback invoked with typed text and selected media when sending media.
 * @param viewModel The [ChatViewModel] used for managing chat state and user info.
 */
@Composable
fun ChatBoxWithPreview(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit,
    onSendWithMedia: (String, SelectedMedia) -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var selectedMedia by remember { mutableStateOf<SelectedMedia?>(null) }

    val context = LocalContext.current

    // Image launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedMedia = SelectedMedia(
                uri = it,
                type = MediaType.IMAGE
            )
        }
        expanded = false
    }

    // Document launcher
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val documentName = getDocumentName(context, it) ?: "Document"
            selectedMedia = SelectedMedia(
                uri = it,
                type = MediaType.DOCUMENT,
                name = documentName
            )
        }
        expanded = false
    }

    Column(modifier = modifier) {
        // Media Preview Section
        selectedMedia?.let { media ->
            MediaPreviewCard(
                media = media,
                onRemove = { selectedMedia = null }
            )
        }

        // Chat Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plus (+) button for dropdown menu
            Box {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Attachments")
                }

                MinimalDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    onImageSend = { imageLauncher.launch("image/*") },
                    onDocumentSend = { documentLauncher.launch("application/*") },
                    onReferralSend = if (viewModel.me.role == Role.DOCTOR) {
                        { viewModel.showReferralDialog = true }
                    } else null
                )
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        if (selectedMedia != null) "Add a message (optional)..."
                        else "Type a message..."
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )

            // Send button
            IconButton(
                onClick = {
                    selectedMedia?.let { media ->
                        onSendWithMedia(text, media)
                        selectedMedia = null
                        text = ""
                    } ?: run {
                        if (text.isNotBlank()) {
                            onSend(text)
                            text = ""
                        }
                    }
                },
                enabled = selectedMedia != null || text.isNotBlank()
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

/**
 * Displays a preview card for the selected media (image or document) in the chat input area.
 *
 * @param media The selected media to preview.
 * @param onRemove Callback invoked when the user removes the selected media.
 */
@Composable
fun MediaPreviewCard(
    media: SelectedMedia,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (media.type) {
                MediaType.IMAGE -> {
                    AsyncImage(
                        model = media.uri,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Image selected",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                MediaType.DOCUMENT -> {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Document",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = media.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Document selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Helper function to get document name from URI
/**
 * Retrieves the display name of a document from its [Uri].
 *
 * @param context The context to access content resolver.
 * @param uri The Uri of the document.
 * @return The display name of the document if available, or the last path segment of the Uri.
 */
fun getDocumentName(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                cursor.getString(nameIndex)
            } else null
        }
    } catch (e: Exception) {
        uri.lastPathSegment
    }
}

/**
 * Displays an individual chat message item in the message list.
 * Handles different message types including text, image, document, and referral messages.
 *
 * @param message The message data to display.
 * @param openNewChat Callback to open a new chat screen with given chat and doctor IDs.
 * @param handleReferralClick Suspend function to handle referral clicks, returning the chat and doctor IDs.
 */
@Composable
fun ChatItem(
    message: Message,
    openNewChat: (String, String) -> Unit,
    handleReferralClick: suspend (String) -> Pair<String, String>?,
) {

    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (message.isFromMe) {
            Text(
                text = formatTimestamp(message.timestamp),
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 4.dp)
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
            when (message.metadata?.get("messageType")) {
                "referral" -> {
                    ReferralMessageItem(
                        message = message,
                        onReferralClick = { referralDoctorId ->
                            coroutineScope.launch {
                                val result = handleReferralClick(referralDoctorId)
                                result?.let { (chatId, referredDoctorId) ->
                                    openNewChat(chatId, referredDoctorId)
                                }
                            }
                        }
                    )
                }
                "referral_intro" -> {
                    ReferralIntroMessageItem(message = message)
                }
                else -> {
                    Column {
                        if (message.text.isNotEmpty()) {
                            Text(
                                text = message.text,
                                color = if (message.isFromMe) Color.White else Color.Black
                            )
                        }

                        message.imageUrl?.let { url ->
                            if (message.text.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            ImagePreview(model = url)
                        }

                        message.documentUrl?.let { url ->
                            if (message.text.isNotEmpty() || message.imageUrl != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            DocumentPreview(
                                documentName = message.documentName ?: "Document",
                                documentUrl = url,
                                isFromMe = message.isFromMe
                            )
                        }
                    }
                }
            }
        }

        if (!message.isFromMe) {
            Text(
                text = formatTimestamp(message.timestamp),
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/**
 * Shows an image preview inside the chat message.
 * Clicking the image opens it in an external viewer.
 *
 * @param model The URL or Uri string of the image to display.
 */
@Composable
fun ImagePreview(model : String){
    val context = LocalContext.current

    AsyncImage(
        model = model,
        contentDescription = "Sent Image",
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(model), "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            },
        contentScale = ContentScale.Crop,
    )
}

/**
 * Shows a preview card for a document attached to a chat message.
 * Includes the document name and a button to open the document.
 *
 * @param documentName The display name of the document.
 * @param documentUrl The URL or Uri string of the document.
 * @param isFromMe Indicates if the document was sent by the current user.
 */
@Composable
fun DocumentPreview(documentName: String, documentUrl: String, isFromMe: Boolean) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFromMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Document Icon",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = documentName,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(documentUrl), "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            }) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open Document")
            }
        }
    }
}

/**
 * Formats a timestamp (in milliseconds) into a human-readable time string.
 *
 * @param timestamp The timestamp to format.
 * @return A formatted time string (e.g., "02:30 PM").
 */
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * A small top app bar composable with a title and back navigation button.
 *
 * @param name The title to display in the app bar.
 * @param goBack Callback invoked when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample(
    name: String = "Chat",
    goBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
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

/**
 * Dialog composable that allows a doctor user to refer the patient to another doctor.
 * Displays a list of available doctors excluding the current doctor.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed.
 * @param onDoctorSelected Callback invoked when a doctor is selected for referral.
 * @param viewModel The ChatViewModel used to retrieve the doctors list.
 */
@Composable
fun DoctorReferralDialog(
    onDismiss: () -> Unit,
    onDoctorSelected: (Doctor) -> Unit,
    viewModel: ChatViewModel
) {
    var doctorList by remember { mutableStateOf<List<Doctor>>(emptyList()) }

    LaunchedEffect(Unit) {
        doctorList = viewModel.getAllDoctors()
            .filter { it.id != viewModel.doctorId }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Refer to Another Doctor") },
        text = {
            Column {
                doctorList.forEach { doctor ->
                    TextButton(onClick = { onDoctorSelected(doctor) }) {
                        Text("${doctor.name} - ${doctor.specialization}")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

/**
 * A minimal dropdown menu for selecting media attachment options in the chat input.
 * Options include sending images, documents, and optionally referrals.
 *
 * @param expanded Whether the dropdown menu is expanded or not.
 * @param onDismissRequest Callback invoked to dismiss the dropdown menu.
 * @param onImageSend Callback invoked when the "Send image" option is selected.
 * @param onDocumentSend Callback invoked when the "Send document" option is selected.
 * @param onReferralSend Optional callback invoked when the "Send referral" option is selected.
 */
@Composable
fun MinimalDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onImageSend: () -> Unit,
    onDocumentSend: () -> Unit,
    onReferralSend: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                text = { Text("Send image") },
                onClick = onImageSend
            )
            DropdownMenuItem(
                text = { Text("Send document") },
                onClick = onDocumentSend
            )
            onReferralSend?.let {
                DropdownMenuItem(
                    text = { Text("Send referral") },
                    onClick = it
                )
            }
        }
    }
}

/**
 * Preview composable for ChatScreenContent showcasing chat UI with dummy data.
 */
@Preview
@Composable
fun ChatScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        ChatScreenContent(
            model = viewModel(),
            chatRoom = ChatRoom(),
            doctor = Doctor(),
            patient = Patient(),
            openChatScreen = { doctorId, patientId, chatId ->
                println("Opening chat with doctor ID: $doctorId and chat ID: $chatId")
            }
        )
    }
}