package com.example.careconnect.screens.doctor.patients.medicalhistory

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Surgery

@Composable
fun MedicationDialog(
    onDismiss: () -> Unit,
    onSave: (Medication) -> Unit,
    existing: Medication? = null
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var dosage by remember { mutableStateOf(existing?.dosage ?: "") }
    var frequency by remember { mutableStateOf(existing?.frequency ?: "") }
    var startDate by remember { mutableStateOf(existing?.startDate ?: "") }
    var endDate by remember { mutableStateOf(existing?.endDate ?: "") }

    val isEditing = existing != null
    val dialogTitle = if (isEditing) "Edit Medication" else "Add Medication"
    val saveButtonText = if (isEditing) "Update" else "Add"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle)},
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                OutlinedTextField(dosage, { dosage = it }, label = { Text("Dosage") })
                OutlinedTextField(frequency, { frequency = it }, label = { Text("Frequency") })
                OutlinedTextField(startDate, { startDate = it }, label = { Text("Start Date") })
                OutlinedTextField(endDate, { endDate = it }, label = { Text("End Date") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val medication = if (isEditing) {
                    existing.copy(id = existing.id,name = name.trim(), dosage = dosage.trim(), frequency = frequency.trim(), startDate = startDate.trim(), endDate = endDate.trim())
                } else {
                    Medication(name = name.trim(), dosage = dosage.trim(), frequency = frequency.trim(), startDate = startDate.trim(), endDate = endDate.trim())
                }
                onSave(medication)
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AllergyDialog(
    onDismiss: () -> Unit,
    onSave: (Allergy) -> Unit,
    existing: Allergy? = null
) {
    var allergen by remember { mutableStateOf(existing?.allergen ?: "") }
    var reaction by remember { mutableStateOf(existing?.reaction ?: "") }
    var severity by remember { mutableStateOf(existing?.severity ?: "") }
    var diagnosedDate by remember { mutableStateOf(existing?.diagnosedDate ?: "") }

    val isEditing = existing != null
    val dialogTitle = if (isEditing) "Edit Allergy" else "Add Allergy"
    val saveButtonText = if (isEditing) "Update" else "Add"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column {
                OutlinedTextField(allergen, { allergen = it }, label = { Text("Allergen") })
                OutlinedTextField(reaction, { reaction = it }, label = { Text("Reaction") })
                OutlinedTextField(severity, { severity = it }, label = { Text("Severity") })
                OutlinedTextField(diagnosedDate, { diagnosedDate = it }, label = { Text("Diagnosed Date") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val allergy = if (isEditing) {
                    existing.copy(id = existing.id, allergen = allergen.trim(), reaction = reaction.trim(), severity = severity.trim(), diagnosedDate = diagnosedDate.trim())
                } else {
                    Allergy(allergen = allergen.trim(), reaction = reaction.trim(), severity = severity.trim(), diagnosedDate = diagnosedDate.trim())
                }
                onSave(allergy)
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ConditionDialog(onDismiss: () -> Unit, onSave: (Condition) -> Unit, existing: Condition? = null) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var diagnosedDate by remember { mutableStateOf(existing?.diagnosedDate ?: "") }
    var status by remember { mutableStateOf(existing?.status ?: "") }

    val isEditing = existing != null
    val dialogTitle = if (isEditing) "Edit Condition" else "Add Condition"
    val saveButtonText = if (isEditing) "Update" else "Add"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                OutlinedTextField(diagnosedDate, { diagnosedDate = it }, label = { Text("Diagnosed Date") })
                OutlinedTextField(status, { status = it }, label = { Text("Status") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val condition = if (isEditing) {
                    existing.copy(name = name.trim(), diagnosedDate = diagnosedDate.trim(), status = status.trim())
                } else {
                    Condition(name = name.trim(), diagnosedDate = diagnosedDate.trim(), status = status.trim())
                }
                onSave(condition)
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SurgeryDialog(onDismiss: () -> Unit, onSave: (Surgery) -> Unit, existing: Surgery? = null) {
    var surgeryName by remember { mutableStateOf(existing?.surgeryName ?: "") }
    var surgeryDate by remember { mutableStateOf(existing?.surgeryDate ?: "") }
    var hospital by remember { mutableStateOf(existing?.hospital ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }

    val isEditing = existing != null
    val dialogTitle = if (isEditing) "Edit Surgery" else "Add Surgery"
    val saveButtonText = if (isEditing) "Update" else "Add"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column {
                OutlinedTextField(surgeryName, { surgeryName = it }, label = { Text("Surgery Name") })
                OutlinedTextField(surgeryDate, { surgeryDate = it }, label = { Text("Surgery Date") })
                OutlinedTextField(hospital, { hospital = it }, label = { Text("Hospital") })
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val surgery = if (isEditing) {
                    existing.copy(surgeryName = surgeryName.trim(), surgeryDate = surgeryDate.trim(), hospital = hospital.trim(), notes = notes.trim())
                } else {
                    Surgery(surgeryName = surgeryName.trim(), surgeryDate = surgeryDate.trim(), hospital = hospital.trim(), notes = notes.trim())
                }
                onSave(surgery)
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ImmunizationDialog(onDismiss: () -> Unit, onSave: (Immunization) -> Unit, existing: Immunization? = null) {
    var vaccineName by remember { mutableStateOf(existing?.vaccineName ?: "") }
    var dateAdministered by remember { mutableStateOf(existing?.dateAdministered ?: "") }
    var administeredBy by remember { mutableStateOf(existing?.administeredBy ?: "") }
    var nextDueDate by remember { mutableStateOf(existing?.nextDueDate ?: "") }

    val isEditing = existing != null
    val dialogTitle = if (isEditing) "Edit Immunization" else "Add Immunization"
    val saveButtonText = if (isEditing) "Update" else "Add"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column {
                OutlinedTextField(vaccineName, { vaccineName = it }, label = { Text("Vaccine Name") })
                OutlinedTextField(dateAdministered, { dateAdministered = it }, label = { Text("Date Administered") })
                OutlinedTextField(administeredBy, { administeredBy = it }, label = { Text("Administered By") })
                OutlinedTextField(nextDueDate, { nextDueDate = it }, label = { Text("Next Due Date") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val immunization = if (isEditing) {
                    existing.copy(vaccineName = vaccineName.trim(), dateAdministered = dateAdministered.trim(), administeredBy = administeredBy.trim(), nextDueDate = nextDueDate.trim())
                } else {
                    Immunization(vaccineName = vaccineName.trim(), dateAdministered = dateAdministered.trim(), administeredBy = administeredBy.trim(), nextDueDate = nextDueDate.trim())
                }
                onSave(immunization)
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}