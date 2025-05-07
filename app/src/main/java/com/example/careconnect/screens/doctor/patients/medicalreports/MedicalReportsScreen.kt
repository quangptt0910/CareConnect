package com.example.careconnect.screens.doctor.patients.medicalreports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun MedicalReportsScreen(
    patientId: String,
    openCreateMedicalReportScreen: (patientId: String) -> Unit

){
    MedicalReportsScreenContent(
        patientId = patientId,
        openCreateMedicalReportScreen = openCreateMedicalReportScreen
    )
}

@Composable
fun MedicalReportsScreenContent(
    patientId: String,
    openCreateMedicalReportScreen: (patientId: String) -> Unit = {}
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
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
            openCreateMedicalReportScreen = {}
        )
    }
}