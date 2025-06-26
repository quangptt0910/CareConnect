package com.example.careconnect.screens.admin.doctormanage

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.Specialization
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Screen to add a new doctor by inputting personal and professional details.
 *
 * Handles navigation to the doctor schedule screen after successful creation.
 *
 * @param openDoctorScheduleScreen Lambda invoked with the created doctor's ID to navigate to the schedule setup.
 * @param showSnackBar Lambda to show feedback messages as snack bars.
 * @param viewModel ViewModel responsible for managing doctor creation state.
 */
@Composable
fun AddDoctorScreen(
    openDoctorScheduleScreen: (doctorId: String) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    viewModel: AddDoctorViewModel = hiltViewModel()
){
    val doctorId by viewModel.newDoctorId.collectAsStateWithLifecycle()

    LaunchedEffect(doctorId) {
        doctorId?.let {
            openDoctorScheduleScreen(it)
        }
    }
    AddDoctorScreenContent(
        createDoctorInfo = viewModel::createDoctorInfo,
        showSnackBar = showSnackBar
    )
}


/**
 * UI content for adding doctor information.
 *
 * Presents a form with fields for doctor's name, surname, email, specialization, experience, address,
 * phone number, and password. Contains a stepper indicator for progress.
 *
 * @param createDoctorInfo Callback invoked with doctor info and a snack bar callback when user submits.
 * @param showSnackBar Callback to display snack bar messages, default empty.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDoctorScreenContent(
    createDoctorInfo: (String, String, String, String, String, String, String, String, (SnackBarMessage) -> Unit) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit = {}
) {

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        AdminTopAppBar(
            label = stringResource(R.string.add_doctor),
            onBack = {}
        )

        Column(modifier = Modifier.padding(top = 80.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

            }
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                // Progress Stepper
                StepperIndicator(currentStep = 1)

                Spacer(modifier = Modifier.height(24.dp))

                // Form for Personal Info
                Text(
                    "Step 1: Personal Information",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                var specializationExpanded by remember { mutableStateOf(false) }
                val specializationOptions = Specialization.all()
                var selectedSpecialization by remember { mutableStateOf<Specialization?>(null) }


                /**
                 * TextFields for doctor information
                 * Validation in viewModel
                 * TODO(): Create drop down menu for specialization - instead of typing
                 * TODO(): Generate a random password that is strong
                 * TODO(): Send the password to the email (of the doctor)
                 */
                CustomTextField(label = stringResource(R.string.name), value = name, onValueChange = { name = it })
                CustomTextField(label = stringResource(R.string.surname), value = surname, onValueChange = { surname = it })
                CustomTextField(label = stringResource(R.string.email), value = email, onValueChange = { email = it })
                ExposedDropdownMenuBox(
                    expanded = specializationExpanded,
                    onExpandedChange = { specializationExpanded = !specializationExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSpecialization?.displayName() ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Specialization") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = specializationExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = specializationExpanded,
                        onDismissRequest = { specializationExpanded = false }
                    ) {
                        specializationOptions.forEach { spec ->
                            DropdownMenuItem(
                                text = { Text(spec.displayName()) },
                                onClick = {
                                    selectedSpecialization = spec
                                    specializationExpanded = false
                                }
                            )
                        }
                    }
                }
                CustomTextField(
                    label = stringResource(R.string.experience),
                    value = experience,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) experience = it },
                    keyboardType = KeyboardType.Number
                )
                CustomTextField(label = stringResource(R.string.address), value = address, onValueChange = { address = it })
                CustomTextField(
                    label = stringResource(R.string.phone),
                    value = phone,
                    onValueChange = {
                        if (it.length <= 9 && it.all { ch -> ch.isDigit() }) phone = it
                    },
                    keyboardType = KeyboardType.Phone
                )
                CustomTextField(label = stringResource(R.string.password), value = password, onValueChange = { password = it })

                Spacer(modifier = Modifier.height(24.dp))

                val isFormValid = name.isNotBlank()
                        && surname.isNotBlank()
                        && email.isNotBlank()
                        && selectedSpecialization != null
                        && experience.isNotBlank()
                        && address.isNotBlank()
                        && phone.length == 9
                        && password.isNotBlank()

                // Next Button
                IconButton(
                    onClick = {
                        selectedSpecialization?.let {
                            createDoctorInfo(
                                name,
                                surname,
                                email,
                                phone,
                                address,
                                it.name, // Enum name
                                experience,
                                password,
                                showSnackBar
                            )
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = isFormValid // ✅ disables button when form is incomplete
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Step")
                }
            }
        }
    }
}

// Progress Stepper UI
/**
 * Displays a horizontal stepper indicator showing progress in doctor creation.
 *
 * @param currentStep Current step number (1-based index).
 */
@Composable
fun StepperIndicator(currentStep: Int) {
    val steps = listOf("Info", "Schedule")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        steps.forEachIndexed { index, step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (index + 1 <= currentStep) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (index + 1 < currentStep) {
                                Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White)
                            } else {
                                Text(text = "${index + 1}", color = Color.White, fontSize = 18.sp)
                            }
                        }

                    }
                }
                Text(text = step, fontSize = 14.sp)
            }
        }
    }
}

// Custom TextField
/**
 * A custom styled outlined text field used in doctor form inputs.
 *
 * @param label Label text describing the field.
 * @param value Current value of the text field.
 * @param onValueChange Callback triggered when the user changes the input text.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit,keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType)
    )
}


@Preview
@Composable
fun AddDoctorScreenPreview() {
    CareConnectTheme {
        AddDoctorScreenContent(
            createDoctorInfo = { _, _, _, _, _, _, _, _, _->},
            showSnackBar = {}
        )
    }
}