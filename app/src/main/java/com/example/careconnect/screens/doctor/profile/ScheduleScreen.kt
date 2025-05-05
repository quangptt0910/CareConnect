package com.example.careconnect.screens.doctor.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.careconnect.dataclass.SlotType
import com.example.careconnect.dataclass.TimeSlot
import com.example.careconnect.screens.admin.doctormanage.MultiDatePicker
import java.time.LocalDate

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
){
    ScheduleScreenContent(
        selectedDate = viewModel.selectedDate.collectAsState().value,
        slots = viewModel.slots.collectAsState().value,
        dialogState = viewModel.dialogState,
        onDateSelected = viewModel::selectDate,
        onAddClick = { viewModel.showSlotDialog(null) },
        onEdit = { viewModel.showSlotDialog(it) },
        onDelete = { viewModel.removeSlot(viewModel.selectedDate.value, it) },
        onDialogSave = { viewModel.addOrUpdateSlot(viewModel.selectedDate.value, it) },
        onDialogDismiss = { viewModel.closeDialog() }
    )
}

@Composable
fun ScheduleScreenContent(
    selectedDate: LocalDate,
    slots: List<TimeSlot>,
    dialogState: DialogState,
    onDateSelected: (LocalDate) -> Unit,
    onAddClick: () -> Unit,
    onEdit: (TimeSlot) -> Unit,
    onDelete: (TimeSlot) -> Unit,
    onDialogSave: (TimeSlot) -> Unit,
    onDialogDismiss: () -> Unit
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
                    selectedDates = setOf(selectedDate),
                    onDateSelected = onDateSelected
                )
                Spacer(Modifier.height(16.dp))
                LazyColumn {
                    items(slots) { slot ->
                        SlotRow(
                            slot = slot,
                            onEdit = { onEdit(slot) },
                            onDelete = { onDelete(slot) }
                        )
                    }
                }
            }
        }

        if (dialogState.isOpen) {
            SlotEditDialog(
                initialSlot = dialogState.editingSlot,
                onDismiss = onDialogDismiss,
                onSave = {
                    onDialogSave(it)
                    onDialogDismiss()
                }
            )
        }
    }
}

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
            title = { Text(if (initialSlot == null) "Add Slot" else "Edit Slot") },
            text = {
                Column {
                    TimeTextField(label = "Start Time", value = start) { start = it }
                    TimeTextField(label = "End Time", value = end) { end = it }
                    OutlinedTextField(
                        value = length,
                        onValueChange = { length = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Appointment Length (min)") }
                    )
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = type.name,
                            onValueChange = {},
                            label = { Text("Slot Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            SlotType.values().forEach { st ->
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
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSave(TimeSlot(start, end, length.toInt(), type))
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }

@Composable
fun TimeTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        label = { Text(label) },
        value = value,
        onValueChange = { newVal ->
            // allow HH or HH:mm
            if (newVal.isEmpty() || newVal.matches(Regex("^([01]?\\d|2[0-3])(:[0-5]?\\d)?$"))) {
                onValueChange(newVal)
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
    ScheduleScreenContent(
        selectedDate = LocalDate.now(),
        slots = listOf(
            TimeSlot("09:00", "12:00", appointmentMinutes = 20),
            TimeSlot("14:00", "18:00", appointmentMinutes = 30)
        ),
        dialogState = DialogState(isOpen = false, editingSlot = null),
        onDateSelected = {},
        onAddClick = {},
        onEdit = {},
        onDelete = {},
        onDialogSave = {},
        onDialogDismiss = {}
    )
}