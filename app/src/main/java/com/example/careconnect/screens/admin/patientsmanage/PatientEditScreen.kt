package com.example.careconnect.screens.admin.patientsmanage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun PatientEditScreen(

){

}

/**
 * Content composable for [PatientEditScreen].
 *
 * Displays the patient profile heading, and placeholders for editing patient info
 * and appointments using [FilledCardEdit] and [FilledCardAppointment].
 */
@Composable
fun PatientEditScreenContent(
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
                    text = "Patient Profile",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

//            val patients = listOf(
//                Patient("John", "Doe", "133", "123", "123", "Cardiologist"),
//                Patient("Jane", "Smith", "133", "123", "123", "Dermatologist"),
//                Patient("Bob", "Johnson", "133", "123", "123", "Neurologist"),
//                Patient("Alice", "Williams", "133", "123", "123", "Pediatrician"),
//                Patient("David", "Brown", "133", "123", "123", "Orthopedic Surgeon"),
//                Patient("Emily", "Jones", "133", "123", "123", "Gynecologist"),
//            )

            FilledCardEdit()

            Spacer(modifier = Modifier.height(15.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(15.dp))

            FilledCardAppointment()


        }
    }
}


@Preview
@Composable
fun PatientEditScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        PatientEditScreenContent(
        )
    }
}