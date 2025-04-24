package com.example.careconnect.screens.admin.doctormanage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme
import java.time.LocalDate


@Composable
fun AddDoctorScheduleScreen(
    doctorId: String,
    onNextStep: () -> Unit,
    onBack: () -> Unit,
    viewModel: AdminDoctorScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateNext) {
        if (uiState.navigateNext) {
            onNextStep()
        }
    }

    AddDoctorScheduleScreenContent(
        uiState = DoctorScheduleUiState(),
        onDateSelected = viewModel::toggleDate,
        onNextStep = viewModel::onSaveAndNext,
        onBack = onBack
    )
}

@Composable
fun AddDoctorScheduleScreenContent(
    uiState: DoctorScheduleUiState,
    onDateSelected: (LocalDate) -> Unit,
    onNextStep: () -> Unit,
    onBack: () -> Unit,
) {
    var selectedDays by remember { mutableStateOf(setOf<LocalDate>()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AdminTopAppBar(
            label = stringResource(R.string.add_doctor) + " " + stringResource(R.string.schedule),
            onBack = onBack,
        )

        Column(modifier = Modifier.fillMaxSize().padding(top = 100.dp, start = 16.dp, end = 16.dp)) {
            // Progress Stepper
            StepperIndicator(currentStep = 2)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Step 2: Select Work Days for the Doctor",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(25.dp))

            MultiDatePicker(
                selectedDates = selectedDays,
                onDateSelected = onDateSelected,
                modifier = Modifier
                    .fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Step")
                }

                IconButton(
                    onClick = { onNextStep() },
                    enabled = !uiState.isSaving && !uiState.isLoading,
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator()
                    }
                    Text(text = "Next")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Step")
                }
            }
        }
    }
}


@Preview
@Composable
fun AdminDoctorScheduleScreenPreview() {
    CareConnectTheme {
        AddDoctorScheduleScreenContent(uiState = DoctorScheduleUiState(), onDateSelected = {}, onNextStep = {}, onBack = {})
    }
}
