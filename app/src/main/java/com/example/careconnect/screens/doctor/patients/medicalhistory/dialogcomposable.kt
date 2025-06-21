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
    onAdd: (Medication) -> Unit,
    existing: Medication? = null
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var dosage by remember { mutableStateOf(existing?.dosage ?: "") }
    var frequency by remember { mutableStateOf(existing?.frequency ?: "") }
    var startDate by remember { mutableStateOf(existing?.startDate ?: "") }
    var endDate by remember { mutableStateOf(existing?.endDate ?: "") }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Medication" else "Edit Medication") },
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
                onAdd(Medication("MEDICATION" ,name, dosage, startDate, endDate, frequency))
            }) {
                Text(if (existing == null) "Add" else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AllergyDialog(onDismiss: () -> Unit, onAdd: (Allergy) -> Unit, existing: Allergy? = null) {
    var allergen by remember { mutableStateOf(existing?.allergen ?: "") }
    var reaction by remember { mutableStateOf(existing?.reaction ?: "") }
    var severity by remember { mutableStateOf(existing?.severity ?: "") }
    var diagnosedDate by remember { mutableStateOf(existing?.diagnosedDate ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Allergy" else "Edit Allergy") },
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
                onAdd(Allergy("ALLERGY", allergen, reaction, severity, diagnosedDate))
            }) {
                Text(if (existing == null) "Add" else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ConditionDialog(onDismiss: () -> Unit, onAdd: (Condition) -> Unit, existing: Condition? = null) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var diagnosedDate by remember { mutableStateOf(existing?.diagnosedDate ?: "") }
    var status by remember { mutableStateOf(existing?.status ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Condition" else "Edit Condition") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                OutlinedTextField(diagnosedDate, { diagnosedDate = it }, label = { Text("Diagnosed Date") })
                OutlinedTextField(status, { status = it }, label = { Text("Status") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onAdd(Condition("CONDITION", name, diagnosedDate, status))
            }) {
                Text(if (existing == null) "Add" else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SurgeryDialog(onDismiss: () -> Unit, onAdd: (Surgery) -> Unit, existing: Surgery? = null) {
    var surgeryName by remember { mutableStateOf(existing?.surgeryName ?: "") }
    var surgeryDate by remember { mutableStateOf(existing?.surgeryDate ?: "") }
    var hospital by remember { mutableStateOf(existing?.hospital ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Surgery" else "Edit Surgery") },
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
                onAdd(Surgery("SURGERY", surgeryName, surgeryDate, hospital, notes))
            }) {
                Text(if (existing == null) "Add" else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ImmunizationDialog(onDismiss: () -> Unit, onAdd: (Immunization) -> Unit, existing: Immunization? = null) {
    var vaccineName by remember { mutableStateOf(existing?.vaccineName ?: "") }
    var dateAdministered by remember { mutableStateOf(existing?.dateAdministered ?: "") }
    var administeredBy by remember { mutableStateOf(existing?.administeredBy ?: "") }
    var nextDueDate by remember { mutableStateOf(existing?.nextDueDate ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Immunization" else "Edit Immunization") },
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
                onAdd(Immunization("IMMUNIZATION", vaccineName, dateAdministered, administeredBy, nextDueDate))
            }) {
                Text(if (existing == null) "Add" else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}