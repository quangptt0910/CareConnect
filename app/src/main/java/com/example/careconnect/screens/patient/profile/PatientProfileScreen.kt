package com.example.careconnect.screens.patient.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.dataclass.Gender
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.ui.theme.CareConnectTheme


/**
 * Screen composable displaying the patient's profile with options to edit profile,
 * view prescriptions, medical reports, and medical history.
 *
 * @param openPrescriptionsScreen Lambda to navigate to the prescriptions screen.
 * @param openMedicalReportsScreen Lambda to navigate to the medical reports screen.
 * @param openMedicalHistoryScreen Lambda to navigate to the medical history screen, taking a type parameter.
 * @param viewModel ViewModel managing patient profile data and operations.
 */
@Composable
fun PatientProfileScreen(
    openPrescriptionsScreen: () -> Unit = {},
    openMedicalReportsScreen: () -> Unit = {},
    openMedicalHistoryScreen: (type: String) -> Unit = {},
    viewModel: PatientProfileViewModel = hiltViewModel()
){
    val patient by viewModel.patient.collectAsState()

    PatientProfileScreenContent(
        openPrescriptionScreen = openPrescriptionsScreen,
        openMedicalReportsScreen = openMedicalReportsScreen,
        openMedicalHistoryScreen = openMedicalHistoryScreen,
        patient = patient,
        onSavePatient = { updatePatient -> viewModel.updatePatient(updatePatient)}
    )
}

/**
 * Composable content for the Patient Profile screen showing patient's data and navigation cards.
 *
 * @param openPrescriptionScreen Lambda to navigate to prescriptions.
 * @param openMedicalReportsScreen Lambda to navigate to medical reports.
 * @param openMedicalHistoryScreen Lambda to navigate to medical history with specified type.
 * @param patient The current patient data to display.
 * @param onSavePatient Callback invoked when patient profile changes are saved.
 */
@Composable
fun PatientProfileScreenContent(
    openPrescriptionScreen: () -> Unit = {},
    openMedicalReportsScreen: () -> Unit = {},
    openMedicalHistoryScreen: (type: String) -> Unit = {},
    patient: Patient? = null,
    onSavePatient: (Patient) -> Unit = {}
){
    val showEditDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){


        Column(
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .width(350.dp).height(50.dp).clickable{ showEditDialog.value = true },

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Edit Profile"
                    )
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowForwardIos,
                        contentDescription = "Go",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .width(350.dp).height(50.dp).clickable{ openPrescriptionScreen() },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContactPage,
                        contentDescription = "View Prescriptions"
                    )
                    Text(
                        text = "View Prescriptions",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowForwardIos,
                        contentDescription = "Go",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .width(350.dp).height(50.dp).clickable{ openMedicalReportsScreen() },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "View Medical Reports"
                    )
                    Text(
                        text = "View Medical Reports",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowForwardIos,
                        contentDescription = "Go",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .width(350.dp).height(50.dp).clickable{ openMedicalHistoryScreen("MEDICATION") },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "View Medical History"
                    )
                    Text(
                        text = "View Medical History",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                        contentDescription = "Go",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                    )
                }
            }
        }

        if (showEditDialog.value && patient != null) {
            EditPatientDialog(
                patient = patient,
                onDismiss = { showEditDialog.value = false },
                onSave = { updatedPatient ->
                    onSavePatient(updatedPatient)
                    showEditDialog.value = false
                }
            )
        }
    }
}

/**
 * Dialog composable to edit patient profile details.
 *
 * Displays input fields for editable patient information and handles confirmation before saving.
 *
 * @param patient The patient data to edit.
 * @param onDismiss Callback invoked when the dialog is dismissed without saving.
 * @param onSave Callback invoked with updated patient data when saving changes.
 */
@Composable
fun EditPatientDialog(
    patient: Patient,
    onDismiss: () -> Unit,
    onSave: (Patient) -> Unit
) {
    val name = remember { mutableStateOf(patient.name) }
    val surname = remember { mutableStateOf(patient.surname) }
    val phone = remember { mutableStateOf(patient.phone) }
    val address = remember { mutableStateOf(patient.address) }
    val dateOfBirth = remember { mutableStateOf(patient.dateOfBirth) }
    val gender = remember { mutableStateOf(patient.gender) }
    val pesel = remember { mutableStateOf(patient.pesel) }
    val height = remember { mutableStateOf(patient.height.toString()) }
    val weight = remember { mutableStateOf(patient.weight.toString()) }

    val showConfirmDialog = remember { mutableStateOf(false) }

    if (showConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog.value = false },
            title = { Text("Confirm Changes") },
            text = { Text("Are you sure you want to save these changes?") },
            confirmButton = {
                Text(
                    "Yes",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onSave(
                                patient.copy(
                                    name = name.value,
                                    surname = surname.value,
                                    phone = phone.value,
                                    address = address.value,
                                    dateOfBirth = dateOfBirth.value,
                                    gender = gender.value,
                                    pesel = pesel.value,
                                    height = height.value.toDoubleOrNull() ?: 0.0,
                                    weight = weight.value.toDoubleOrNull() ?: 0.0,
                                )
                            )
                            showConfirmDialog.value = false
                        }
                )
            },
            dismissButton = {
                Text(
                    "Cancel",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showConfirmDialog.value = false }
                )
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text("Save", modifier = Modifier
                .padding(8.dp)
                .clickable { showConfirmDialog.value = true }
            )
        },
        dismissButton = {
            Text("Cancel", modifier = Modifier
                .padding(8.dp)
                .clickable { onDismiss() }
            )
        },
        title = { Text("Edit Patient Profile") },
        text = {
            Column {
                OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("First Name") })
                OutlinedTextField(value = surname.value, onValueChange = { surname.value = it }, label = { Text("Last Name") })
                OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Phone") })
                OutlinedTextField(value = address.value, onValueChange = { address.value = it }, label = { Text("Address") })
                OutlinedTextField(value = dateOfBirth.value, onValueChange = { dateOfBirth.value = it }, label = { Text("Date of Birth") })
                GenderDropdown(selectedGender = gender.value, onGenderSelected = { gender.value = it })
                OutlinedTextField(value = pesel.value, onValueChange = { pesel.value = it }, label = { Text("PESEL") })
                OutlinedTextField(value = height.value, onValueChange = { height.value = it }, label = { Text("Height (cm)") })
                OutlinedTextField(value = weight.value, onValueChange = { weight.value = it }, label = { Text("Weight (kg)") })
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Dropdown menu for selecting a patient's gender.
 *
 * @param selectedGender The currently selected gender.
 * @param onGenderSelected Callback when a gender is selected from the dropdown.
 */
@Composable
fun GenderDropdown(selectedGender: Gender, onGenderSelected: (Gender) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedGender.name,
            onValueChange = {},
            label = { Text("Gender") },
            readOnly = true,
            modifier = Modifier
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Gender.values().forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender.name) },
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}


/**
 * Preview composable to visualize PatientProfileScreenContent in Android Studio.
 */
@Composable
@Preview
fun PatientProfilePreview(){
    CareConnectTheme {
        PatientProfileScreenContent()
    }
}