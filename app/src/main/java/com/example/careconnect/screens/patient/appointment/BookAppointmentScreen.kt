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
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.common.LoadingIndicator
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.DoctorSchedule
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.TimeSlot
import com.example.careconnect.ui.theme.CareConnectTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


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
        onTimeSelected = { timeSlot ->
            viewModel.onTimeSelected(timeSlot)
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
    onTimeSelected: (TimeSlot) -> Unit,
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

                InlineDatePicker { selectedDate ->
                    onDateSelected(Instant.ofEpochMilli(selectedDate)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                }

                // Time slots
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Text(
                        text = "Available Time Slots",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    when {
                        uiState.isLoading -> LoadingIndicator()
                        uiState.availableSlots.isEmpty() -> NoSlotsMessage()
                        else -> TimeSelectionSection(
                            slots = uiState.availableSlots,
                            selectedTimeSlot = uiState.selectedTimeSlot,
                            onTimeSelected = onTimeSelected
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onBookAppointment) {
                    Text("Book an Appointment",
                        style = MaterialTheme.typography.titleLarge)
                }
            }

        }



    }
}

@Composable
private fun TimeSelectionSection(
    slots: List<TimeSlot>,
    selectedTimeSlot: TimeSlot?,
    onTimeSelected: (TimeSlot) -> Unit
) {
    TimeSelectionChips(
        availableTimeSlots = slots,
        onTimeSelected = onTimeSelected,
        selectedTimeSlot = selectedTimeSlot
    )
}

@Composable
private fun NoSlotsMessage() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No available time slots for this date",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
fun InlineDatePicker(onDateSelected: (Long) -> Unit) {
    val datePickerState = rememberDatePickerState()
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { selectedDate ->
            onDateSelected(selectedDate)
        }
    }
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
fun TimeSelectionChips(
    availableTimeSlots: List<TimeSlot>,
    selectedTimeSlot: TimeSlot?,
    onTimeSelected: (TimeSlot) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4 // Limit to 4 chips per row
    ) {
        availableTimeSlots.forEach { slot ->
            val timeRange = "${slot.startTime} - ${slot.endTime}"
            val isSelected = selectedTimeSlot == slot
            val isAvailable = slot.isAvailable

            FilterChip(
                selected = isSelected,
                onClick = { if (isAvailable) onTimeSelected(slot) },
                label = {
                    Text(
                        text = timeRange,
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            !isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                        },
                enabled = isAvailable,
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = when {
                        !isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
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
                specialization = "Family Medicine",
                schedule = DoctorSchedule(

                )
            ),
            uiState = uiState,
            onDateSelected = {},
            onTimeSelected = {},
            onBookAppointment = {}
        )
    }
}