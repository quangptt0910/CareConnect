package com.example.careconnect.screens.admin.appointment

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


@Composable
fun AppointmentManageScreen(
    viewModel: AppointmentManageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    AppointmentManageScreenContent(
        uiState = uiState,
        onRangeChange = viewModel::setRange,
        onDateChange = viewModel::setDate,
        onFilterChange = viewModel::setFilter,
        onSortChange = viewModel::setSort,
        onReset = viewModel::resetAll
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentManageScreenContent(
    uiState: AppointmentUiState,
    onRangeChange: (TimeRange) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onFilterChange: (AppointmentStatus?) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onReset: () -> Unit
) {

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.currentDate
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val weekFields = WeekFields.of(Locale.getDefault())
    val weekStart = uiState.currentDate.with(weekFields.dayOfWeek(), 1)
    val weekEnd = weekStart.plusDays(6)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Chips for Day/Week/Month
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeRange.entries.forEach { range ->
                FilterChip(
                    selected = range == uiState.selectedRange,
                    onClick = { onRangeChange(range) },
                    label = { Text(range.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Arrow navigation row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Left arrow
            IconButton(onClick = {
                val newDate = when (uiState.selectedRange) {
                    TimeRange.Day -> uiState.currentDate.minusDays(1)
                    TimeRange.Week -> uiState.currentDate.minusWeeks(1)
                    TimeRange.Month -> uiState.currentDate.minusMonths(1)
                }
                onDateChange(newDate)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }

            // Date show
            Text(
                text = when (uiState.selectedRange) {
                    TimeRange.Day -> uiState.currentDate.format(dateFormatter)
                    TimeRange.Week -> "${weekStart.format(dateFormatter)} - ${weekEnd.format(dateFormatter)}"
                    TimeRange.Month -> uiState.currentDate.format(monthFormatter)
                },
                modifier = Modifier.padding(horizontal = 16.dp).clickable { showDatePicker = true }

            )

            // Right arrow
            IconButton(onClick = {
                val newDate = when (uiState.selectedRange) {
                    TimeRange.Day   -> uiState.currentDate.plusDays(1)
                    TimeRange.Week  -> uiState.currentDate.plusWeeks(1)
                    TimeRange.Month -> uiState.currentDate.plusMonths(1)
                }
                onDateChange(newDate)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
            }
        }

        // DatePickerDialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val picked = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateChange(
                                when (uiState.selectedRange) {
                                    TimeRange.Day   -> picked
                                    TimeRange.Week  -> picked.with(weekFields.dayOfWeek(), 1)
                                    TimeRange.Month -> picked.withDayOfMonth(1)
                                }
                            )
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                modifier = Modifier,
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                },
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                colors = DatePickerDefaults.colors(),
                properties = DialogProperties(),
                content = {
                    DatePicker(
                        state = datePickerState,
                        title = { Text("Select Date") }
                    )
                }
            )
        }




        Spacer(modifier = Modifier.height(16.dp))

        // Filter, Sort, Reset row
        var showFilterMenu by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter button

            Box {
                Button(onClick = { showFilterMenu = true }) { Text("Filter") }
                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                                onFilterChange(null)
                                showFilterMenu = false
                            }
                        )
                    AppointmentStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.title) },
                            onClick = {
                                onFilterChange(status)
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Sort button
            var showSortMenu by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { showSortMenu = true }) { Text("Sort") }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                onSortChange(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Reset button on far-right
            TextButton(onClick = onReset) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Appointment cards list
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.appointments) { appt ->
                    AppointmentCard(appt = appt)
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(appt: Appointment) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${appt.startTime} - ${appt.endTime}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = appt.status.title,
                    color = appt.status.color,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(text = "Patient: ${appt.patientName}")
            Text(text = "Doctor: ${appt.doctorName}")
            Text(text = "Type: ${appt.type}")
            Text(text = "Address: ${appt.address}")
        }
    }
}



// Sample Preview Data
private val sampleAppointments = listOf(
    Appointment(
        id = "1",
        patientName = "Alice Johnson",
        doctorName = "Dr. Smith",
        type = "Consultation",
        appointmentDate = "2025-04-30",
        startTime = "09:00",
        endTime = "09:30",
        address = "123 Main St",
        status = AppointmentStatus.PENDING
    ),
    Appointment(
        id = "2",
        patientName = "Bob Lee",
        doctorName = "Dr. Wong",
        type = "Checkup",
        appointmentDate = "2025-04-30",
        startTime = "10:00",
        endTime = "10:30",
        address = "456 Elm St",
        status = AppointmentStatus.COMPLETED
    )
)

@Preview(showBackground = true)
@Composable
fun AppointmentManageScreenPreview() {
    AppointmentManageScreenContent(
        uiState = AppointmentUiState(appointments = sampleAppointments),
        onRangeChange = {},
        onDateChange = {},
        onFilterChange = {},
        onSortChange = {},
        onReset = {}
    )
}
