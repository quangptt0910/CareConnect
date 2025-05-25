package com.example.careconnect.screens.patient.chat

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.chat.ChatRoom
import com.example.careconnect.dataclass.chat.Message
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    chatId: String,
    patientId: String,
    doctorId: String,
){
    var doctor by remember { mutableStateOf<Doctor?>(null) }
    var patient by remember { mutableStateOf<Patient?>(null) }

    println("ChatScreen: chatId=$chatId, patientId=$patientId, doctorId=$doctorId")

    LaunchedEffect(patientId, doctorId, chatId) {
        doctor = viewModel.getDoctor(doctorId)
        println("ChatScreen: doctor=$doctor")
        patient = viewModel.getPatient(patientId)
        println("ChatScreen: patient=$patient")
        viewModel.loadChat(chatId)
        viewModel.initializeCurrentUser(patient, doctor, patientId)
    }

    val chatRoom = viewModel.chatRoom

    // Pass only the necessary data to ChatScreenContent
    if (doctor != null && patient != null && chatRoom != null) {
        ChatScreenContent(
            model = viewModel,
            chatRoom = chatRoom,
            patient = patient!!,
            doctor = doctor!!
        )
    } else {
        println("ChatScreen: doctor=$doctor, patient=$patient, chatRoom=$chatRoom")
    }
}

@Composable
fun ChatScreenContent(
    model: ChatViewModel,
    chatRoom: ChatRoom,
    doctor: Doctor,
    patient: Patient,
) {

    val listState = rememberLazyListState()
    val messages by model.messages.collectAsState()

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
                name = chatName
            )
        }

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (messageListRef, chatBox) = createRefs()

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
                //reverseLayout = true // Makes chat scroll from bottom up
            ) {
                items(messages) { message ->
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
                onSend = { text -> model.sendMessage(text, chatRoom.chatId) }
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
            // Display text if it's not null or empty
            message.text.let {
                Text(
                    text = it,
                    color = if (message.isFromMe) Color.White else Color.Black
                )
            }

            // Display image if the message contains an image URI
            message.imageUrl?.let { url ->
                Spacer(modifier = Modifier.height(8.dp)) // Spacing between text and image
                ImagePreview(
                    model = url
                )
            }

            message.documentUrl?.let { url ->
                Spacer(modifier = Modifier.height(8.dp)) // Spacing between text and image
                DocumentPreview(
                    documentName = message.documentName ?: "Document",
                    documentUrl = url,
                    isFromMe = message.isFromMe
                )
            }
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

@Composable
fun ImagePreview(model : String){
    val context = LocalContext.current

    AsyncImage(
        model = model,
        contentDescription = "Sent Image",
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable{
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(model), "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            },

        contentScale = ContentScale.Crop,
    )
}

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
                Icon(Icons.Default.OpenInNew, contentDescription = "Open Document")
            }
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
fun SmallTopAppBarExample(
    name: String = "Chat"
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
                    IconButton(onClick = { /* do something */ }) {
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


@Composable
fun ChatBox(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) } // Controls dropdown visibility
    var text by remember { mutableStateOf("") }

    // for image

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.sendImage(
            it,
            message = Message(
                text = "",
                author = viewModel.me,
                imageUrl = null
            ),
            chatId = viewModel.chatRoom?.chatId ?: ""
        ) }
    }

    // for document

    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val documentName = uri?.lastPathSegment ?: "Document"
        uri?.let { viewModel.sendDocument(
            it,
            message = Message(
                text = "",
                author = viewModel.me,
                documentUrl = null,
                documentName = documentName
            ),
            chatId = viewModel.chatRoom?.chatId ?: ""
        ) }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Plus (+) button for dropdown menu
        Box {
            IconButton(
                onClick = { expanded = true } // Open dropdown
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Attachments")
            }

            MinimalDropdownMenu(
                expanded,
                onDismissRequest = { expanded = false },
                onImageSend = { launcher.launch("image/*") },
                onDocumentSend = { launcher2.launch("application/*") }
            )
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

        //Send button
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

@Composable
fun MinimalDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, onImageSend: () -> Unit, onDocumentSend: () -> Unit) {
    //var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                text = { Text("Send image") },
                onClick = { onImageSend() }
            )
            DropdownMenuItem(
                text = { Text("Send document") },
                onClick = {  onDocumentSend() }
            )
        }
    }
}


@Preview
@Composable
fun ChatScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        ChatScreenContent(
            model = viewModel(),
            chatRoom = ChatRoom(),
            doctor = Doctor(),
            patient = Patient()
        )

    }
}