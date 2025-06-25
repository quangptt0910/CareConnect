package com.example.careconnect.screens.doctor.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.common.LoadingIndicator
import com.example.careconnect.dataclass.SlotType
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.TimeSlot
import com.example.careconnect.screens.admin.doctormanage.MultiDatePicker
import java.time.LocalDate


/**
 * Composable that displays the doctor's schedule screen.
 *
 * It shows the available time slots for the selected date and allows adding,
 * editing, and deleting time slots.
 *
 * @param showSnackBar Lambda function to display snack bar messages.
 * @param viewModel The [ScheduleViewModel] instance injected via Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    showSnackBar: (SnackBarMessage) -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()

    ScheduleScreenContent(
        uiState = uiState,
        onDateSelected = { date -> viewModel.selectDate(date, showSnackBar) },
        onAddClick = { viewModel.showSlotDialog(null) },
        onEdit = { viewModel.showSlotDialog(it) },
        onDelete = { slot -> viewModel.removeSlot(slot, showSnackBar) },
        onDialogSave = { slot -> viewModel.addOrUpdateSlot(slot, showSnackBar) },
        onDialogDismiss = { viewModel.closeDialog() },
    )
}

/**
 * Content composable for the schedule screen.
 *
 * Displays a multi-date picker, a list of time slots for the selected date,
 * and a floating action button to add new time slots.
 * Also manages the display of the slot editing dialog.
 *
 * @param uiState The current UI state holding selected date, slots, loading status, and dialog state.
 * @param onDateSelected Called when the user selects a different date.
 * @param onAddClick Called when the add button is clicked to add a new slot.
 * @param onEdit Called when an existing slot is edited.
 * @param onDelete Called when an existing slot is deleted.
 * @param onDialogSave Called when a slot is saved via the dialog.
 * @param onDialogDismiss Called when the slot editing dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenContent(
    uiState: ScheduleUiState = ScheduleUiState(),
    onDateSelected: (LocalDate) -> Unit,
    onAddClick: () -> Unit,
    onEdit: (TimeSlot) -> Unit,
    onDelete: (TimeSlot) -> Unit,
    onDialogSave: (TimeSlot) -> Unit,
    onDialogDismiss: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { /* AppBar here */ },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Slot")
                }
            }
        ) { padding ->
            Column(Modifier.padding(padding)) {
                MultiDatePicker(
                    selectedDates = setOf(uiState.selectedDate),
                    onDateSelected = onDateSelected
                )

                Spacer(Modifier.height(16.dp))

                // Show loading indicator if data is loading
                if (uiState.isLoading) {
                    LoadingIndicator()
                }

                // Slots list with key for better recomposition
                if (uiState.slots.isEmpty() && !uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No time slots available for this date",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = uiState.slots,
                            key = { "${it.startTime}-${it.endTime}-${it.slotType}" }
                        ) { slot ->
                            SlotRow(
                                slot = slot,
                                onEdit = { onEdit(slot) },
                                onDelete = { onDelete(slot) }
                            )
                        }
                    }
                }
            }
        }


        if (uiState.dialogState.isOpen) {
            SlotEditDialog(
                initialSlot = uiState.dialogState.editingSlot,
                onDismiss = onDialogDismiss,
                onSave = onDialogSave
            )
        }
    }
}

/**
 * Displays a single row representing a time slot with edit and delete actions.
 *
 * @param slot The [TimeSlot] data to display.
 * @param onEdit Lambda to invoke when the edit button is clicked.
 * @param onDelete Lambda to invoke when the delete button is clicked.
 */
@Composable
fun SlotRow(slot: TimeSlot, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "${slot.startTime} – ${slot.endTime}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "${slot.appointmentMinutes}min • ${slot.slotType}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row {
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
        }
    }
}

/**
 * Dialog composable for adding or editing a time slot.
 *
 * Contains input fields for start time, end time, appointment length, and slot type,
 * with validation and dropdown selection for slot type.
 *
 * @param initialSlot The slot to edit or null to add a new one.
 * @param onDismiss Called when the dialog is dismissed without saving.
 * @param onSave Called with the new or updated [TimeSlot] when the user confirms.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotEditDialog(
    initialSlot: TimeSlot?,
    onDismiss: () -> Unit,
    onSave: (TimeSlot) -> Unit
) {
    var start by remember { mutableStateOf(initialSlot?.startTime ?: "") }
    var end by remember { mutableStateOf(initialSlot?.endTime ?: "") }
    var length by remember {
        mutableStateOf(
            initialSlot?.appointmentMinutes?.toString() ?: "15"
        )
    }
    var type by remember { mutableStateOf(initialSlot?.slotType ?: SlotType.CONSULT) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialSlot == null) "Add Time Block" else "Edit Time Block") },
        text = {
            Column {
                TimeTextField(label = "Start Time", value = start) { start = it }
                TimeTextField(label = "End Time", value = end) { end = it }

                OutlinedTextField(
                    value = length,
                    onValueChange = { length = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Appointment Length (min)") }
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        readOnly = true,
                        value = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        label = { Text("Slot Type") },
                        trailingIcon = {
                            IconButton(onClick = { dropdownExpanded = !dropdownExpanded }) {
                                Icon(
                                    imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Select slot type"
                                )
                            }
                                       },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        SlotType.entries.forEach { st ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        st.name.lowercase().replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    type = st
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Show currently selected type for clarity
                Text(
                    text = "Selected: ${type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                onSave(TimeSlot(start, end, length.toInt(), type))
            },
                enabled = start.isNotEmpty() && end.isNotEmpty()
            ) { Text("Generate Slots") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}


/**
 * Custom outlined text field for entering time values.
 *
 * Validates input to ensure it conforms to "HH:mm" format and only allows valid hours and minutes.
 *
 * @param label Label text shown above the text field.
 * @param value The current text value.
 * @param onValueChange Lambda called when the text changes.
 */
@Composable
fun TimeTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        label = { Text(label) },
        value = value,
        onValueChange = { newInput ->
            //
            // If the user typed just a single digit
            if (newInput.length == 1 && newInput[0].isDigit()) {
                // Don't modify single digits - let the user continue typing
                onValueChange(newInput)
                return@OutlinedTextField
            }

            if (!newInput.contains(":") && newInput.length == 2 && value.length < 2) {
                newInput.toIntOrNull()?.let { hours ->
                    if (hours in 0..23) {
                        onValueChange("$hours:")
                        return@OutlinedTextField
                    }
                }
            }

            // Remove any non-digit chac except colon
            val cleanInput = newInput.filter { it.isDigit() || it == ':' }

            // Split by colon to handle hours and minute separately
            val parts = cleanInput.split(":")

            when (// If no colon, -> potential hours
                parts.size) {
                1 -> {
                    val hours = parts[0].take(2)
                    if (hours.isNotEmpty()) {
                        val hoursInt = hours.toIntOrNull() ?: 0
                        if (hoursInt in 0..23) {
                            onValueChange(hours)
                        }
                    } else {
                        onValueChange("")
                    }
                }

                2 -> {
                    val hours = parts[0].take(2)
                    val minutes = parts[1].take(2)
                    if (hours.isEmpty()) {
                        onValueChange("0:$minutes")
                        return@OutlinedTextField
                    }
                    val hoursInt = hours.toIntOrNull() ?: 0
                    val minutesInt = minutes.toIntOrNull() ?: 0
                    if (hoursInt in 0..23 && minutesInt in 0..59) {
                        val time = "$hours:${minutes.take(2)}"
                        onValueChange(time)
                    }
                }
                else -> return@OutlinedTextField
            }
        },
        placeholder = { Text("HH:mm") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun ScheduleScreenPreview(){
    val uiState = ScheduleUiState(
        selectedDate = LocalDate.now(),
        slots = listOf(
            TimeSlot("09:00", "12:00", appointmentMinutes = 20),
            TimeSlot("14:00", "18:00", appointmentMinutes = 30)
        ),
        dialogState = DialogState(isOpen = false, editingSlot = null)
    )
    ScheduleScreenContent(
        uiState = uiState,
        onDateSelected = {},
        onAddClick = {},
        onEdit = {},
        onDelete = {},
        onDialogSave = {},
        onDialogDismiss = {},
    )
}