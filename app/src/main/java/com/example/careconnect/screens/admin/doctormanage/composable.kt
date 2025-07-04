package com.example.careconnect.screens.admin.doctormanage

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.careconnect.dataclass.Doctor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.careconnect.R.string as AppText

/**
 * A top app bar for the admin screens with a title and a back navigation icon.
 *
 * @param label The title text to be displayed in the app bar.
 * @param onBack Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopAppBar(
    label: String,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
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

/**
 * Displays a card representing a doctor with their details and action buttons for editing or deleting.
 *
 * Shows a confirmation dialog when the delete button is pressed.
 *
 * @param modifier Modifier to be applied to the card.
 * @param doctor The [Doctor] data object to display.
 * @param onOpenProfile Callback triggered when the edit icon is clicked.
 * @param onDeleteDoc Callback triggered when the doctor deletion is confirmed.
 */
@Composable
fun DoctorCard(
    modifier: Modifier = Modifier,
    doctor: Doctor,
    onChangeSchedule: () -> Unit,
    onOpenProfile: () -> Unit,
    onDeleteDoc: (Doctor) -> Unit,
) {
    //var expanded by rememberSaveable { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var doctorToDelete by remember { mutableStateOf<Doctor?>(null) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                    ListItem(
                        headlineContent = { Text(doctor.name + " " + doctor.surname) },
                        supportingContent = {
                            Column {
                                Text(doctor.specialization) }
                        },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ){
                                IconButton(onClick = onChangeSchedule ){
                                    Icon(imageVector = Icons.Default.Schedule, contentDescription = "Change Schedule")
                                }
                                IconButton(onClick = onOpenProfile ){
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                                }

                                Spacer(modifier = Modifier.width(2.dp))

                                IconButton(
                                    onClick = {
                                        doctorToDelete = doctor
                                        showWarningDialog = true
                                    }
                                ){
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }

                        }
                    )
            }
        }

        if (showWarningDialog && doctorToDelete != null) {
            AlertDialog(
                title = { Text(stringResource(AppText.delete_doctor_title) )},
                text = { Text(stringResource(AppText.delete_doctor_description)) },
                dismissButton = { DialogCancelButton(AppText.cancel) { showWarningDialog = false } },
                confirmButton = {
                    DialogConfirmButton(AppText.delete) {
                        onDeleteDoc(doctorToDelete!!)
                        showWarningDialog = false
                    }
                },
                onDismissRequest = { showWarningDialog = false }
            )
        }
    }
}
@Composable
fun ChangeScheduleDialog(
    doctor: Doctor,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    selectedDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Work Schedule") },
        text = {
            Column {
                Text(
                    text = "Select working days:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                MultiDatePicker(
                    selectedDates = selectedDates,
                    onDateSelected = onDateSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Text(
                text = "Save",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onSave() }
            )
        },
        dismissButton = {
            Text(
                text = "Cancel",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDismiss() }
            )
        },
        shape = RoundedCornerShape(12.dp)
    )
}



/**
 * Confirmation button used in dialogs, styled with primary theme colors.
 *
 * @param text String resource ID for the button text.
 * @param action Callback triggered when button is clicked.
 */
@Composable
fun DialogConfirmButton(@StringRes text: Int, action: () -> Unit) {
    Button(
        onClick = action,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
    ) {
        Text(text = stringResource(text))
    }
}


/**
 * Cancellation button used in dialogs, styled with primary theme colors.
 *
 * @param text String resource ID for the button text.
 * @param action Callback triggered when button is clicked.
 */
@Composable
fun DialogCancelButton(@StringRes text: Int, action: () -> Unit) {
    Button(
        onClick = action,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
    ) {
        Text(text = stringResource(text))
    }
}

//fun calculateTotalHoursWorked(schedule: DoctorSchedule): Double {
//    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a") // Format: "09:00 AM"
//
//    var totalMinutesWorked = 0L
//
//    schedule.availability.forEach { daySchedule ->
//        daySchedule.availableSlots.forEach { slot ->
//            if (slot.isAvailable) {
//                val start = LocalTime.parse(slot.startTime, timeFormatter)
//                val end = LocalTime.parse(slot.endTime, timeFormatter)
//                val duration = Duration.between(start, end).toMinutes()
//                totalMinutesWorked += duration
//            }
//        }
//    }
//
//    return totalMinutesWorked / 60.0  // Convert minutes to hours
//}

/**
 * Multi-date picker calendar allowing selection of multiple workdays.
 *
 * Displays a monthly calendar grid with navigation arrows to switch months.
 * Selected dates are highlighted.
 *
 * @param selectedDates Set of currently selected [LocalDate]s.
 * @param onDateSelected Callback triggered when a date is selected or deselected.
 * @param modifier Modifier to be applied to the calendar card.
 */
@Composable
fun MultiDatePicker(
    selectedDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }

    val firstDayOfWeek = remember { DayOfWeek.MONDAY }
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
                }

                Text(
                    text = currentMonth.format(monthFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day of week headers
            Row(modifier = Modifier.fillMaxWidth()) {
                var day = firstDayOfWeek
                repeat(7) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    day = if (day == DayOfWeek.SUNDAY) DayOfWeek.MONDAY else day.plus(1)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            val firstDayOfMonth = currentMonth.atDay(1)

            val daysFromStart = (firstDayOfMonth.dayOfWeek.value - firstDayOfWeek.value+ 7) %7
            val firstCalendarDate = firstDayOfMonth.minusDays(daysFromStart.toLong())

            Column {
                var currentDate = firstCalendarDate

                // Generate 6 rows (maximum required for a month)
                repeat(6) { weekIndex ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        repeat(7) { dayIndex ->
                            val date = currentDate
                            val isCurrentMonth = date.month == currentMonth.month

                            DayCell(
                                date = date,
                                isSelected = selectedDates.contains(date),
                                isCurrentMonth = isCurrentMonth,
                                onDateSelected = onDateSelected
                            )

                            currentDate = date.plusDays(1)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected dates summary
            if (selectedDates.isNotEmpty()) {
                Text(
                    text = "Selected: ${selectedDates.size} day(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * A single day cell within the [MultiDatePicker] calendar grid.
 *
 * Highlights the cell if the date is selected, today, or outside the current month.
 *
 * @param date The date represented by this cell.
 * @param isSelected Whether this date is currently selected.
 * @param isCurrentMonth Whether this date belongs to the currently displayed month.
 * @param onDateSelected Callback invoked when this date cell is clicked.
 */
@Composable
private fun RowScope.DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = remember { LocalDate.now() }
    val isToday = date == today

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceContainerLow
                }
            )
            .clickable(enabled = isCurrentMonth) { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}