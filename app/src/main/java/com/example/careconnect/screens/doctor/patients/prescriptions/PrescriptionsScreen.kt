package com.example.careconnect.screens.doctor.patients.prescriptions

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.screens.patient.profile.prescription.PrescriptionCard
import com.example.careconnect.screens.patient.profile.prescription.PrescriptionTopBar
import com.example.careconnect.screens.patient.profile.prescription.PrescriptionUiModel
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun PrescriptionScreen(
    viewModel: PrescriptionsViewModel = hiltViewModel(),
    patientId: String,
    openCreatePrescriptionsScreen: (patientId: String) -> Unit,
    goBack: () -> Unit
){
    val prescriptions by viewModel.prescriptions.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.fetchPrescriptions(patientId)
    }

    PrescriptionsScreenContent(
        patientId = patientId,
        openCreatePrescriptionsScreen = openCreatePrescriptionsScreen,
        prescriptions = prescriptions,
        goBack = goBack
    )
}

@Composable
fun PrescriptionsScreenContent(
    patientId: String,
    openCreatePrescriptionsScreen: (patientId: String) -> Unit = {},
    prescriptions: List<PrescriptionUiModel>,
    goBack: () -> Unit = {}
){
    Scaffold(
        topBar = {
            PrescriptionTopBar(
                goBack = goBack
            )
        }
    ){ paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            items(prescriptions) { prescription ->
                PrescriptionCard(
                    prescription,
                    generateQRCode = {  }
                )
            }
        }

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
            openCreatePrescriptionsScreen = {},
            prescriptions = listOf(
            )
        )
    }
}