package com.example.careconnect.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
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


@Composable
fun AppointmentCard(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Appointment ID: ${appointment.id}",
                style = MaterialTheme.typography.titleMedium
            )


            Text(text = "Patient ID: ${appointment.patientId}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(text = "Doctor ID: ${appointment.doctorId}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}

 @Preview
@Composable
fun AppointmentCardPreview() {
    val appointment = Appointment(
        id = "123",
        patientId = "456",
        doctorId = "789",
        appointmentDate = "2023-05-15",
        startTime = "10:00 AM",
        endTime = "11:00 AM",
        status = AppointmentStatus.PENDING
    )
    AppointmentCard(appointment =appointment)
}


//@Preview(showBackground = true)
//@Composable
//fun CardEditorPreview() {
//    MaterialTheme {
//        Surface(
//            color = MaterialTheme.colorScheme.background,
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Column {
//                // Card with content
//                RegularCardEditor(
//                    title = R.string.app_name, // Using app_name as placeholder, replace with actual string resource
//                    icon = Icons.Default.Phone,
//                    content = "+1 (555) 123-4567",
//                    modifier = Modifier.fillMaxWidth(),
//                    onEditClick = {}
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Card with empty content
//                RegularCardEditor(
//                    title = R.string.app_name, // Replace with actual string resource
//                    icon = Icons.Default.Email,
//                    content = "",
//                    modifier = Modifier.fillMaxWidth(),
//                    onEditClick = {}
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Card with long content
//                RegularCardEditor(
//                    title = R.string.app_name, // Replace with actual string resource
//                    icon = Icons.Default.LocationOn,
//                    content = "123 Medical Center Drive, Suite 456, Healthcare City, CA 90210",
//                    modifier = Modifier.fillMaxWidth(),
//                    onEditClick = {}
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Card with custom highlight color (uses private CardEditor directly)
//                CardEditor(
//                    title = R.string.app_name, // Replace with actual string resource
//                    icon = Icons.Default.Edit,
//                    content = "Custom highlight color example",
//                    onEditClick = {},
//                    highlightColor = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
//        }
//    }
//}