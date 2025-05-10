package com.example.careconnect.common

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
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus

@Composable
private fun CardEditor(
    @StringRes title: Int,
    //@DrawableRes icon: Int,
    icon: ImageVector,
    content: String,
    onEditClick: () -> Unit,
    highlightColor: Color,
    modifier: Modifier
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier,
        onClick = onEditClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) { Text(stringResource(title), color = highlightColor) }

            if (content.isNotBlank()) {
                Text(text = content,
                    modifier = Modifier.padding(16.dp, 0.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = highlightColor)
        }
    }
}


@Composable
fun RegularCardEditor(
    @StringRes title: Int,
    //@DrawableRes icon: Int,
    icon: ImageVector,
    content: String,
    modifier: Modifier,
    onEditClick: () -> Unit
) {
    CardEditor(title, icon, content, onEditClick, MaterialTheme.colorScheme.onSurface, modifier)
}



 @Preview
@Composable
fun AppointmentCardPreview() {
    val appointment = Appointment(
        id = "123",
        patientId = "456",
        doctorId = "789",
        patientName = "John Doe",
        doctorName = "Dr. Smith",
        type = "Consultation",
        address = "123 Main St",
        appointmentDate = "2023-05-15",
        startTime = "10:00",
        endTime = "11:00",
        status = AppointmentStatus.PENDING
    )
    AppointmentCard(appt = appointment,
        displayFields = listOf(
            "Patient" to { it.patientName },
            "Type" to { it.type },
            "Address" to { it.address }
        )
        )
}

@Composable
fun AppointmentCard(
    modifier: Modifier = Modifier,
    appt: Appointment,
    displayFields: List<Pair<String, (Appointment) -> String>> = listOf(
        "Patient" to { it.patientName },
        "Doctor" to { it.doctorName },
        "Type" to { it.type },
        "Address" to { it.address }
    ),

) {
    Card(modifier = modifier.fillMaxWidth(),colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = appt.status.toString().lowercase()
                            .replaceFirstChar { it.uppercase() },
                        color = appt.status.color,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Date And Time",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = appt.appointmentDate + ", " + appt.startTime + "-" + appt.endTime,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            // Improved Details Section with aligned values
            if (displayFields.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))

                // Use the new, simpler approach with the Grid layout
                DetailsGrid(
                    displayFields = displayFields,
                    getValue = { field -> field(appt) },
                    labelWidth = 80.dp // Adjust this value as needed
                )
            }
        }
    }
}

@Composable
private fun DetailsGrid(
    displayFields: List<Pair<String, (Appointment) -> String>>,
    getValue: (((Appointment) -> String)) -> String,
    labelWidth: Dp = 80.dp, // Adjustable parameter
    spaceBetween: Dp = 8.dp,
    labelEndPadding: Dp = 8.dp
) {
    Column(verticalArrangement = Arrangement.spacedBy(spaceBetween)) {
        displayFields.forEach { (label, valueGetter) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fixed-width label container aligned to the start (left)
                Text(
                    text = "$label:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .width(labelWidth)
                        .padding(end = labelEndPadding),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Value with remaining space
                Text(
                    text = getValue(valueGetter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(4.dp)
            )
            content()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StatCardPreview() {
    StatCard(
        title = "Title",
        value = "Value",
        modifier = Modifier.fillMaxWidth(),
        content = {
            Icon(
                imageVector = Icons.Filled.AccessAlarms,
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
    )
}