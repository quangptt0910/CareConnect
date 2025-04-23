package com.example.careconnect.screens.patient.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.screens.patient.home.ErrorMessage
import com.example.careconnect.screens.patient.home.LoadingIndicator
import com.example.careconnect.screens.patient.home.SuggestionsList

/**
 * Composable function for displaying a search bar and search results.
 *
 * @param uiState UI state containing search information.
 * @param onProductSelected Callback for selecting products.
 * @param onSearchQueryChange Callback for updating search queries.
 */
@ExperimentalMaterial3Api
@Composable
fun SearchSectionMenu(
    uiState: ChatMenuUiState,
    onDoctorSelected: (Doctor, Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth().height(50.dp)
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
        SearchMenuResults(
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
fun SearchMenuResults(
    uiState: ChatMenuUiState,
    onDoctorSelected: (Doctor, Boolean) -> Unit
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.errorMessage != null -> ErrorMessage(uiState.errorMessage)
            else -> SuggestionsList(
                suggestions = uiState.suggestions,
                selectedDoctors = uiState.selectedDoctors,
                onDoctorSelected = onDoctorSelected
            )
        }
    }
}

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
            supportingContent = { Text(text = message) },
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