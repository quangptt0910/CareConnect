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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.ui.theme.CareConnectTheme
import java.time.LocalDate

@Composable
fun HomeScreenDoctor(
    openSettingsScreen: () -> Unit
) {
    HomeScreenDoctorContent(
        openSettingsScreen = openSettingsScreen
    )
}

@Composable
fun HomeScreenDoctorContent(
    openSettingsScreen: () -> Unit = {}
){
    val date = LocalDate.now()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Column(
            modifier = Modifier.fillMaxWidth().padding(15.dp)
        ){
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
                    onClick = {  },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ){
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
            ){
                Card(
                    modifier = Modifier.weight(1f).padding(3.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)

                ){
                    Text(
                        text = "Appointment",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth().padding(5.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        Text(
                            text = "5",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(5.dp)
                        )
                        //Spacer(modifier = Modifier.width(60.dp))
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                        )
                    }

                }

                Card(
                    modifier = Modifier.weight(1f).padding(3.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)

                ){
                    Text(
                        text = "Patients",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth().padding(5.dp)
                    )
                    Row(

                    ){
                        Text(
                            text = "5",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(5.dp)
                        )
                        //Spacer(modifier = Modifier.width(60.dp))
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = null,
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).padding(3.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)

                ){
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth().padding(5.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        Text(
                            text = "5",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(5.dp)
                        )
                        //Spacer(modifier = Modifier.width(60.dp))
                        Icon(
                            imageVector = Icons.Outlined.Checklist,
                            contentDescription = null,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Upcoming appointments",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(15.dp)
            )


            DailyAppointmentsSection(
                appointments = listOf(
                    Appointment(
                        id = "1",
                        patientId = "",
                        doctorId = "",
                        appointmentDate = "",
                        startTime = "10:00 AM",
                        endTime = "11:00 AM",
                        address = "",
                        status = AppointmentStatus.PENDING
                    ),
                    Appointment(
                        id = "2",
                        patientId = "",
                        doctorId = "",
                        appointmentDate = "",
                        startTime = "11:00 AM",
                        endTime = "12:00 PM",
                        address = "",
                        status = AppointmentStatus.PENDING
                    ),
                )
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
            ){
                ListItem(
                    headlineContent = { Text(text = "Name Surname") },
                    colors = ListItemDefaults.colors(Color.LightGray)
                )
                ListItem(
                    headlineContent = { Text(text = "Name Surname") },
                    colors = ListItemDefaults.colors(Color.LightGray)
                )
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
            ){
                val (checkedState, onStateChange) = remember { mutableStateOf(true) }
                Row(     Modifier. fillMaxWidth()
                    .height(56.dp)
                    .toggleable(value = checkedState,
                        onValueChange = { onStateChange(!checkedState) },
                        role = androidx.compose.ui.semantics.Role.Checkbox)
                    .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment. CenterVertically
                ){
                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = {}
                    )
                    Text(text = "Check me out",
                        style = MaterialTheme.typography.bodyLarge,)


                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { /* View/Start consultation */ }) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.padding(start = 20.dp))

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = appointment.startTime, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Name Surname", style = MaterialTheme.typography.titleMedium)
                    }
                    IconButton(onClick = { /* View/Start consultation */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
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
        HomeScreenDoctorContent()
    }
}