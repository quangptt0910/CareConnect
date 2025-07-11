package com.example.careconnect.screens.patient.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.careconnect.dataclass.chat.Message


/**
 * A composable that renders a search bar to search through chat messages.
 *
 * @param query The current search query string.
 * @param onSearchQueryChange Lambda to handle changes in the search query input.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSectionMenu(
    query: String,
    onSearchQueryChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { onSearchQueryChange(it) },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Search in chats...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (query.isNotEmpty()) {
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
    ) {}
}

/**
 * A card composable representing a referral message in the chat,
 * showing which doctor the patient has been referred to.
 *
 * @param message The referral message containing metadata about the referral.
 * @param onReferralClick Callback when the referral card is clicked,
 *                      passing the referred doctor's ID.
 */
@Composable
fun ReferralMessageItem(
    message: Message,
    onReferralClick: (String) -> Unit
) {
    val doctorName = message.metadata?.get("referralDoctorName") ?: "a doctor"
    val specialization = message.metadata?.get("referralSpecialization") ?: "Specialist"

    Card(
        modifier = Modifier.padding(8.dp).clickable { message.metadata?.get("referralDoctorId")?.let { onReferralClick(it) } },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("You've been referred to Dr. $doctorName ($specialization).", fontWeight = FontWeight.Bold)
            Text("Tap here to start the consultation.")
        }
    }
}

/**
 * A card composable representing an introductory referral message,
 * typically sent when a referral chat starts.
 *
 * @param message The referral intro message containing metadata about the referrer doctor.
 */
@Composable
fun ReferralIntroMessageItem(
    message: Message
) {
    val doctorName = message.metadata?.get("referrerDoctorName") ?: "a doctor"
    val specialization = message.metadata?.get("referrerSpecialization") ?: "Specialist"

    Card(
        modifier = Modifier.padding(8.dp)
        // Not clickable
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Hi, I've been referred to you by Dr. $doctorName ($specialization).",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * A composable that represents a single chat item in the chat list,
 * showing the contact's name, last message, time, and profile image.
 *
 * @param name The display name of the chat contact.
 * @param message The preview text of the last message.
 * @param time The timestamp string for the last message.
 * @param imageRes The URL or resource identifier of the contact's profile image.
 * @param onChatClicked Callback triggered when the chat item is clicked.
 */
@Composable
fun ChatListItem(
    name: String,
    message: String,
    time: String,
    imageRes: String, // Resource ID of the profile image
    onChatClicked: () -> Unit
) {
    Column(
        Modifier.clickable(
            onClick = onChatClicked
        )
    ) {
        ListItem(
            modifier = Modifier.padding(8.dp),
            headlineContent = { Text(text = name) },
            supportingContent = { Text(
                text = message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) },
            leadingContent = {
                Image(
                    painter = rememberAsyncImagePainter(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(55.dp) // Ensures width and height are the same
                        .clip(CircleShape) // Clips the image into a circle
                )
            },
            trailingContent = {
                Text(text = time)

            }

        )
        HorizontalDivider(modifier = Modifier.padding(start = 90.dp))
    }
}