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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


enum class TimeRange { Day, Week, Month }
enum class SortOption(val label: String) { TimeAsc("Time: Earliest"), DoctorName("Doctor Name"), Status("Status") }

data class FilterState(
    val status: AppointmentStatus? = null,
    val doctor: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentManageScreenContent(
    appointments: List<Appointment>,
    doctors: List<String>,
    onFilterChange: (FilterState) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onReset: () -> Unit
) {
    // State
    var selectedRange by remember { mutableStateOf(TimeRange.Day) }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var filterState by remember { mutableStateOf(FilterState()) }
    var sortOption by remember { mutableStateOf(SortOption.TimeAsc) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy")
    val weekFields = WeekFields.of(Locale.getDefault())
    val weekStart: LocalDate = currentDate.with(weekFields.dayOfWeek(), 1)
    val weekEnd: LocalDate = currentDate.with(weekFields.dayOfWeek(), 7)

    // DatePickerState
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // Chips for Day/Week/Month
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeRange.entries.forEach { range ->
                FilterChip(
                    selected = range == selectedRange,
                    onClick = { selectedRange = range },
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
            IconButton(onClick = {
                currentDate = when (selectedRange) {
                    TimeRange.Day -> currentDate.minusDays(1)
                    TimeRange.Week -> currentDate.minusWeeks(1)
                    TimeRange.Month -> currentDate.minusMonths(1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }

            Text(
                text = when (selectedRange) {
                    TimeRange.Day -> currentDate.format(dateFormatter)
                    TimeRange.Week -> "${weekStart.format(dateFormatter)} - ${weekEnd.format(dateFormatter)}"
                    TimeRange.Month -> currentDate.format(DateTimeFormatter.ofPattern("MM/yyyy"))
                },
                modifier = Modifier.clickable { showDatePicker = true }

            )

            IconButton(onClick = {
                currentDate = when (selectedRange) {
                    TimeRange.Day -> currentDate.plusDays(1)
                    TimeRange.Week -> currentDate.plusWeeks(1)
                    TimeRange.Month -> currentDate.plusMonths(1)
                }
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
                            currentDate = when (selectedRange) {
                                TimeRange.Day -> picked
                                TimeRange.Week -> picked
                                TimeRange.Month -> picked.withDayOfMonth(1)
                            }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter button
            Box {
                Button(onClick = { showFilterMenu = true }) { Text("Filter") }
                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    Text("Status:", modifier = Modifier.padding(8.dp))
                    listOf("All") + AppointmentStatus.entries.map { it.title }.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                filterState = filterState.copy(
                                    status = if (status == "All") null else AppointmentStatus.entries.first { it.title == status }
                                )
                                onFilterChange(filterState)
                                showFilterMenu = false
                            }
                        )
                    }
                    Divider()
                    Text("Doctor:", modifier = Modifier.padding(8.dp))
                    doctors.forEach { doc ->
                        DropdownMenuItem(
                            text = { Text(doc) },
                            onClick = {
                                filterState = filterState.copy(doctor = doc)
                                onFilterChange(filterState)
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Sort button
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
                                sortOption = option
                                onSortChange(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Reset button on far-right
            TextButton(onClick = {
                filterState = FilterState()
                sortOption = SortOption.TimeAsc
                onReset()
            }) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Appointment cards list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(appointments) { appt ->
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
        startTime = "09:00 AM",
        endTime = "09:30 AM",
        address = "123 Main St",
        status = AppointmentStatus.PENDING
    ),
    Appointment(
        id = "2",
        patientName = "Bob Lee",
        doctorName = "Dr. Wong",
        type = "Checkup",
        appointmentDate = "2025-04-30",
        startTime = "10:00 AM",
        endTime = "10:30 AM",
        address = "456 Elm St",
        status = AppointmentStatus.COMPLETED
    )
)

private val sampleDoctors = listOf("Dr. Smith", "Dr. Wong", "Dr. Patel")

@Preview(showBackground = true)
@Composable
fun AppointmentManageScreenPreview() {
    AppointmentManageScreenContent(
        appointments = sampleAppointments,
        doctors = sampleDoctors,
        onFilterChange = {},
        onSortChange = {},
        onReset = {}
    )
}
