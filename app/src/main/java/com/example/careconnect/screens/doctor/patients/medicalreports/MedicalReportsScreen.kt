package com.example.careconnect.screens.doctor.patients.medicalreports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.screens.patient.profile.medicalreport.MedicalReportCard
import com.example.careconnect.screens.patient.profile.medicalreport.MedicalReportUiModel
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun MedicalReportsScreen(
    viewModel: MedicalReportsViewModel = hiltViewModel(),
    patientId: String,
    openCreateMedicalReportScreen: (patientId: String) -> Unit

){

    val medicalReports by viewModel.medicalReport.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.fetchMedicalReports(patientId)
    }

    MedicalReportsScreenContent(
        patientId = patientId,
        openCreateMedicalReportScreen = openCreateMedicalReportScreen,
        medicalReports = medicalReports
    )
}

@Composable
fun MedicalReportsScreenContent(
    patientId: String,
    openCreateMedicalReportScreen: (patientId: String) -> Unit = {},
    medicalReports: List<MedicalReportUiModel>
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){

        LazyColumn {
            items(medicalReports) { medicalReport ->
                MedicalReportCard(
                    medicalReport
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            FloatingActionButton(
                onClick = {openCreateMedicalReportScreen(patientId)},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(60.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Medical Report")
            }
        }
    }
}

@Preview
@Composable
fun MedicalReportsScreenPreview(){
    CareConnectTheme {
        MedicalReportsScreenContent(
            patientId = "123",
            openCreateMedicalReportScreen = {},
            medicalReports = listOf(

            )
        )
    }
}