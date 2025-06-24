package com.example.careconnect.screens.admin.home

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.R
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.ui.theme.CareConnectTheme

// Overview admin screen
@Composable
fun AdminHomeScreen(
    openAddDoctorScreen: () -> Unit,
    viewModel: AdminHomeViewModel = hiltViewModel()
){

    val appointmentsToday by viewModel.appointmentsToday.collectAsState()
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()
    val recentDoctors by viewModel.recentDoctors.collectAsState()
    val cancelledAppointmentsToday by viewModel.cancelledAppointmentsToday.collectAsState()
    val appointmentsUpcoming by viewModel.appointmentsUpcoming.collectAsState()
    val doctorsWorkingToday by viewModel.doctorsWorkingToday.collectAsState()

    AdminHomeScreenContent(
        openAddDoctorScreen = openAddDoctorScreen,
        appointmentsToday = appointmentsToday,
        upcomingAppointments = upcomingAppointments,
        recentDoctors = recentDoctors,
        cancelledAppointmentsToday = cancelledAppointmentsToday,
        appointmentsUpcoming = appointmentsUpcoming,
        doctorsWorkingToday = doctorsWorkingToday
    )
}


@Composable
fun AdminHomeScreenContent(
    openAddDoctorScreen: () -> Unit,
    appointmentsToday: Int,
    upcomingAppointments: Int,
    recentDoctors: List<Doctor>,
    cancelledAppointmentsToday: Int,
    appointmentsUpcoming: List<Appointment>,
    doctorsWorkingToday: List<Doctor>
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Today's Overview
            Text(text = stringResource(R.string.today_s_overview), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            OverviewSection(
                doctorsAvailable = doctorsWorkingToday.size,
                appointmentsScheduled = appointmentsToday,
                cancellationsToday = cancelledAppointmentsToday
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Activities
            Text(text = stringResource(R.string.recent_activities), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            RecentActivities(
                recentDoctors = recentDoctors,
                upcomingAppointments = appointmentsUpcoming
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions (add doctor)
            Text(text = stringResource(R.string.quick_actions), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            QuickActions(label = stringResource(R.string.add_doctor), onClick = openAddDoctorScreen)
        }
    }
}

@Composable
fun OverviewSection(
    doctorsAvailable: Int,
    appointmentsScheduled: Int,
    cancellationsToday: Int
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("• Doctors Available: $doctorsAvailable")
            Text("• Appointments Scheduled: $appointmentsScheduled")
            Text("• Cancellations Today: $cancellationsToday")

        }

    }
}

@Composable
fun RecentActivities(
    recentDoctors: List<Doctor> = emptyList(),
    upcomingAppointments: List<Appointment> = emptyList()
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {

        LazyColumn(modifier = Modifier.height(300.dp).padding(16.dp)) {
            item { Text("Recently Added Doctors:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 10.dp)) }
            items(recentDoctors) { doctor -> Text(
                "Dr. ${doctor.name} ${doctor.surname}",
                modifier = Modifier.padding(4.dp)
            ) }

            item { Spacer(modifier = Modifier.height(15.dp)) }

            item { Text("Upcoming Appointments:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 10.dp)) }
            items(upcomingAppointments) { appointment ->
                Text(
                    "${appointment.patientName} with doctor ${appointment.doctorName} on ${appointment.appointmentDate}",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActions(
    label: String,
    onClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = { onClick() }) {
            Text(text = label)
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    CareConnectTheme {
        AdminHomeScreenContent(
            openAddDoctorScreen = {},
            appointmentsToday = 10,
            upcomingAppointments = 2,
            recentDoctors = listOf(
                Doctor()),
            cancelledAppointmentsToday = 1,
            appointmentsUpcoming = listOf(
                Appointment()),
            doctorsWorkingToday = listOf(
                Doctor())
        )
    }
}
