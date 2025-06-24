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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun PatientManageScreen(
    viewModel: PatientsManageViewModel = hiltViewModel()
){
    val patients by viewModel.patients.collectAsState()

    PatientManageScreenContent(
        patients = patients
    )
}


@Composable
fun PatientManageScreenContent(
    patients: List<Patient> = emptyList()
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