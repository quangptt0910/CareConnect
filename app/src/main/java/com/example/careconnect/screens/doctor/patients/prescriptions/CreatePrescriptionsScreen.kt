package com.example.careconnect.screens.doctor.patients.prescriptions

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Prescription
import com.example.careconnect.screens.doctor.patients.TextFieldDoctor
import com.example.careconnect.ui.theme.CareConnectTheme
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun CreatePrescriptionsScreen(
    patientId: String,
    viewModel: CreatePrescriptionsViewModel = hiltViewModel()
){
    LaunchedEffect(patientId) {
        viewModel.loadPatient(patientId)
    }

    val patient by viewModel.patient.collectAsStateWithLifecycle()

    CreatePrescriptionsScreenContent(
        patientId = patientId,
        patient = patient,
        onCreatePrescription = viewModel::createPrescription
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePrescriptionsScreenContent(
    patientId: String,
    patient: Patient? = null,
    onCreatePrescription: (String, Prescription) -> Unit
) {
    val context = LocalContext.current
    val datePicker = rememberDatePickerState()
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var medicationName by remember { mutableStateOf("") }
    var dosageExpanded by remember { mutableStateOf(false) }
    var selectedDosage by remember { mutableStateOf("") }
    var refills by remember { mutableStateOf(0) }
    var instructions by remember { mutableStateOf("") }
    var validUntil by remember { mutableStateOf<Timestamp?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val dosageOptions = listOf(
        "1 tablet once a day",
        "1 tablet twice a day",
        "1 tablet 3 times a day",
        "2 tablets once a day"
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text("Create Prescription", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

                patient?.let {
                    Text("${it.name} ${it.surname}", style = MaterialTheme.typography.bodyMedium)
                    Text("Date of Birth: ${it.dateOfBirth}", style = MaterialTheme.typography.bodyMedium)
                    Text("Gender: ${it.gender}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                TextFieldDoctor(
                    value = medicationName,
                    onValueChange = { medicationName = it },
                    label = "Medication Name"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dosage Selector
                ExposedDropdownMenuBox(
                    expanded = dosageExpanded,
                    onExpandedChange = { dosageExpanded = !dosageExpanded }
                ) {
                    TextField(
                        value = selectedDosage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Dosage") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dosageExpanded) },
                        modifier = Modifier.menuAnchor().width(300.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = dosageExpanded,
                        onDismissRequest = { dosageExpanded = false }
                    ) {
                        dosageOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedDosage = option
                                    dosageExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Refills Number Picker with Up/Down Arrows
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = refills.toString(),
                        onValueChange = {},
                        label = { Text("Refills Allowed") },
                        readOnly = true,
                        trailingIcon = {
                            Column {
                                IconButton(onClick = { refills += 1 }) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                                }
                                IconButton(onClick = {
                                    if (refills > 0) refills -= 1
                                }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                                }
                            }
                        },
                        modifier = Modifier.width(300.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextFieldDoctor(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = "Additional Instructions"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Valid Until Date Picker
                TextField(
                    value = validUntil?.let { dateFormatter.format(it.toDate()) } ?: "",
                    onValueChange = {},
                    label = { Text("Valid Until") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            val datePickerDialog = DatePickerDialog(context)
                            datePickerDialog.setOnDateSetListener { _, year, month, day ->
                                val cal = Calendar.getInstance()
                                cal.set(year, month, day)
                                validUntil = Timestamp(cal.time)
                            }
                            datePickerDialog.show()
                        }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                        }
                    },
                    modifier = Modifier.width(300.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.width(200.dp).height(100.dp).padding(16.dp)
                ) {
                    Text("Submit Prescription")
                }

                if (showDialog) {
                    AlertDialogExample(
                        onDismissRequest = { showDialog = false },
                        onConfirmation = {
                            showDialog = false
                            val prescription = Prescription(
                                patientId = patientId,
                                medicationName = medicationName,
                                dosage = selectedDosage,
                                refills = refills,
                                instructions = instructions,
                                issueDate = Timestamp.now(),
                                validUntil = validUntil
                            )
                            onCreatePrescription(patientId, prescription)
                        },
                        dialogTitle = "Submit Prescription",
                        dialogText = "Are you sure you want to submit this prescription?",
                        icon = Icons.Default.Check
                    )
                }
            }
        }
    }
}


@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}


@Preview
@Composable
fun CreatePrescriptionsScreenPreview(){
    CareConnectTheme {
        CreatePrescriptionsScreenContent(
            patientId = "123",
            onCreatePrescription = { _, _ -> }

        )
    }
}