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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun AddDoctorScreen(
    onAddScheduleScreen: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: AddDoctorViewModel = hiltViewModel()
){
    val navigateToAddSchedule by viewModel.navigateToAddSchedule.collectAsStateWithLifecycle()
    if (navigateToAddSchedule) {
        onAddScheduleScreen()
    } else {
        AddDoctorScreenContent(
            createDoctorInfo = viewModel::createDoctorInfo,
            showErrorSnackbar = showErrorSnackbar
        )
    }
}


@Composable
fun AddDoctorScreenContent(
    createDoctorInfo: (String, String, String, String, String, String, String, String, (ErrorMessage) -> Unit) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit = {}
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
        SmallTopAppBarExample()

        Column(modifier = Modifier.padding(top = 80.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

            }
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Progress Stepper
                StepperIndicator(currentStep = 1)

                Spacer(modifier = Modifier.height(24.dp))

                // Form for Personal Info
                Text(
                    "Step 1: Personal Information",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                CustomTextField(label = stringResource(R.string.specialization), value = specialization, onValueChange = { specialization = it })
                CustomTextField(label = stringResource(R.string.experience), value = experience, onValueChange = { experience = it })
                CustomTextField(label = stringResource(R.string.address), value = address, onValueChange = { address = it })
                CustomTextField(label = stringResource(R.string.phone), value = phone, onValueChange = { phone = it })
                CustomTextField(label = stringResource(R.string.password), value = password, onValueChange = { password = it })

                Spacer(modifier = Modifier.height(24.dp))

                // Next Button
                IconButton(
                    onClick = { createDoctorInfo(name, surname, email, phone, address, specialization, experience, password, showErrorSnackbar) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Step")
                }
            }
        }
    }
}

// Progress Stepper UI
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Add Doctor",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ){
        Box(modifier = Modifier.padding(it))
    }
}


@Preview
@Composable
fun AddDoctorScreenPreview() {
    CareConnectTheme {
        AddDoctorScreenContent(
            createDoctorInfo = { _, _, _, _, _, _, _, _, _->},
            showErrorSnackbar = {}
        )
    }
}