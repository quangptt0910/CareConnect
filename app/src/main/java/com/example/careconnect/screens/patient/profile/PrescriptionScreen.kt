package com.example.careconnect.screens.patient.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme
import qrgenerator.qrkitpainter.rememberQrKitPainter

data class PrescriptionUiModel(
    val id: String,
    val pdfUrl: String,
    val medicationName: String,
    val issueDate: String // optional
)


@Composable
fun PrescriptionScreen(
    viewModel: PrescriptionScreenViewModel = hiltViewModel()
){

    val patientId by viewModel.patientId.collectAsState()

    val prescriptions by viewModel.prescriptions.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.fetchPrescriptions(patientId)
    }

    PrescriptionScreenContent(
        prescriptions = prescriptions
    )
}

@Composable
fun PrescriptionScreenContent(
    prescriptions: List<PrescriptionUiModel>
){
    var qrData by remember { mutableStateOf<String?>(null) }

    LazyColumn {
        items(prescriptions) { prescription ->
            PrescriptionCard(
                prescription,
                generateQRCode = { qrData = it }
            )
        }
    }

    qrData?.let { data ->
        QrCodeDialog(data = data, onDismiss = { qrData = null })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionCard(
    prescription: PrescriptionUiModel,
    generateQRCode: (String) -> Unit = {}
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
                text = prescription.medicationName,
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
            TextButton(onClick = {
                generateQRCode(prescription.pdfUrl)
                showSheet = false
            }) {
                Text("Generate QR code")
            }
        }
    }
}

@Composable
fun QrCodeDialog(data: String, onDismiss: () -> Unit) {
    val painter: Painter = rememberQrKitPainter(data = data)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = { Text("QR Code") },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painter = painter, contentDescription = "QR Code")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scan to view/download prescription",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
@Preview
fun PrescriptionPreview(){
    CareConnectTheme {
        PrescriptionScreenContent(
            prescriptions = listOf(

            )
        )
    }
}