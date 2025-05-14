package com.example.careconnect.screens.doctor.home

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.common.AppointmentCard
import com.example.careconnect.common.StatCard
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.ui.theme.CareConnectTheme
import java.time.LocalDate

@Composable
fun DoctorHomeScreen(
    openSettingsScreen: () -> Unit,
    viewModel: DoctorHomeViewModel = hiltViewModel()
) {
    val patientList by viewModel.patientList.collectAsStateWithLifecycle(emptyList())
    val pendingAppointmentList by viewModel.pendingAppointments.collectAsStateWithLifecycle()
    val upcomingAppointmentList by viewModel.appointments.collectAsStateWithLifecycle()

    DoctorHomeScreenContent(
        openSettingsScreen = openSettingsScreen,
        patientList = patientList,
        upcomingAppointmentList = upcomingAppointmentList,
        pendingAppointmentList = pendingAppointmentList,
        onAccept = { appt ->
            viewModel.updateAppointmentStatus(appt, AppointmentStatus.CONFIRM)
        },
        onDecline = { appt ->
            viewModel.updateAppointmentStatus(appt, AppointmentStatus.CANCELED)
        }
    )
}

@Composable
fun DoctorHomeScreenContent(
    openSettingsScreen: () -> Unit = {},
    patientList: List<Patient> = emptyList(),
    upcomingAppointmentList: List<Appointment> = emptyList(),
    pendingAppointmentList: List<Appointment> = emptyList(),
    onAccept: (Appointment) -> Unit = {},
    onDecline: (Appointment) -> Unit = {}
) {
    val date = LocalDate.now()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Welcome back!",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Go to notifications screen
                IconButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                }

                // Go to settings screen
                IconButton(
                    onClick = { openSettingsScreen() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            }

            Text(
                text = "$date",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
            ) {
                StatCard(
                    title = stringResource(R.string.appointments),
                    value = "5",
                    modifier = Modifier.weight(1f).padding(3.dp),
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                        )
                    }
                )

                StatCard(
                    title = stringResource(R.string.patients),
                    value = "5",
                    modifier = Modifier.weight(1f).padding(3.dp),
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = null,
                        )
                    }
                )

                StatCard(
                    title = stringResource(R.string.tasks),
                    value = "5",
                    modifier = Modifier.weight(1f).padding(3.dp),
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.Checklist,
                            contentDescription = null,
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (pendingAppointmentList.isNotEmpty()) {
                Text(
                    text = "Appointments Pending",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(15.dp)
                )

                pendingAppointmentList.forEach { appt ->
                    PendingAppointmentCard(
                        appt = appt,
                        onAccept = { onAccept(appt) },
                        onDecline = { onDecline(appt) }
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "Upcoming appointments",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(15.dp)
            )

            DailyAppointmentsSection(
                appointments = upcomingAppointmentList
            )

            Text(
                text = "My Patients",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(15.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                if (patientList.isEmpty()) {
                    ListItem(
                        headlineContent = { Text(text = "No patients found") },
                        colors = ListItemDefaults.colors(Color.LightGray)
                    )
                } else {
                    patientList.forEach { patient ->
                        ListItem(
                            headlineContent = { Text(text = "${patient.name} ${patient.surname}") },
                            colors = ListItemDefaults.colors(Color.LightGray)
                        )
                    }
                }
            }

                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(15.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    val (checkedState, onStateChange) = remember { mutableStateOf(true) }
                    Row(
                        Modifier.fillMaxWidth()
                            .height(56.dp)
                            .toggleable(
                                value = checkedState,
                                onValueChange = { onStateChange(!checkedState) },
                                role = androidx.compose.ui.semantics.Role.Checkbox
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedState,
                            onCheckedChange = {}
                        )
                        Text(
                            text = "Check me out",
                            style = MaterialTheme.typography.bodyLarge,
                        )


                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { /* View/Start consultation */ }) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 20.dp)
                            )

                        }
                        Text(
                            text = "Add Task",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth().padding(start = 16.dp, top = 13.dp)
                        )
                    }
                }
            }
        }
    }

@Composable
fun DailyAppointmentsSection(appointments: List<Appointment>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appt = appointment,
                displayFields = listOf(
                    "Patient" to { it.patientName },
                    "Type" to { it.type },)
                )
        }
    }
}

@Composable
fun PendingAppointmentCard(
    modifier: Modifier = Modifier,
    appt: Appointment,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    displayFields: List<Pair<String, (Appointment) -> String>> = listOf(
        "Patient" to { it.patientName },
        "Type" to { it.type },
    )
) {
    Card {
        Column {
            // Use existing AppointmentCard layout
            AppointmentCard(
                modifier = Modifier.fillMaxWidth(),
                appt = appt,
                displayFields = displayFields
            )
            // Add action buttons only for pending status
            if (appt.status == AppointmentStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.padding(start = 40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("   Accept   ")
                    }

                    Button(
                        onClick = onDecline,
                        modifier = Modifier.padding(end = 40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("   Decline   ")
                    }


                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenDoctorPreview() {
    CareConnectTheme {
        val pendingAppointmentList = listOf(
            Appointment(
                id = "1",
                patientId = "123",
                doctorId = "1234",
                patientName = "John Doe",
                doctorName = "Dr. Smith",
                type = "Consultation",
                address = "123 Main St",
                appointmentDate = "2025-05-10",
                startTime = "10:00",
                endTime = "11:00",
                status = AppointmentStatus.PENDING
            ),
        )
        DoctorHomeScreenContent(
            patientList = listOf(
                Patient(
                    name = "Nerike",
                    surname = "Bosch"
                ),
                Patient(
                    name = "Nicky",
                    surname = "Bosch"
                )
            ),
            pendingAppointmentList = pendingAppointmentList
        )
    }
}