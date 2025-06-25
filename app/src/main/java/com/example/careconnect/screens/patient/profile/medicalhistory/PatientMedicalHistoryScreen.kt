package com.example.careconnect.screens.patient.profile.medicalhistory

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.dataclass.MedicalHistoryEntry
import com.example.careconnect.dataclass.MedicalHistoryType
import com.example.careconnect.screens.doctor.patients.medicalhistory.MedicalHistorySectionScreenContent
import kotlinx.coroutines.launch

val tabItems = listOf(
    MedicalHistoryType.MEDICATION,
    MedicalHistoryType.ALLERGY,
    MedicalHistoryType.CONDITION,
    MedicalHistoryType.SURGERY,
    MedicalHistoryType.IMMUNIZATION
)

/**
 * Composable screen displaying the medical history of a patient segmented by different medical history types.
 *
 * Shows tabs for medication, allergy, condition, surgery, and immunization history.
 * Loads and manages the patient's medical history entries for the selected type.
 *
 * @param viewModel The [PatientMedicalHistoryViewModel] to handle UI state and data operations.
 * @param type The initially selected [MedicalHistoryType] as a string, defaults to the first tab if invalid.
 * @param goBack Lambda to navigate back from this screen.
 */
@Composable
fun PatientMedicalHistoryScreen(
    viewModel: PatientMedicalHistoryViewModel = hiltViewModel(),
    type: String,
    goBack: () -> Unit
){
    val entries by viewModel.entries.collectAsState()
    val patientId by viewModel.patientId.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val initialTabIndex = remember(type) {
        tabItems.indexOfFirst { it.name.equals(type, ignoreCase = true) }
            .coerceAtLeast(0) // fall back to 0 if type is invalid
    }

    var selectedTabIndex by remember { mutableStateOf(initialTabIndex) }

    // Load entries whenever patientId or tabIndex changes
    LaunchedEffect(patientId, selectedTabIndex) {
        patientId?.let { viewModel.loadEntries(it, tabItems[selectedTabIndex]) }

    }

    patientId?.let {
        PatientMedicalHistoryScreenContent(
        patientId = it,
        goBack = goBack,
        entries = entries,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { selectedTabIndex = it },
            addEntry = { id, entry -> coroutineScope.launch { viewModel.addEntry(id, entry) } },
            updateEntry = { id, entry -> coroutineScope.launch { viewModel.updateEntry(id, entry) } },
            deleteEntry = { id, entry -> coroutineScope.launch { viewModel.deleteEntry(id, entry) } }
        )
    }
}

/**
 * Composable content for the patient medical history screen, including the tab layout and the medical history list.
 *
 * @param patientId The unique identifier of the patient whose history is displayed.
 * @param goBack Lambda invoked when the user wants to navigate back.
 * @param entries List of [MedicalHistoryEntry] currently displayed.
 * @param selectedTabIndex The index of the currently selected medical history tab.
 * @param onTabSelected Callback invoked when a tab is selected, with the new tab index.
 * @param addEntry Callback to add a new medical history entry for the patient.
 * @param updateEntry Callback to update an existing medical history entry.
 * @param deleteEntry Callback to delete a medical history entry.
 */
@Composable
fun PatientMedicalHistoryScreenContent(
    patientId: String,
    goBack: () -> Unit,
    entries : List<MedicalHistoryEntry>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    addEntry: (String, MedicalHistoryEntry) -> Unit,
    updateEntry: (String, MedicalHistoryEntry) -> Unit,
    deleteEntry: (String, MedicalHistoryEntry) -> Unit
    ){

    Column {
        ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
            tabItems.forEachIndexed { index, type ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        MedicalHistorySectionScreenContent(
            sectionType = tabItems[selectedTabIndex],
            entries = entries,
            onBack = goBack,
            onAddEntry = { addEntry(patientId, it) },
            onUpdateEntry = { updateEntry(patientId, it) },
            onDeleteEntry = { deleteEntry(patientId, it) },
            showSnackbar = { /* handle snackbar */ }
        )


    }
}