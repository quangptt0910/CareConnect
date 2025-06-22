package com.example.careconnect.screens.admin.patientsmanage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.dataclass.Gender
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun PatientManageScreen(

){
    PatientManageScreenContent()
}


@Composable
fun PatientManageScreenContent(
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
                    text = "Patients",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val patients = listOf(
                Patient(
                    id = "patient456",
                    name = "Jane",
                    surname = "Smith",
                    email = "jane.smith@example.com",
                    role = Role.PATIENT,
                    phone = "987-654-3210",
                    address = "456 Wellness Ave, Care Town",
                    dateOfBirth = "1990-05-15",
                    gender = Gender.FEMALE,
                    height = 165.0, // Height in cm
                    weight = 50.0,  // Weight in kg
                )
            )

            FilledCardPatients(
                title = "Patients",
                patients = patients,
                onDeleteProduct = { /* Handle product deletion */ }
            )

        }


    }
}


@Preview
@Composable
fun PatientsManageScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        PatientManageScreenContent(
        )
    }
}