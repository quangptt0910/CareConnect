package com.example.careconnect.screens.doctor.patients.medicalhistory

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Surgery
import kotlinx.coroutines.launch

sealed class MedicalHistorySection(val title: String) {
    object Medications : MedicalHistorySection("Medications")
    object Allergies : MedicalHistorySection("Allergies")
    object Conditions : MedicalHistorySection("Conditions")
    object Surgeries : MedicalHistorySection("Surgeries")
    object Immunizations : MedicalHistorySection("Immunizations")
}

@Composable
fun MedicalHistorySectionScreen(
    patientId: String,
    section: String,
    onBack: () -> Unit,
    viewModel: MedicalHistorySectionViewModel = hiltViewModel()
){
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(patientId) {
        viewModel.loadPatient(patientId)
        when (section) {
            "Medications" -> viewModel.loadMedications(patientId)
            "Allergies" -> viewModel.loadAllergies(patientId)
            "Conditions" -> viewModel.loadConditions(patientId)
            "Surgeries" -> viewModel.loadSurgeries(patientId)
            "Immunizations" -> viewModel.loadImmunizations(patientId)
        }
    }

    val medications by viewModel.medications.collectAsState()
    val allergies by viewModel.allergies.collectAsState()
    val conditions by viewModel.conditions.collectAsState()
    val surgeries by viewModel.surgeries.collectAsState()
    val immunizations by viewModel.immunizations.collectAsState()

    val entries: List<Any> = when (section) {
        "Medications" -> medications
        "Allergies" -> allergies
        "Conditions" -> conditions
        "Surgeries" -> surgeries
        "Immunizations" -> immunizations
        else -> listOf()
    }

    MedicalHistorySectionScreenContent(
        section = when (section) {
            "Medications" -> MedicalHistorySection.Medications
            "Allergies" -> MedicalHistorySection.Allergies
            "Conditions" -> MedicalHistorySection.Conditions
            "Surgeries" -> MedicalHistorySection.Surgeries
            "Immunizations" -> MedicalHistorySection.Immunizations
            else -> {MedicalHistorySection.Medications}
        },
        entries = entries,
        patient = viewModel.patient.value ?: Patient(),
        onBack = onBack,
        onAddEntry = { section, entry ->
            coroutineScope.launch {
                when (section) {
                    is MedicalHistorySection.Medications -> {
                        if (entry is Medication) {
                            viewModel.addMedication(patientId, entry)
                            viewModel.loadMedications(patientId)
                        }
                    }
                    is MedicalHistorySection.Allergies -> {
                        if (entry is Allergy) {
                            viewModel.addAllergy(patientId, entry)
                            viewModel.loadAllergies(patientId)
                        }
                    }
                    is MedicalHistorySection.Conditions -> {
                        if (entry is Condition) {
                            viewModel.addCondition(patientId, entry)
                            viewModel.loadConditions(patientId)
                        }
                    }
                    is MedicalHistorySection.Immunizations -> {
                        if (entry is Immunization) {
                            viewModel.addImmunization(patientId, entry)
                            viewModel.loadImmunizations(patientId)
                        }
                    }
                    is MedicalHistorySection.Surgeries -> {
                        if (entry is Surgery) {
                            viewModel.addSurgery(patientId, entry)
                            viewModel.loadSurgeries(patientId)
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHistorySectionScreenContent(
    section: MedicalHistorySection,
    patient: Patient,
    entries: List<Any>,
    onBack: () -> Unit,
    onAddEntry: (MedicalHistorySection, Any) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(section.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            when (section) {
                is MedicalHistorySection.Medications -> items(entries.filterIsInstance<Medication>()) {
                    Text("- ${it.name} (${it.dosage})")
                }
                is MedicalHistorySection.Allergies -> items(entries.filterIsInstance<Allergy>()) {
                    Text("- ${it.allergen} (${it.severity})")
                }
                is MedicalHistorySection.Conditions -> items(entries.filterIsInstance<Condition>()) {
                    Text("- ${it.name} (${it.status})")
                }
                is MedicalHistorySection.Surgeries -> items(entries.filterIsInstance<Surgery>()) {
                    Text("- ${it.surgeryName} (${it.surgeryDate})")
                }
                is MedicalHistorySection.Immunizations -> items(entries.filterIsInstance<Immunization>()) {
                    Text("- ${it.vaccineName} (${it.dateAdministered})")
                }
            }
        }
    }

    if (showDialog) {
        when (section) {
            is MedicalHistorySection.Medications -> MedicationDialog(onDismiss = { showDialog = false }, onAdd = {
                onAddEntry(section, it)
                showDialog = false
            }
            )
            is MedicalHistorySection.Allergies -> AllergyDialog(onDismiss = { showDialog = false }, onAdd = {
                onAddEntry(section, it)
                showDialog = false
            }
            )
            is MedicalHistorySection.Conditions -> ConditionDialog(onDismiss = { showDialog = false }) {
                onAddEntry(section, it)
                showDialog = false
            }
            is MedicalHistorySection.Surgeries -> SurgeryDialog(onDismiss = { showDialog = false }) {
                onAddEntry(section, it)
                showDialog = false
            }
            is MedicalHistorySection.Immunizations -> ImmunizationDialog(onDismiss = { showDialog = false }) {
                onAddEntry(section, it)
                showDialog = false
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MedicalHistorySectionScreenPreview() {
    MedicalHistorySectionScreenContent(
        section = MedicalHistorySection.Medications,
        patient = Patient(),
        onBack = {},
        onAddEntry = { _, _ -> },
        entries = listOf(
            Medication("Medication 1", "Dosage 1", "Start Date 1", "End Date 1", "Frequency 1"),
            Medication("Medication 2", "Dosage 2", "Start Date 2", "End Date 2", "Frequency 2")
        )
    )
}