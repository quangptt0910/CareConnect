package com.example.careconnect.screens.doctor.patients.medicalhistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.MedicalHistoryEntry
import com.example.careconnect.dataclass.MedicalHistoryType
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.Surgery
import kotlinx.coroutines.launch


/**
 * Enum to represent the mode of the dialog (Add or Edit).
 */
enum class DialogMode {
    Add, Edit
}


/**
 * Composable function for the Medical History Section screen.
 * This screen displays a list of medical history entries for a specific patient and section type (e.g., medications, allergies).
 * It allows users to add, edit, and delete entries.
 *
 * @param patientId The ID of the patient whose medical history is being viewed.
 * @param sectionType The type of medical history section to display (e.g., "medications", "allergies").
 * @param onBack Callback function to navigate back to the previous screen.
 * @param showSnackbar Callback function to display a snackbar message.
 * @param viewModel The [MedicalHistorySectionViewModel] used to manage the state and logic of this screen.
 */
@Composable
fun MedicalHistorySectionScreen(
    patientId: String,
    sectionType: String, // of type like 'medications', 'allergies'
    onBack: () -> Unit,
    showSnackbar: (SnackBarMessage) -> Unit,
    viewModel: MedicalHistorySectionViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val medicalHistoryType = MedicalHistoryType.fromCollectionName(sectionType)
        ?: MedicalHistoryType.MEDICATION

    LaunchedEffect(patientId) {
        viewModel.loadPatient(patientId)
        viewModel.loadEntries(patientId, medicalHistoryType)
    }

    val entries by viewModel.entries.collectAsState()
    val patient by viewModel.patient.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    MedicalHistorySectionScreenContent(
        sectionType = medicalHistoryType,
        entries = entries,
        onBack = onBack,
        onAddEntry = { entry ->
            coroutineScope.launch {
                val success = viewModel.addEntry(patientId, entry)
                if (success != null) {
                    viewModel.refreshEntries(patientId, medicalHistoryType)
                }
                // TODO: Show success/error message
            }
        },
        onUpdateEntry = { entry ->
            coroutineScope.launch {
                val success = viewModel.updateEntry(patientId, entry)
                if (success) {
                    viewModel.refreshEntries(patientId, medicalHistoryType)
                }
                // TODO: Show success/error message
            }
        },
        onDeleteEntry = { entry ->
            coroutineScope.launch {
                val success = viewModel.deleteEntry(patientId, entry)
                if (success) {
                    viewModel.refreshEntries(patientId, medicalHistoryType)
                }
                // TODO: Show success/error message
            }
        },
        showSnackbar = showSnackbar
    )
}


/**
 * Composable function for the content of the Medical History Section screen.
 * This function is responsible for the UI layout and user interactions.
 *
 * @param sectionType The [MedicalHistoryType] being displayed.
 * @param entries A list of [MedicalHistoryEntry] items to display.
 * @param isLoading A boolean indicating if data is currently loading.
 * @param onBack Callback function to navigate back.
 * @param onAddEntry Callback function to add a new [MedicalHistoryEntry].
 * @param onUpdateEntry Callback function to update an existing [MedicalHistoryEntry].
 * @param onDeleteEntry Callback function to delete a [MedicalHistoryEntry].
 * @param showSnackbar Callback function to display a snackbar message.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHistorySectionScreenContent(
    sectionType: MedicalHistoryType,
    entries: List<MedicalHistoryEntry>,
    isLoading: Boolean = false,
    onBack: () -> Unit,
    onAddEntry: (MedicalHistoryEntry) -> Unit,
    onUpdateEntry: (MedicalHistoryEntry) -> Unit,
    onDeleteEntry: (MedicalHistoryEntry) -> Unit,
    showSnackbar: (SnackBarMessage) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogMode by remember { mutableStateOf<DialogMode>(DialogMode.Add) }
    var selectedEntry by remember { mutableStateOf<MedicalHistoryEntry?>(null) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(sectionType.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                dialogMode = DialogMode.Add
                selectedEntry = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(entries) { entry ->
                MedicalHistoryCard(
                    entry = entry,
                    onClick = {
                        selectedEntry = entry
                        showBottomSheet = true
                    }
                )
            }
        }
    }

    // Generic dialog based on section type
    if (showDialog) {
        when (sectionType) {
            MedicalHistoryType.MEDICATION -> MedicationDialog(
                onDismiss = { showDialog = false },
                onSave = { medication ->
                    when (dialogMode) {
                        DialogMode.Add -> onAddEntry(medication)
                        DialogMode.Edit -> onUpdateEntry(medication)
                    }
                    showDialog = false
                },
                existing = if (dialogMode == DialogMode.Edit) selectedEntry as? Medication else null
            )
            MedicalHistoryType.ALLERGY -> AllergyDialog(
                onDismiss = { showDialog = false },
                onSave = { allergy ->
                    when (dialogMode) {
                        DialogMode.Add -> onAddEntry(allergy)
                        DialogMode.Edit -> onUpdateEntry(allergy)
                    }
                    showDialog = false
                },
                existing = if (dialogMode == DialogMode.Edit) selectedEntry as? Allergy else null
            )
            MedicalHistoryType.CONDITION -> ConditionDialog(
                onDismiss = { showDialog = false },
                onSave = { condition ->
                    when (dialogMode) {
                        DialogMode.Add -> onAddEntry(condition)
                        DialogMode.Edit -> onUpdateEntry(condition)
                    }
                    showDialog = false
                },
                existing = if (dialogMode == DialogMode.Edit) selectedEntry as? Condition else null
            )
            MedicalHistoryType.SURGERY -> SurgeryDialog(
                onDismiss = { showDialog = false },
                onSave = { surgery ->
                    when (dialogMode) {
                        DialogMode.Add -> onAddEntry(surgery)
                        DialogMode.Edit -> onUpdateEntry(surgery)
                    }
                    showDialog = false
                },
                existing = if (dialogMode == DialogMode.Edit) selectedEntry as? Surgery else null
            )
            MedicalHistoryType.IMMUNIZATION -> ImmunizationDialog(
                onDismiss = { showDialog = false },
                onSave = { immunization ->
                    when (dialogMode) {
                        DialogMode.Add -> onAddEntry(immunization)
                        DialogMode.Edit -> onUpdateEntry(immunization)
                    }
                    showDialog = false
                },
                existing = if (dialogMode == DialogMode.Edit) selectedEntry as? Immunization else null
            )
        }
    }

    if (showBottomSheet && selectedEntry != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            BottomSheetContent(
                onEdit = {
                    showBottomSheet = false
                    dialogMode = DialogMode.Edit
                    showDialog = true
                },
                onDelete = {
                    selectedEntry?.let { entry ->
                        onDeleteEntry(entry)
                        showBottomSheet = false
                    }
                }
            )
        }
    }

}


/**
 * Composable function for the content displayed within the modal bottom sheet.
 * It provides options to edit or delete a selected medical history entry.
 *
 * @param onEdit Callback function invoked when the "Edit" option is selected.
 * @param onDelete Callback function invoked when the "Delete" option is selected.
 */
@Composable
fun BottomSheetContent(onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select an action", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onEdit) {
            Text("Edit")
        }
        TextButton(onClick = onDelete) {
            Text("Delete", color = Color.Red)
        }
    }
}


/**
 * Composable function to display a single medical history entry in a card format.
 *
 * @param entry The [MedicalHistoryEntry] to display.
 * @param onClick Callback function invoked when the card is clicked.
 */
@Composable
fun MedicalHistoryCard(
    entry: MedicalHistoryEntry,
    onClick: () -> Unit = {}
) {
    val (title, details) = when (entry) {
        is Medication -> entry.name to listOf(
            "Dosage: ${entry.dosage}",
            "Frequency: ${entry.frequency}",
            "From: ${entry.startDate} to ${entry.endDate}"
        )
        is Allergy -> entry.allergen to listOf(
            "Severity: ${entry.severity}",
            "Reaction: ${entry.reaction}"
        )
        is Condition -> entry.name to listOf(
            "Status: ${entry.status}",
            "Diagnosed on: ${entry.diagnosedDate}"
        )
        is Surgery -> entry.surgeryName to listOf(
            "Date: ${entry.surgeryDate}",
            "Hospital: ${entry.hospital}"
        )
        is Immunization -> entry.vaccineName to listOf(
            "Date: ${entry.dateAdministered}",
            "Administered by: ${entry.administeredBy}",
            "Next due date: ${entry.nextDueDate}"
        )
        else -> "Unknown" to listOf("No details available")
    }

    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                Icons.Default.MedicalServices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                details.forEach { detail ->
                    Text(text = detail, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicalHistorySectionScreenPreview() {
    MedicalHistorySectionScreenContent(
        sectionType = MedicalHistoryType.ALLERGY,
        entries = listOf(
            Allergy("Paracetamol", "500mg", "2023-01-01", "2023-01-10", "Twice a day"),
            Allergy("Ibuprofen", "200mg", "2023-02-01", "2023-02-07", "Once a day")
        ),
        onBack = {},
        onAddEntry = {},
        onUpdateEntry = {},
        onDeleteEntry = {},
        showSnackbar = {}
    )
}
