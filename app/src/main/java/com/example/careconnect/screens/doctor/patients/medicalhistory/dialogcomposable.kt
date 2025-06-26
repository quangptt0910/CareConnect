package com.example.careconnect.screens.doctor.patients.medicalhistory

import android.app.Activity
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Surgery
import java.util.Calendar

enum class DosageOption(val label: String) {
    ONCE_DAILY("Once Daily"),
    TWICE_DAILY("Twice Daily"),
    THREE_TIMES_DAILY("Three Times Daily"),
    AS_NEEDED("As Needed");

    override fun toString() = label
}

enum class FrequencyOption(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    AS_NEEDED("As Needed");

    override fun toString() = label
}
@Composable
fun DatePickerField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit,
    activityContext: android.app.Activity
) {
    val calendar = remember { Calendar.getInstance() }
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        DisposableEffect(Unit) {
            val datePicker = DatePickerDialog(
                activityContext,
                { _, year, month, day ->
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                    onDateSelected(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.setOnDismissListener { showDialog.value = false }
            datePicker.show()

            onDispose {
                datePicker.setOnDismissListener(null)
            }
        }
    }

    OutlinedTextField(
        value = date,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog.value = true },
        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Select date") }
    )
}






/**
 * A composable function that displays a dialog for adding or editing a medication.
 *
 * @param onDismiss Callback function to be invoked when the dialog is dismissed.
 * @param onSave Callback function to be invoked when the save button is clicked, passing the [Medication] object.
 * @param existing An optional [Medication] object. If provided, the dialog will be in "edit" mode, pre-filling the fields with the existing medication's data. Otherwise, it will be in "add" mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDialog(
    onDismiss: () -> Unit,
    onSave: (Medication) -> Unit,
    existing: Medication? = null
) {
    val context = LocalContext.current
    val activity = context as? Activity
        ?: throw IllegalStateException("Context is not an Activity")

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var dosage by remember { mutableStateOf(existing?.dosage ?: "") }
    var frequency by remember { mutableStateOf(existing?.frequency ?: "") }
    var startDate by remember { mutableStateOf(existing?.startDate ?: "") }
    var endDate by remember { mutableStateOf(existing?.endDate ?: "") }

    val isEditing = existing != null
    val dialogTitle = if (isEditing) "Edit Medication" else "Add Medication"
    val saveButtonText = if (isEditing) "Update" else "Add"

    var expandedDosage by remember { mutableStateOf(false) }
    var expandedFrequency by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(dialogTitle, style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dosage Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedDosage,
                    onExpandedChange = { expandedDosage = !expandedDosage }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = dosage,
                        onValueChange = {},
                        label = { Text("Dosage") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDosage)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDosage,
                        onDismissRequest = { expandedDosage = false }
                    ) {
                        DosageOption.entries.forEach {
                            DropdownMenuItem(
                                text = { Text(it.label) },
                                onClick = {
                                    dosage = it.label
                                    expandedDosage = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Frequency Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedFrequency,
                    onExpandedChange = { expandedFrequency = !expandedFrequency }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = frequency,
                        onValueChange = {},
                        label = { Text("Frequency") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrequency)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFrequency,
                        onDismissRequest = { expandedFrequency = false }
                    ) {
                        FrequencyOption.entries.forEach {
                            DropdownMenuItem(
                                text = { Text(it.label) },
                                onClick = {
                                    frequency = it.label
                                    expandedFrequency = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Start Date with Activity context passed
                OutlinedTextField(startDate, { startDate = it }, label = { Text("Start Date") })
                OutlinedTextField(endDate, { endDate = it }, label = { Text("End Date") })

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val medication = if (isEditing) {
                            existing?.copy(
                                id = existing.id,
                                name = name.trim(),
                                dosage = dosage.trim(),
                                frequency = frequency.trim(),
                                startDate = startDate.trim(),
                                endDate = endDate.trim()
                            )
                        } else {
                            Medication(
                                name = name.trim(),
                                dosage = dosage.trim(),
                                frequency = frequency.trim(),
                                startDate = startDate.trim(),
                                endDate = endDate.trim()
                            )
                        }
                        medication?.let(onSave)
                    }) {
                        Text(saveButtonText)
                    }
                }
            }
        }
    }
}




/**
 * A composable function that displays a dialog for adding or editing an allergy.
 *
 * @param onDismiss Callback function to be invoked when the dialog is dismissed.
 * @param onSave Callback function to be invoked when the save button is clicked, passing the [Allergy] object.
 * @param existing An optional [Allergy] object. If provided, the dialog will be in "edit" mode, pre-filling the fields with the existing allergy's data. Otherwise, it will be in "add" mode.
 */
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
                    existing?.copy(id = existing.id, allergen = allergen.trim(), reaction = reaction.trim(), severity = severity.trim(), diagnosedDate = diagnosedDate.trim())
                } else {
                    Allergy(allergen = allergen.trim(), reaction = reaction.trim(), severity = severity.trim(), diagnosedDate = diagnosedDate.trim())
                }
                if (allergy != null) {
                    onSave(allergy)
                }
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}


/**
 * A composable function that displays a dialog for adding or editing a medical condition.
 *
 * @param onDismiss Callback function to be invoked when the dialog is dismissed.
 * @param onSave Callback function to be invoked when the save button is clicked, passing the [Condition] object.
 * @param existing An optional [Condition] object. If provided, the dialog will be in "edit" mode, pre-filling the fields with the existing condition's data. Otherwise, it will be in "add" mode.
 */
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
                    existing?.copy(name = name.trim(), diagnosedDate = diagnosedDate.trim(), status = status.trim())
                } else {
                    Condition(name = name.trim(), diagnosedDate = diagnosedDate.trim(), status = status.trim())
                }
                if (condition != null) {
                    onSave(condition)
                }
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}


/**
 * A composable function that displays a dialog for adding or editing a surgery.
 *
 * @param onDismiss Callback function to be invoked when the dialog is dismissed.
 * @param onSave Callback function to be invoked when the save button is clicked, passing the [Surgery] object.
 * @param existing An optional [Surgery] object. If provided, the dialog will be in "edit" mode, pre-filling the fields with the existing surgery's data. Otherwise, it will be in "add" mode.
 */
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
                    existing?.copy(surgeryName = surgeryName.trim(), surgeryDate = surgeryDate.trim(), hospital = hospital.trim(), notes = notes.trim())
                } else {
                    Surgery(surgeryName = surgeryName.trim(), surgeryDate = surgeryDate.trim(), hospital = hospital.trim(), notes = notes.trim())
                }
                if (surgery != null) {
                    onSave(surgery)
                }
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}


/**
 * A composable function that displays a dialog for adding or editing an immunization record.
 *
 * @param onDismiss Callback function to be invoked when the dialog is dismissed.
 * @param onSave Callback function to be invoked when the save button is clicked, passing the [Immunization] object.
 * @param existing An optional [Immunization] object. If provided, the dialog will be in "edit" mode, pre-filling the fields with the existing immunization's data. Otherwise, it will be in "add" mode.
 */
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
                    existing?.copy(vaccineName = vaccineName.trim(), dateAdministered = dateAdministered.trim(), administeredBy = administeredBy.trim(), nextDueDate = nextDueDate.trim())
                } else {
                    Immunization(vaccineName = vaccineName.trim(), dateAdministered = dateAdministered.trim(), administeredBy = administeredBy.trim(), nextDueDate = nextDueDate.trim())
                }
                if (immunization != null) {
                    onSave(immunization)
                }
            }) {
                Text(saveButtonText)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}