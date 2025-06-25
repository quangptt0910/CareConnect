package com.example.careconnect.screens.doctor.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.Task
import com.example.careconnect.ui.theme.CareConnectTheme
import java.time.LocalDate


/**
 * Composable function for the Doctor's Home Screen.
 *
 * This screen displays an overview for the doctor, including:
 * - Welcome message and current date.
 * - Statistics for total appointments, patients, and tasks.
 * - Lists of pending and upcoming appointments.
 * - A list of the doctor's patients.
 * - A list of tasks with options to add, edit, and delete tasks.
 *
 * It uses a [DoctorHomeViewModel] to fetch and manage data.
 *
 * @param openSettingsScreen Lambda function to navigate to the settings screen.
 * @param openNotificationsScreen Lambda function to navigate to the notifications screen.
 * @param viewModel Instance of [DoctorHomeViewModel] for this screen.
 */
@Composable
fun DoctorHomeScreen(
    openSettingsScreen: () -> Unit,
    openNotificationsScreen: () -> Unit,
    viewModel: DoctorHomeViewModel = hiltViewModel()
) {
    val patientList by viewModel.patientList.collectAsStateWithLifecycle(emptyList())
    val pendingAppointmentList by viewModel.pendingAppointments.collectAsStateWithLifecycle()
    val upcomingAppointmentList by viewModel.appointments.collectAsStateWithLifecycle()
    val tasks = viewModel.tasks.collectAsStateWithLifecycle(emptyList())


    val totalAppointments = pendingAppointmentList.size + upcomingAppointmentList.size
    val totalPatients = patientList.size

    DoctorHomeScreenContent(
        openSettingsScreen = openSettingsScreen,
        patientList = patientList,
        upcomingAppointmentList = upcomingAppointmentList,
        pendingAppointmentList = pendingAppointmentList,
        totalAppointments = totalAppointments,
        totalPatients = totalPatients,
        onAccept = { appt ->
            viewModel.updateAppointmentStatus(appt, AppointmentStatus.CONFIRMED)
        },
        onDecline = { appt ->
            viewModel.updateAppointmentStatus(appt, AppointmentStatus.CANCELED)
        },
        tasks = tasks.value,
        onAddTask = viewModel::addTask,
        onUpdateTask = { task ->
            viewModel.updateTask(task)
        },
        onDeleteTask = { task ->
            viewModel.deleteTask(task)
        },
        showSnackBar = {},
        openNotificationsScreen = openNotificationsScreen
    )
}


/**
 * Content composable for the Doctor's Home Screen.
 *
 * This composable is responsible for laying out the UI elements of the screen.
 * It is a stateless composable that receives all its data and callbacks as parameters.
 *
 * @param openSettingsScreen Lambda function to navigate to the settings screen.
 * @param patientList List of [Patient] objects to display.
 * @param upcomingAppointmentList List of upcoming [Appointment] objects.
 * @param pendingAppointmentList List of pending [Appointment] objects.
 * @param tasks List of [Task] objects.
 * @param totalAppointments Total number of appointments (pending + upcoming).
 * @param totalPatients Total number of patients.
 * @param onAccept Callback function when a pending appointment is accepted.
 * @param onDecline Callback function when a pending appointment is declined.
 * @param onAddTask Callback function to add a new task.
 * @param onUpdateTask Callback function to update an existing task.
 * @param onDeleteTask Callback function to delete a task.
 * @param showSnackBar Callback function to display a snackbar message.
 * @param openNotificationsScreen Lambda function to navigate to the notifications screen.
 */
@Composable
fun DoctorHomeScreenContent(
    openSettingsScreen: () -> Unit = {},
    patientList: List<Patient> = emptyList(),
    upcomingAppointmentList: List<Appointment> = emptyList(),
    pendingAppointmentList: List<Appointment> = emptyList(),
    tasks: List<Task>,
    totalAppointments: Int = 0,
    totalPatients: Int = 0,
    onAccept: (Appointment) -> Unit = {},
    onDecline: (Appointment) -> Unit = {},
    onAddTask: (Task, (SnackBarMessage) -> Unit) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    openNotificationsScreen: () -> Unit = {}
) {
    val date = LocalDate.now()
    var isAddDialogOpen by remember { mutableStateOf(false) }
    val selectedTask = remember { mutableStateOf<Task?>(null) }
    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    val totalTasks = tasks.size

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Welcome back!",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Row {
                        IconButton(onClick = { openNotificationsScreen() }) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                        }
                        IconButton(onClick = openSettingsScreen) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                }

                Text(
                    text = "$date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                // Stat cards
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        title = stringResource(R.string.appointments),
                        value = totalAppointments.toString(),
                        modifier = Modifier.weight(1f).padding(3.dp),
                        content = { Icon(Icons.Outlined.CalendarMonth, null) }
                    )
                    StatCard(
                        title = stringResource(R.string.patients),
                        value = totalPatients.toString(),
                        modifier = Modifier.weight(1f).padding(3.dp),
                        content = { Icon(Icons.Outlined.PersonOutline, null) }
                    )
                    StatCard(
                        title = stringResource(R.string.tasks),
                        value = totalTasks.toString(),
                        modifier = Modifier.weight(1f).padding(3.dp),
                        content = { Icon(Icons.Outlined.Checklist, null) }
                    )
                }
            }

            if (pendingAppointmentList.isNotEmpty()) {
                item {
                    Text("Appointments Pending", style = MaterialTheme.typography.titleLarge)
                }
                items(pendingAppointmentList) { appt ->
                    PendingAppointmentCard(
                        appt = appt,
                        onAccept = { onAccept(appt) },
                        onDecline = { onDecline(appt) }
                    )
                }
            }

            item { HorizontalDivider() }

            item {
                Text("Upcoming appointments", style = MaterialTheme.typography.titleLarge)
            }

            items(upcomingAppointmentList) { appointment ->
                AppointmentCard(
                    appt = appointment,
                    displayFields = listOf(
                        "Patient" to { it.patientName },
                        "Type" to { it.type }
                    )
                )
            }

            item {
                Text("My Patients", style = MaterialTheme.typography.titleLarge)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    if (patientList.isEmpty()) {
                        ListItem(
                            headlineContent = { Text("No patients found") },
                            colors = ListItemDefaults.colors(Color.LightGray)
                        )
                    } else {
                        Column {
                            patientList.forEach { patient ->
                                ListItem(
                                    headlineContent = { Text("${patient.name} ${patient.surname}") },
                                    colors = ListItemDefaults.colors(Color.LightGray)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text("Tasks", style = MaterialTheme.typography.titleLarge)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column {
                        tasks.forEach { task ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isChecked,
                                    onCheckedChange = {
                                        val updated = task.copy(isChecked = it)
                                        onUpdateTask(updated)
                                    }
                                )
                                Text(
                                    text = task.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                        .clickable {
                                            selectedTask.value = task
                                            isAddDialogOpen = true
                                        }
                                )
                                IconButton(
                                    onClick = {
                                    selectedTask.value = task
                                    isDeleteDialogOpen = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                                }
                            }
                        }

                        // Add task
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTask.value = null
                                    isAddDialogOpen = true
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Add Task", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }

        if (isAddDialogOpen) {
            val isEditing = selectedTask.value != null
            var taskName by remember { mutableStateOf(selectedTask.value?.name ?: "") }

            AlertDialog(
                onDismissRequest = { isAddDialogOpen = false },
                confirmButton = {
                    Button(onClick = {
                        val updatedTask = selectedTask.value?.copy(name = taskName) ?: Task(name = taskName)
                        onAddTask(updatedTask, showSnackBar)
                        isAddDialogOpen = false
                    }, enabled = taskName.isNotBlank()) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Row {
                        Button(onClick = { isAddDialogOpen = false }) {
                            Text("Cancel")
                        }
                    }
                },
                title = { Text("Edit Task") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = taskName,
                            onValueChange = { taskName = it },
                            label = { Text("Task Name") }
                        )
                    }
                }
            )
        }
        // end of isAddDialogOpen

        // delete confirmation dialog
        if (isDeleteDialogOpen) {
            val taskToDelete = selectedTask.value
            if (taskToDelete != null) {
                AlertDialog(
                    onDismissRequest = { isDeleteDialogOpen = false },
                    confirmButton = {
                        Button(onClick = {
                            onDeleteTask(taskToDelete)
                            isDeleteDialogOpen = false
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { isDeleteDialogOpen = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Delete Task") },
                    text = { Text("Are you sure you want to delete this task?") }
                )
                }
            }
        }

}


/**
 * Composable function to display a card for a pending appointment.
 *
 * This card shows information about a pending appointment and provides
 * buttons to accept or decline the appointment.
 *
 * @param appt The [Appointment] object to display.
 * @param onAccept Lambda function to be invoked when the "Accept" button is clicked.
 * @param onDecline Lambda function to be invoked when the "Decline" button is clicked.
 */
@Composable
fun PendingAppointmentCard(
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
            pendingAppointmentList = pendingAppointmentList,
            showSnackBar = {},
            onAddTask = { _, _ -> },
            onUpdateTask = {},
            onDeleteTask = {},
            tasks = listOf(
                Task(name = "Task 1 Super long long name nameeeeeee", isChecked = true),
                Task(name = "Task 2", isChecked = false)
            )
        )
    }
}