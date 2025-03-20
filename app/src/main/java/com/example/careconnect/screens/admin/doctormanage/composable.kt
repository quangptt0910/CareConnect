package com.example.careconnect.screens.admin.doctormanage

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.DoctorSchedule
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.careconnect.R.string as AppText

/**
 * Composable function to display a filled card with a title, expandable list of user products, and delete functionality.
 *
 * @param title The title of the card.
 * @param userProducts List of user-added products to be displayed.
 * @param onDeleteProduct Callback function to handle product deletion.
 */
@Composable
fun FilledCardExample(
    title: String,
    modifier: Modifier = Modifier,
    userProducts: List<Doctor>,
    onDeleteProduct: (Doctor) -> Unit,
) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Doctor?>(null) }

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
                userProducts.forEach { doctor ->
                    ListItem(
                        headlineContent = { Text(doctor.name + " " + doctor.surname) },
                        supportingContent = {
                            Column {
                                Text(doctor.specialization)

                            } },
                        trailingContent = {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ){
                                IconButton(
                                    onClick = {

                                    }
                                ){
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                }

                                Spacer(modifier = Modifier.width(2.dp))

                                IconButton(
                                    onClick = {
                                        productToDelete = doctor
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

        if (expanded) {

            }
        }

        if (showWarningDialog && productToDelete != null) {
            AlertDialog(
                title = { Text(stringResource(AppText.delete_product_title)) },
                text = { Text(stringResource(AppText.delete_product_description)) },
                dismissButton = { DialogCancelButton(AppText.cancel) { showWarningDialog = false } },
                confirmButton = {
                    DialogConfirmButton(AppText.delete) {
                        onDeleteProduct(productToDelete!!)
                        showWarningDialog = false
                    }
                },
                onDismissRequest = { showWarningDialog = false }
            )
        }
    }
}


@Composable
fun FilledCardStats(
    title: String,
    modifier: Modifier = Modifier,
    userProducts: List<Doctor>,
    onDeleteProduct: (Doctor) -> Unit,
) {
    // Calculate statistics
    val totalDoctors = userProducts.size
    val totalHoursWorked = userProducts.sumOf { doctor ->
        calculateTotalHoursWorked(doctor.schedule)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Statistics List
            Text(text = "• Total hours worked: $totalHoursWorked hours")
            Text(text = "• Total doctors: $totalDoctors")

            // Additional statistics can be added here
        }
    }
}







/**
 * A confirmation button composable typically used in dialogs.
 * The button uses the primary color from the Material theme.
 *
 * @param text The string resource ID for the button text.
 * @param action The action to be triggered when the button is clicked.
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
 * A cancellation button composable typically used in dialogs.
 * The button uses the primary color from the Material theme.
 *
 * @param text The string resource ID for the button text.
 * @param action The action to be triggered when the button is clicked.
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

fun calculateTotalHoursWorked(schedule: DoctorSchedule): Double {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a") // Format: "09:00 AM"

    var totalMinutesWorked = 0L

    schedule.availability.forEach { daySchedule ->
        daySchedule.availableSlots.forEach { slot ->
            if (slot.isAvailable) {
                val start = LocalTime.parse(slot.startTime, timeFormatter)
                val end = LocalTime.parse(slot.endTime, timeFormatter)
                val duration = Duration.between(start, end).toMinutes()
                totalMinutesWorked += duration
            }
        }
    }

    return totalMinutesWorked / 60.0  // Convert minutes to hours
}