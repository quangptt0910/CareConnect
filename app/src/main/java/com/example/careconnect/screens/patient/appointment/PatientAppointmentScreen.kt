package com.example.careconnect.screens.patient.appointment

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.common.ActionTextButton
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Screen composable that displays the patient's appointments.
 *
 * This screen allows users to view their appointments filtered by time range (e.g., all or monthly),
 * navigate through months (if monthly view is selected), and apply filters by appointment status.
 * Users can also sort the appointments and reset applied filters and sorting.
 *
 * It observes the [PatientAppointmentViewModel] for UI state updates and triggers events
 * such as range selection, date changes, filtering, sorting, and reset actions.
 *
 * @param viewModel The [PatientAppointmentViewModel] instance used to manage state and business logic.
 */
@Composable
fun PatientAppointmentScreen(
    viewModel: PatientAppointmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    PatientAppointmentScreenContent(
        uiState = uiState,
        onRangeChange = viewModel::setRange,
        onDateChange = viewModel::setDate,
        onFilterChange = viewModel::setFilter,
        onSortChange = viewModel::setSort,
        onReset = viewModel::resetAll
    )
}

/**
 * Content composable for the [PatientAppointmentScreen].
 *
 * Displays the UI elements including:
 * - Header with patient info
 * - Time range selection chips (All / Month)
 * - Month navigation controls (if in monthly range mode)
 * - Filtering and sorting controls with dropdown menus and filter chips
 * - List of appointment cards or loading/error/no-data states
 *
 * @param uiState The current UI state representing appointments and UI selections.
 * @param onRangeChange Callback invoked when the user changes the time range filter.
 * @param onDateChange Callback invoked when the user changes the current date (month).
 * @param onFilterChange Callback invoked when the appointment status filters are modified.
 * @param onSortChange Callback invoked when the sort option changes.
 * @param onReset Callback invoked when the user resets all filters and sorting.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PatientAppointmentScreenContent(
    uiState: PatientAppointmentUiState,
    onRangeChange: (PatientTimeRange) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onFilterChange: (Set<AppointmentStatus?>) -> Unit,
    onSortChange: (PatientSortOption) -> Unit,
    onReset: () -> Unit
) {
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Patient info header
        uiState.patient?.let { patient ->
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
            PatientTimeRange.entries.forEach { range ->
                FilterChip(
                    selected = range == uiState.selectedRange,
                    onClick = { onRangeChange(range) },
                    label = { Text(range.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Month navigation row - only shown for Month view
        if (uiState.selectedRange == PatientTimeRange.Month) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Left arrow
                IconButton(onClick = {
                    onDateChange(uiState.currentDate.minusMonths(1))
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Month display
                Text(
                    text = uiState.currentDate.format(monthFormatter),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                // Right arrow
                IconButton(onClick = {
                    onDateChange(uiState.currentDate.plusMonths(1))
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
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
                        PatientSortOption.entries.forEach { option ->
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
                    PatientAppointmentCard(appt = appt)
                }
            }
        }
    }
}

/**
 * Displays a card representing an individual appointment.
 *
 * Shows appointment details such as:
 * - Appointment date
 * - Time range (start and end time)
 * - Appointment status (with color coding)
 * - Doctor's name
 * - Appointment type
 * - Location address
 *
 * @param appt The [Appointment] data class instance containing appointment details.
 */
@Composable
fun PatientAppointmentCard(appt: Appointment) {
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
            Text(text = "Doctor: ${appt.doctorName}")
            Text(text = "Type: ${appt.type}")
            Text(text = "Address: ${appt.address}")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PatientAppointmentScreenPreview() {
    PatientAppointmentScreenContent(
        uiState = PatientAppointmentUiState(),
        onRangeChange = {},
        onDateChange = {},
        onFilterChange = {},
        onSortChange = {},
        onReset = {}
    )

}