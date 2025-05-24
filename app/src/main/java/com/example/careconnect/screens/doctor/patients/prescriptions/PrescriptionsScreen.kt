package com.example.careconnect.screens.doctor.patients.prescriptions

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
fun PrescriptionScreen(
    patientId: String,
    openCreatePrescriptionsScreen: (patientId: String) -> Unit

){
    PrescriptionsScreenContent(
        patientId = patientId,
        openCreatePrescriptionsScreen = openCreatePrescriptionsScreen
    )
}

@Composable
fun PrescriptionsScreenContent(
    patientId: String,
    openCreatePrescriptionsScreen: (patientId: String) -> Unit = {}
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            FloatingActionButton(
                onClick = {openCreatePrescriptionsScreen(patientId)},
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
fun PrescriptionsScreenPreview(){
    CareConnectTheme {
        PrescriptionsScreenContent(
            patientId = "123",
            openCreatePrescriptionsScreen = {}
        )
    }
}