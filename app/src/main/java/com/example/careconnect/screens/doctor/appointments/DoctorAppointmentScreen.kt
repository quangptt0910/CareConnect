package com.example.careconnect.screens.doctor.appointments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.common.ActionTextButton
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


/**
 * Composable function for the Doctor Appointment screen.
 * This screen displays a list of appointments for the doctor, allowing them to filter, sort, and navigate through dates.
 *
 * @param viewModel The [DoctorAppointmentViewModel] used to manage the state and logic of this screen.
 */
@Composable
fun DoctorAppointmentScreen(
    viewModel: DoctorAppointmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    DoctorAppointmentScreenContent(
        uiState = uiState,
        onRangeChange = viewModel::setRange,
        onDateChange = viewModel::setDate,
        onFilterChange = viewModel::setFilter,
        onSortChange = viewModel::setSort,
        onReset = viewModel::resetAll
    )
}


/**
 * Composable function for the content of the Doctor Appointment screen.
 *
 * @param uiState The current state of the UI, containing appointment data, selected filters, etc.
 * @param onRangeChange Callback invoked when the time range (Day, Week, Month, All) is changed.
 * @param onDateChange Callback invoked when the current date is changed.
 * @param onFilterChange Callback invoked when the appointment status filters are changed.
 * @param onSortChange Callback invoked when the sorting option for appointments is changed.
 * @param onReset Callback invoked when the reset button is clicked to clear all filters and selections.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DoctorAppointmentScreenContent(
    uiState: DoctorAppointmentUiState,
    onRangeChange: (TimeRange) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onFilterChange: (Set<AppointmentStatus?>) -> Unit,
    onSortChange: (DoctorSortOption) -> Unit,
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
        // Patient info header
        uiState.doctor?.let { doctor ->
            Text(
                text = "My Appointments",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Chips for All/Month
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
                    TimeRange.All -> uiState.currentDate
                }
                onDateChange(newDate)
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Date show
            Text(
                text = when (uiState.selectedRange) {
                    TimeRange.Day -> uiState.currentDate.format(dateFormatter)
                    TimeRange.Week -> "${weekStart.format(dateFormatter)} - ${
                        weekEnd.format(
                            dateFormatter
                        )
                    }"
                    TimeRange.Month -> uiState.currentDate.format(monthFormatter)
                    TimeRange.All -> "All"
                },
                modifier = Modifier.padding(horizontal = 16.dp).clickable { showDatePicker = true },
                color = MaterialTheme.colorScheme.primary
            )

            // Right arrow
            IconButton(onClick = {
                val newDate = when (uiState.selectedRange) {
                    TimeRange.Day -> uiState.currentDate.plusDays(1)
                    TimeRange.Week -> uiState.currentDate.plusWeeks(1)
                    TimeRange.Month -> uiState.currentDate.plusMonths(1)
                    TimeRange.All -> uiState.currentDate
                }
                onDateChange(newDate)
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.primary
                )
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
                                    TimeRange.Day -> picked
                                    TimeRange.Week -> picked.with(weekFields.dayOfWeek(), 1)
                                    TimeRange.Month -> picked.withDayOfMonth(1)
                                    TimeRange.All -> picked
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

        HorizontalDivider()

        // Filter, Sort, Reset row
        var showFilterMenu by remember { mutableStateOf(false) }
        var showSortMenu by remember { mutableStateOf(false) }
        var selectedFilters by remember { mutableStateOf<Set<AppointmentStatus>>(emptySet()) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter button
                Box {
                    ActionTextButton(
                        text = if (selectedFilters.isEmpty()) "Filter" else "Filters (${selectedFilters.size})",
                        icon = Icons.Default.FilterList,
                        onClick = { showFilterMenu = true }
                    )

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = {
                                selectedFilters = emptySet()
                                onFilterChange(emptySet())
                                showFilterMenu = false
                            },
                            trailingIcon = {
                                Checkbox(
                                    checked = selectedFilters.isEmpty(),
                                    onCheckedChange = null
                                )
                            }
                        )
                        AppointmentStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.title) },
                                onClick = {
                                    selectedFilters = if (selectedFilters.contains(status)) {
                                        selectedFilters - status
                                    } else {
                                        selectedFilters + status
                                    }
                                    onFilterChange(selectedFilters)
                                },
                                trailingIcon = {
                                    Checkbox(
                                        checked = selectedFilters.contains(status),
                                        onCheckedChange = null
                                    )
                                }
                            )
                        }
                    }
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(24.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )

                // Sort button
                Box {
                    ActionTextButton(
                        text = "Sort",
                        icon = Icons.Default.SwapVert,
                        onClick = { showSortMenu = true },
                    )
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DoctorSortOption.entries.forEach { option ->
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
                TextButton(onClick = {
                    selectedFilters = emptySet()
                    onReset()
                }) {
                    Text("Reset")
                }
            }

            // Filter chips
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedFilters.forEach { status ->
                    FilterChip(
                        selected = true,
                        onClick = {},
                        modifier = Modifier.height(36.dp).padding(4.dp),
                        label = { Text(status.title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp)) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    selectedFilters = selectedFilters - status
                                    onFilterChange(selectedFilters)
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove filter",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
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
        } else if (uiState.appointments.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No appointments found", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.appointments) { appt ->
                    DoctorAppointmentCard(appt = appt)
                }
            }
        }
    }
}

@Composable
fun DoctorAppointmentCard(appt: Appointment) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${appt.appointmentDate}",
                )
                Text(
                    text = "${appt.startTime} - ${appt.endTime}",
                )
                Text(
                    text = appt.status.title,
                    color = appt.status.color,
                )
            }
            Spacer(Modifier.height(4.dp))

            Spacer(Modifier.height(8.dp))
            Text(text = "Patient: ${appt.patientName}")
            Text(text = "Type: ${appt.type}")
            Text(text = "Address: ${appt.address}")
        }
    }
}


@Composable
@Preview
fun DoctorAppointmentScreenPreview() {
    DoctorAppointmentScreenContent(
        uiState = DoctorAppointmentUiState(),
        onRangeChange = TODO(),
        onDateChange = TODO(),
        onFilterChange = TODO(),
        onSortChange = TODO(),
        onReset = TODO(),
    )
}