package com.example.careconnect.screens.patient.profile.prescription

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

/**
 * UI model representing a prescription with relevant details.
 *
 * @property id Unique identifier of the prescription.
 * @property pdfUrl URL to the prescription PDF document.
 * @property medicationName Name of the medication prescribed.
 * @property issueDate Date when the prescription was issued (optional).
 */
data class PrescriptionUiModel(
    val id: String,
    val pdfUrl: String,
    val medicationName: String,
    val issueDate: String // optional
)

/**
 * Composable screen that displays a list of prescriptions for the current patient.
 *
 * Fetches the prescriptions from [PrescriptionScreenViewModel] and shows
 * them in a scrollable list with options to view, download, or generate QR codes.
 *
 * @param viewModel ViewModel that manages the prescriptions data and state.
 * @param goBack Lambda callback triggered when user wants to navigate back.
 */
@Composable
fun PrescriptionScreen(
    viewModel: PrescriptionScreenViewModel = hiltViewModel(),
    goBack: () -> Unit = {}
){

    val patientId by viewModel.patientId.collectAsState()

    val prescriptions by viewModel.prescriptions.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.fetchPrescriptions(patientId)
    }

    PrescriptionScreenContent(
        prescriptions = prescriptions,
        goBack = goBack
    )
}

/**
 * Composable content that shows the scaffold layout for the prescription screen,
 * including a top bar and a lazy column of [PrescriptionCard] items.
 *
 * Also handles showing a QR code dialog when requested.
 *
 * @param prescriptions List of prescriptions to display.
 * @param goBack Lambda callback for back navigation.
 */
@Composable
fun PrescriptionScreenContent(
    prescriptions: List<PrescriptionUiModel>,
    goBack: () -> Unit = {}
){
    var qrData by remember { mutableStateOf<String?>(null) }
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
                    generateQRCode = { qrData = it }
                )
            }
        }
        qrData?.let { data ->
            QrCodeDialog(data = data, onDismiss = { qrData = null })
        }
    }
}

/**
 * Composable card that represents a single prescription.
 *
 * Displays the medication name and provides a modal bottom sheet with options:
 * Download, View, or Generate QR code.
 *
 * @param prescription The prescription data to display.
 * @param generateQRCode Lambda invoked with the PDF URL to generate a QR code.
 */
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

/**
 * Dialog composable that displays a QR code generated from the provided data string.
 *
 * Provides a close button to dismiss the dialog.
 *
 * @param data The string data encoded in the QR code.
 * @param onDismiss Lambda callback when the dialog is dismissed.
 */
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

/**
 * Top app bar composable for the prescription screen.
 *
 * Displays the screen title and a back navigation icon.
 *
 * @param goBack Lambda callback invoked when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionTopBar(
    goBack: () -> Unit = {}
) {
    TopAppBar(
        modifier = Modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = {
            Text(
                "View Prescriptions",
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { goBack() }) {
                Icon(
                    tint = MaterialTheme.colorScheme.onPrimary,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        }
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