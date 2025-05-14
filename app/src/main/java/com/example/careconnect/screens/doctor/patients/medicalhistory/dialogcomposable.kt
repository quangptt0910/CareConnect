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
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication") },
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
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AllergyDialog(onDismiss: () -> Unit, onAdd: (Allergy) -> Unit) {
    var allergen by remember { mutableStateOf("") }
    var reaction by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("") }
    var diagnosedDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Allergy") },
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
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ConditionDialog(onDismiss: () -> Unit, onAdd: (Condition) -> Unit) {
    var name by remember { mutableStateOf("") }
    var diagnosedDate by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Condition") },
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
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SurgeryDialog(onDismiss: () -> Unit, onAdd: (Surgery) -> Unit) {
    var surgeryName by remember { mutableStateOf("") }
    var surgeryDate by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Surgery") },
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
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ImmunizationDialog(onDismiss: () -> Unit, onAdd: (Immunization) -> Unit) {
    var vaccineName by remember { mutableStateOf("") }
    var dateAdministered by remember { mutableStateOf("") }
    var administeredBy by remember { mutableStateOf("") }
    var nextDueDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Immunization") },
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
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}