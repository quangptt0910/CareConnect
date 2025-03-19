package com.example.careconnect.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.careconnect.dataclass.Doctor

/**
 * Composable function for displaying a search bar and search results.
 *
 * @param uiState UI state containing search information.
 * @param onProductSelected Callback for selecting products.
 * @param onSearchQueryChange Callback for updating search queries.
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
private fun SearchResults(
    uiState: HomeUiState,
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


/**
 * Displays a loading indicator while search results are being fetched.
 */
@Composable
private fun LoadingIndicator() {
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
private fun ErrorMessage(message: String) {
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
private fun SuggestionsList(
    suggestions: List<Doctor>,
    selectedDoctors: List<Doctor>,
    onDoctorSelected: (Doctor, Boolean) -> Unit
) {
    suggestions.forEach { doctor ->
        val isSelected = selectedDoctors.contains(doctor)
        ListItem(
            headlineContent = {
                Text(doctor.name ?: "Unknown doctor")
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