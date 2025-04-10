package com.example.careconnect.screens.admin.doctormanage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.DoctorSchedule
import com.example.careconnect.dataclass.Role
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun DoctorManageScreen(
    onAddDoctorClick: () -> Unit = {}
){
    DoctorManageScreenContent(
        onAddDoctorClick = onAddDoctorClick
    )
}


@Composable
fun DoctorManageScreenContent(
    onAddDoctorClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Doctors",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val doctors = listOf(
                Doctor(
                    id = "doctor123",
                    name = "John",
                    surname = "Doe",
                    email = "john.doe@example.com",
                    role = Role.DOCTOR,
                    phone = "123-456-7890",
                    address = "123 Medical St, Health City",
                    specialization = "Cardiology",
                    experience = 2015,
                    schedule = DoctorSchedule() // Assuming DoctorSchedule has a default constructor
                ),
                Doctor(
                    id = "doctor456",
                    name = "Jane",
                    surname = "Smith",
                    email = "jane.smith@example.com",
                    role = Role.DOCTOR,
                    phone = "987-654-3210",
                    address = "456 Health Rd, Cityville",
                    specialization = "Dermatology",
                    experience = 2010,
                    schedule = DoctorSchedule()
                )
            )

            FilledCardExample(
                title = stringResource(R.string.doctor),
                doctors = doctors,
                onDeleteProduct = {}
            )

            FilledCardStats(
                title = "Total hours worked",
                userProducts = doctors,
                onDeleteProduct = {}
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            FloatingActionButton(
                onClick = onAddDoctorClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(60.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Doctor")
            }
        }

    }
}


@Preview
@Composable
fun DoctorManageScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        DoctorManageScreenContent(
        )
    }
}