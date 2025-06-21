package com.example.careconnect.screens.doctor.patients.medicalhistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.vector.ImageVector
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

sealed class MedicalHistorySection {
    object Medications : MedicalHistorySection()
    data class MedicationsEdit(val existing: Medication) : MedicalHistorySection()

    object Allergies : MedicalHistorySection()
    data class AllergiesEdit(val existing: Allergy) : MedicalHistorySection()

    object Conditions : MedicalHistorySection()
    data class ConditionsEdit(val existing: Condition) : MedicalHistorySection()

    object Surgeries : MedicalHistorySection()
    data class SurgeriesEdit(val existing: Surgery) : MedicalHistorySection()

    object Immunizations : MedicalHistorySection()
    data class ImmunizationsEdit(val existing: Immunization) : MedicalHistorySection()

}

val MedicalHistorySection.title: String
    get() = when (this) {
        is MedicalHistorySection.Medications,
        is MedicalHistorySection.MedicationsEdit -> "Medications"
        is MedicalHistorySection.Allergies,
        is MedicalHistorySection.AllergiesEdit -> "Allergies"
        is MedicalHistorySection.Conditions,
        is MedicalHistorySection.ConditionsEdit -> "Conditions"
        is MedicalHistorySection.Surgeries,
        is MedicalHistorySection.SurgeriesEdit -> "Surgeries"
        is MedicalHistorySection.Immunizations,
        is MedicalHistorySection.ImmunizationsEdit -> "Immunizations"
    }

@Composable
fun MedicalHistorySectionScreen(
    patientId: String,
    section: String,
    onBack: () -> Unit,
    viewModel: MedicalHistorySectionViewModel = hiltViewModel()
) {
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
            else -> MedicalHistorySection.Medications
        },
        entries = entries,
        patient = viewModel.patient.value ?: Patient(),
        onBack = onBack,
        onAddEntry = { section, entry ->
            coroutineScope.launch {
                when (section) {
                    is MedicalHistorySection.Medications -> if (entry is Medication) {
                        viewModel.addMedication(patientId, entry)
                        viewModel.loadMedications(patientId)
                    }
                    is MedicalHistorySection.MedicationsEdit -> if (entry is Medication) {
                        viewModel.updateMedication(patientId, entry)
                        viewModel.loadMedications(patientId)
                    }

                    is MedicalHistorySection.Allergies -> if (entry is Allergy) {
                        viewModel.addAllergy(patientId, entry)
                        viewModel.loadAllergies(patientId)
                    }
                    is MedicalHistorySection.AllergiesEdit -> if (entry is Allergy) {
                        viewModel.updateAllergy(patientId, entry)
                        viewModel.loadAllergies(patientId)
                    }

                    is MedicalHistorySection.Conditions -> if (entry is Condition) {
                        viewModel.addCondition(patientId, entry)
                        viewModel.loadConditions(patientId)
                    }
                    is MedicalHistorySection.ConditionsEdit -> if (entry is Condition) {
                        viewModel.updateCondition(patientId, entry)
                        viewModel.loadConditions(patientId)
                    }

                    is MedicalHistorySection.Surgeries -> if (entry is Surgery) {
                        viewModel.addSurgery(patientId, entry)
                        viewModel.loadSurgeries(patientId)
                    }
                    is MedicalHistorySection.SurgeriesEdit -> if (entry is Surgery) {
                        viewModel.updateSurgery(patientId, entry)
                        viewModel.loadSurgeries(patientId)
                    }

                    is MedicalHistorySection.Immunizations -> if (entry is Immunization) {
                        viewModel.addImmunization(patientId, entry)
                        viewModel.loadImmunizations(patientId)
                    }
                    is MedicalHistorySection.ImmunizationsEdit -> if (entry is Immunization) {
                        viewModel.updateImmunization(patientId, entry)
                        viewModel.loadImmunizations(patientId)
                    }
                }
            }
        },
        onDeleteEntry = { entry ->
            coroutineScope.launch {
                when (entry) {
                    is Medication -> {
                        viewModel.deleteMedication(patientId, entry)
                        viewModel.loadMedications(patientId)
                    }
                    is Allergy -> {
                        viewModel.deleteAllergy(patientId, entry)
                        viewModel.loadAllergies(patientId)
                    }
                    is Condition -> {
                        viewModel.deleteCondition(patientId, entry)
                        viewModel.loadConditions(patientId)
                    }
                    is Surgery -> {
                        viewModel.deleteSurgery(patientId, entry)
                        viewModel.loadSurgeries(patientId)
                    }
                    is Immunization -> {
                        viewModel.deleteImmunization(patientId, entry)
                        viewModel.loadImmunizations(patientId)
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
    onAddEntry: (MedicalHistorySection, Any) -> Unit,
    onSectionChange: (MedicalHistorySection) -> Unit = {},
    onDeleteEntry: (Any) -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<Any?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(section.title, style = MaterialTheme.typography.titleLarge)
                },
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            when (section) {
                is MedicalHistorySection.Medications -> items(entries.filterIsInstance<Medication>()) { medication ->
                    MedicalHistoryCard(
                        icon = Icons.Default.MedicalServices,
                        title = medication.name,
                        details = listOf(
                            "Dosage: ${medication.dosage}",
                            "Frequency: ${medication.frequency}",
                            "From: ${medication.startDate} to ${medication.endDate}"
                        ),
                        onClick = {
                            showBottomSheet = true
                            selectedEntry = medication
                        }
                    )
                }
                is MedicalHistorySection.Allergies -> items(entries.filterIsInstance<Allergy>()) { allergy ->
                    MedicalHistoryCard(
                        icon = Icons.Default.MedicalServices,
                        title = allergy.allergen,
                        details = listOf(
                            "Severity: ${allergy.severity}",
                            "Reaction: ${allergy.reaction}"
                        ),
                        onClick = {
                            showBottomSheet = true
                            selectedEntry = allergy
                        }
                    )
                }
                is MedicalHistorySection.Conditions -> items(entries.filterIsInstance<Condition>()) { condition ->
                    MedicalHistoryCard(
                        icon = Icons.Default.MedicalServices,
                        title = condition.name,
                        details = listOf(
                            "Status: ${condition.status}",
                            "Diagnosed on: ${condition.diagnosedDate}"
                        ),
                        onClick = {
                            showBottomSheet = true
                            selectedEntry = condition
                        }
                    )
                }
                is MedicalHistorySection.Surgeries -> items(entries.filterIsInstance<Surgery>()) { surgery ->
                    MedicalHistoryCard(
                        icon = Icons.Default.MedicalServices,
                        title = surgery.surgeryName,
                        details = listOf(
                            "Date: ${surgery.surgeryDate}",
                            "Hospital: ${surgery.hospital}"
                        ),
                        onClick = {
                            showBottomSheet = true
                            selectedEntry = surgery
                        }
                    )
                }
                is MedicalHistorySection.Immunizations -> items(entries.filterIsInstance<Immunization>()) { immunization ->
                    MedicalHistoryCard(
                        icon = Icons.Default.MedicalServices,
                        title = immunization.vaccineName,
                        details = listOf(
                            "Date: ${immunization.dateAdministered}",
                            "Administered by: ${immunization.administeredBy}",
                            "Next due date: ${immunization.nextDueDate}"
                        ),
                        onClick = {
                            showBottomSheet = true
                            selectedEntry = immunization
                        }
                    )
                }
                is MedicalHistorySection.AllergiesEdit,
                is MedicalHistorySection.ConditionsEdit,
                is MedicalHistorySection.ImmunizationsEdit,
                is MedicalHistorySection.MedicationsEdit,
                is MedicalHistorySection.SurgeriesEdit -> {
                    item {
                        Spacer(modifier = Modifier.height(0.dp)) // or nothing
                    }
                }
            }
        }
    }

    if (showDialog) {
        when (val sec = section) {
            is MedicalHistorySection.Medications ->
                MedicationDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    }
                )
            is MedicalHistorySection.MedicationsEdit ->
                MedicationDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    },
                    existing = sec.existing
                )
            is MedicalHistorySection.Allergies ->
                AllergyDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    }
                )
            is MedicalHistorySection.AllergiesEdit ->
                AllergyDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    },
                    existing = sec.existing
                )
            is MedicalHistorySection.Conditions ->
                ConditionDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    }
                )
            is MedicalHistorySection.ConditionsEdit ->
                ConditionDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    },
                    existing = sec.existing
                )
            is MedicalHistorySection.Surgeries ->
                SurgeryDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    }
                )
            is MedicalHistorySection.SurgeriesEdit ->
                SurgeryDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    },
                    existing = sec.existing
                )
            is MedicalHistorySection.Immunizations ->
                ImmunizationDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    }
                )
            is MedicalHistorySection.ImmunizationsEdit ->
                ImmunizationDialog(
                    onDismiss = { showDialog = false },
                    onAdd = {
                        onAddEntry(sec, it)
                        showDialog = false
                    },
                    existing = sec.existing
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
                    selectedEntry?.let {
                        val editSection = when (it) {
                            is Medication -> MedicalHistorySection.MedicationsEdit(it)
                            is Allergy -> MedicalHistorySection.AllergiesEdit(it)
                            is Condition -> MedicalHistorySection.ConditionsEdit(it)
                            is Surgery -> MedicalHistorySection.SurgeriesEdit(it)
                            is Immunization -> MedicalHistorySection.ImmunizationsEdit(it)
                            else -> section
                        }
                        onSectionChange(editSection)
                        showDialog = true
                    }
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

@Composable
fun MedicalHistoryCard(
    icon: ImageVector,
    title: String,
    details: List<String>,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                details.forEach {
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicalHistorySectionScreenPreview() {
    MedicalHistorySectionScreenContent(
        section = MedicalHistorySection.Medications,
        patient = Patient(),
        entries = listOf(
            Medication("Paracetamol", "500mg", "2023-01-01", "2023-01-10", "Twice a day"),
            Medication("Ibuprofen", "200mg", "2023-02-01", "2023-02-07", "Once a day")
        ),
        onBack = {},
        onAddEntry = { _, _ -> }
    )
}
