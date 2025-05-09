package com.example.careconnect.screens.patient.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.ui.theme.CareConnectTheme
import java.time.LocalDate


@Composable
fun BookAppointmentScreen(
    doctorId: String,
    viewModel: BookAppointmentViewModel = hiltViewModel(),
    showSnackBar: (SnackBarMessage) -> Unit
){

    LaunchedEffect(doctorId) {
        viewModel.setDoctorId(doctorId)
    }

    val doctor by viewModel.doctor.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BookAppointmentScreenContent(
        doctor = doctor,
        uiState = uiState,
        onDateSelected = { date ->
            viewModel.onDateSelected(date, showSnackBar)
        },
        onTimeSelected = { time ->
            viewModel.onTimeSelected(time)
        },
        onBookAppointment = {
            viewModel.bookAppointment(showSnackBar)
        },
    )
}


@Composable
fun BookAppointmentScreenContent(
    doctor: Doctor? = Doctor(),
    uiState: BookAppointmentUiState,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (String) -> Unit,
    onBookAppointment: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        SmallTopAppBarExample()

        Column(
            modifier = Modifier.padding(top = 80.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            doctor?.let {
                Text(
                    text = "Dr. ${it.name} ${it.surname}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 20.dp)
                )

                Text(
                    text = it.specialization,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 20.dp, bottom = 16.dp)
                )

                InlineDatePicker {  }

                val times = listOf("09:00 AM", "10:30 AM", "12:00 PM", "02:00 PM", "03:30 PM","04:00 PM", "05:30 PM", "07:00 PM", "08:30 PM")
                TimeSelectionChips(availableTimes = times) { selectedTime ->
                    println("User selected: $selectedTime")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {}) {
                    Text("Book an Appointment",
                        style = MaterialTheme.typography.titleLarge)
                }
            }

        }



    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(
                        "Request Appointment",
                        style = MaterialTheme.typography.titleLarge
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineDatePicker(onDateSelected: (Long?) -> Unit) {
    val datePickerState = rememberDatePickerState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 500.dp, height = 500.dp) // Adjust size
                .scale(0.9f) // Scale down the DatePicker
        ) {
            DatePicker(state = datePickerState)
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TimeSelectionChips(availableTimes: List<String>, onTimeSelected: (String) -> Unit) {
    var selectedTime by remember { mutableStateOf<String?>(null) }

    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4 // Limit to 4 chips per row
    ) {
        availableTimes.forEach { time ->
            FilterChip(
                selected = time == selectedTime,
                onClick = {
                    selectedTime = time
                    onTimeSelected(time)
                },
                label = { Text(time, style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.width(80.dp)
            )
        }
    }
}


@Preview
@Composable
fun BookAppointmentScreenPreview() {
    CareConnectTheme {
        val uiState = BookAppointmentUiState()
        BookAppointmentScreenContent(
            doctor = Doctor(
                name = "John",
                surname = "Doe",
                address = "123 Main St",
                specialization = "Family Medicine"
            ),
            uiState = uiState,
            onDateSelected = {},
            onTimeSelected = {},
            onBookAppointment = {}
        )
    }
}