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
import com.example.careconnect.screens.patient.profile.medicalreport.MedicalReportCard
import com.example.careconnect.screens.patient.profile.medicalreport.MedicalReportTopBar
import com.example.careconnect.screens.patient.profile.medicalreport.MedicalReportUiModel
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Composable screen displaying a list of medical reports for a given patient.
 * Automatically loads reports and provides a button to add a new report.
 *
 * @param viewModel ViewModel used to manage and fetch medical reports.
 * @param patientId ID of the patient whose reports are displayed.
 * @param openCreateMedicalReportScreen Callback to navigate to the report creation screen.
 * @param goBack Callback to navigate back.
 */
@Composable
fun MedicalReportsScreen(
    viewModel: MedicalReportsViewModel = hiltViewModel(),
    patientId: String,
    openCreateMedicalReportScreen: (patientId: String) -> Unit,
    goBack: () -> Unit = {}
){

    val medicalReports by viewModel.medicalReport.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.fetchMedicalReports(patientId)
    }

    MedicalReportsScreenContent(
        patientId = patientId,
        openCreateMedicalReportScreen = openCreateMedicalReportScreen,
        medicalReports = medicalReports,
        goBack = goBack
    )
}

/**
 * Composable content for the [MedicalReportsScreen], showing the list of reports
 * and a Floating Action Button to create a new one.
 *
 * @param patientId ID of the patient.
 * @param openCreateMedicalReportScreen Function to navigate to report creation.
 * @param medicalReports List of medical reports to display.
 * @param goBack Callback to navigate back.
 */
@Composable
fun MedicalReportsScreenContent(
    patientId: String,
    openCreateMedicalReportScreen: (patientId: String) -> Unit = {},
    medicalReports: List<MedicalReportUiModel>,
    goBack: () -> Unit = {}
){
    Scaffold(
        topBar = {
            MedicalReportTopBar(
                goBack = goBack
            )
        }
    ){ paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
        ) {
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

/**
 * Preview for the [MedicalReportsScreenContent] composable.
 */
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