package com.example.careconnect.screens.patient.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.SelectableDates
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
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.TimeSlot
import com.example.careconnect.ui.theme.CareConnectTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId


/**
 * Main Composable to display the Book Appointment screen.
 *
 * @param doctorId The unique identifier of the doctor for whom the appointment is being booked.
 * @param viewModel The ViewModel managing appointment data and UI state.
 * @param showSnackBar A lambda callback to display snack bar messages.
 * @param goBack A lambda callback to navigate back from the screen.
 */
@Composable
fun BookAppointmentScreen(
    doctorId: String,
    viewModel: BookAppointmentViewModel = hiltViewModel(),
    showSnackBar: (SnackBarMessage) -> Unit,
    goBack: () -> Unit = {}
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
        goBack = goBack
    )
}

/**
 * The UI content of the Book Appointment screen, displaying doctor info,
 * date picker, available time slots, and booking button.
 *
 * @param doctor The [Doctor] object whose appointment is being booked.
 * @param uiState The current UI state of the booking screen.
 * @param onDateSelected Callback when a new date is selected.
 * @param onTimeSelected Callback when a time slot is selected.
 * @param onBookAppointment Callback to trigger booking action.
 * @param goBack Callback to navigate back.
 */
@Composable
fun BookAppointmentScreenContent(
    doctor: Doctor? = Doctor(),
    uiState: BookAppointmentUiState,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (TimeSlot) -> Unit,
    onBookAppointment: () -> Unit,
    goBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            BookAppointmentTopBar(
                goBack = goBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            doctor?.let {
                Text(
                    text = "Dr. ${it.name} ${it.surname}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 20.dp, top = 16.dp)
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
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)) {
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
                            selectedDate = uiState.selectedDate,
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

/**
 * Displays a section with selectable time slots as chips.
 *
 * @param slots List of available [TimeSlot]s.
 * @param selectedTimeSlot Currently selected time slot.
 * @param selectedDate The selected date.
 * @param onTimeSelected Callback triggered when a time slot is selected.
 */
@Composable
private fun TimeSelectionSection(
    slots: List<TimeSlot>,
    selectedTimeSlot: TimeSlot?,
    selectedDate: LocalDate,
    onTimeSelected: (TimeSlot) -> Unit
) {
    TimeSelectionChips(
        availableTimeSlots = slots,
        onTimeSelected = onTimeSelected,
        selectedDate = selectedDate,
        selectedTimeSlot = selectedTimeSlot,
    )
}

/**
 * Shows a message when there are no available time slots.
 */
@Composable
private fun NoSlotsMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No available time slots for this date",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Top app bar for the Book Appointment screen, includes a back button and title.
 *
 * @param goBack Callback triggered when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentTopBar(
    goBack: () -> Unit
) {

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
}

/**
 * Inline date picker Composable to select appointment dates.
 * Restricts selection to today and future dates, within 2 years.
 *
 * @param onDateSelected Callback returning the selected date in milliseconds.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineDatePicker(onDateSelected: (Long) -> Unit) {
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            return !selectedDate.isBefore(LocalDate.now())
        }

        override fun isSelectableYear(year: Int): Boolean {
            val currentYear = LocalDate.now().year
            return year >= currentYear && year <= currentYear + 2
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        yearRange = IntRange(LocalDate.now().year, LocalDate.now().year + 2),
        selectableDates = selectableDates
    )

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

/**
 * Displays a list of selectable chips representing available time slots.
 *
 * Disabled if the slot is in the past or unavailable.
 *
 * @param availableTimeSlots List of time slots.
 * @param selectedTimeSlot Currently selected time slot.
 * @param selectedDate The date for which slots are shown.
 * @param onTimeSelected Callback when a time slot is selected.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TimeSelectionChips(
    availableTimeSlots: List<TimeSlot>,
    selectedTimeSlot: TimeSlot?,
    selectedDate: LocalDate?,
    onTimeSelected: (TimeSlot) -> Unit
) {
    val currentTime = LocalTime.now()
    val currentDate = LocalDate.now()

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4 // Limit to 4 chips per row
    ) {
        availableTimeSlots.forEach { slot ->
            val timeRange = "${slot.startTime} - ${slot.endTime}"
            val isSelected = selectedTimeSlot == slot
            val isPastTime = selectedDate == currentDate && LocalTime.parse(slot.startTime) < currentTime

            val isAvailable = slot.available && !isPastTime

            Box(
                modifier = Modifier
                    .width(80.dp)
            ) {
                FilterChip(
                    selected = isSelected,
                    onClick = { if (isAvailable) onTimeSelected(slot) },
                    enabled = isAvailable,
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
                    colors = FilterChipDefaults.filterChipColors(
                        labelColor = when {
                            !isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}

/**
 * Preview of the Book Appointment screen content for design and testing purposes.
 */
@Preview
@Composable
fun BookAppointmentScreenPreview() {
    CareConnectTheme {
        val uiState = BookAppointmentUiState(
            availableSlots = listOf(
                TimeSlot(
                    startTime = "10:00",
                    endTime = "11:00",
                    available = true,
                ),
                TimeSlot(
                    startTime = "11:00",
                    endTime = "12:00",
                    available = true,
                    ),
                TimeSlot(
                    startTime = "12:00",
                    endTime = "13:00",
                    available = false,
                )
            )
        )
        BookAppointmentScreenContent(
            doctor = Doctor(
                name = "John",
                surname = "Doe",
                address = "123 Main St",
                specialization = "Family Medicine",
            ),
            uiState = uiState,
            onDateSelected = {},
            onTimeSelected = {},
            onBookAppointment = {}
        )
    }
}