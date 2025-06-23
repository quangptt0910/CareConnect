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

@Composable
fun PatientMedicalHistoryScreen(
    viewModel: PatientMedicalHistoryViewModel = hiltViewModel(),
    goBack: () -> Unit
){
    val entries by viewModel.entries.collectAsState()
    val patientId by viewModel.patientId.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableStateOf(0) }

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