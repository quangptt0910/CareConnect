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
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.screens.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun PatientsManageScreen(

){

}


@Composable
fun PatientsManageScreenContent(
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

            Spacer(modifier = Modifier.height(20.dp))

            val patients = listOf(
                Patient("John", "Doe", "133","123","123","Cardiologist"),
                Patient("Jane", "Smith","133","123","123", "Dermatologist"),
                Patient("Bob", "Johnson","133","123","123", "Neurologist"),
                Patient("Alice", "Williams","133","123","123", "Pediatrician"),
                Patient("David", "Brown","133","123","123", "Orthopedic Surgeon"),
                Patient("Emily", "Jones","133","123","123", "Gynecologist"),
            )

            FilledCardPatients(
                title = "Patients",
                userProducts = patients,
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
        PatientsManageScreenContent(
        )
    }
}