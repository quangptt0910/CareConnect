package com.example.careconnect.screens.patient.profile


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme

data class MedicalReportUiModel(
    val id: String,
    val pdfUrl: String,
    val diagnosis: String,
    val reportDate: String
)

@Composable
fun MedicalReportScreen(
    viewModel: MedicalReportViewModel = hiltViewModel()
){

    val patientId by viewModel.patientId.collectAsState()

    val medicalReport by viewModel.medicalReport.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.fetchMedicalReports(patientId)
    }

    MedicalReportScreenContent(
        medicalReports = medicalReport
    )
}

@Composable
fun MedicalReportScreenContent(
    medicalReports: List<MedicalReportUiModel>
){
    LazyColumn {
        items(medicalReports) { medicalReport ->
            MedicalReportCard(
                medicalReport
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalReportCard(
    medicalReport: MedicalReportUiModel
) {
    var showSheet by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = { showSheet = true },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.downloadable_file,
                contentDescription = "File Icon",
                modifier = Modifier
                    .size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = medicalReport.diagnosis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            TextButton(onClick = {
                // download logic
                showSheet = false
            }) {
                Text("Download")
            }
            TextButton(onClick = {
                // view logic (e.g., open PDF viewer screen)
                showSheet = false
            }) {
                Text("View")
            }
        }
    }
}

@Composable
@Preview
fun MedicalReportPreview() {
    CareConnectTheme {
        MedicalReportScreenContent(
            medicalReports = listOf(

            )
        )
    }
}
