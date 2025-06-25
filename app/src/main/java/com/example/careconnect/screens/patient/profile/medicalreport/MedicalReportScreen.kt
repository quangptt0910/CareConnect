package com.example.careconnect.screens.patient.profile.medicalreport


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Represents a UI model for a medical report containing necessary details
 * to be displayed on the screen.
 *
 * @property id Unique identifier of the medical report.
 * @property pdfUrl URL to the medical report PDF document.
 * @property diagnosis Diagnosis or title of the medical report.
 * @property reportDate Date when the report was created or issued.
 */
data class MedicalReportUiModel(
    val id: String,
    val pdfUrl: String,
    val diagnosis: String,
    val reportDate: String
)


/**
 * Composable screen displaying a list of medical reports for the current patient.
 *
 * Fetches the patient's medical reports via the [MedicalReportViewModel] and displays
 * them in a scrollable list. Allows navigation back via [goBack].
 *
 * @param viewModel ViewModel responsible for fetching and holding medical reports data.
 * @param goBack Lambda callback invoked to navigate back.
 */
@Composable
fun MedicalReportScreen(
    viewModel: MedicalReportViewModel = hiltViewModel(),
    goBack: () -> Unit = {}
){

    val patientId by viewModel.patientId.collectAsState()

    val medicalReport by viewModel.medicalReport.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.fetchMedicalReports(patientId)
    }

    MedicalReportScreenContent(
        medicalReports = medicalReport,
        goBack = goBack
    )
}

/**
 * Composable content showing a scaffold with a top bar and a list of medical report cards.
 *
 * @param medicalReports List of medical reports to display.
 * @param goBack Lambda callback invoked when the back navigation is triggered.
 */
@Composable
fun MedicalReportScreenContent(
    medicalReports: List<MedicalReportUiModel>,
    goBack: () -> Unit = {}
){
    Scaffold(
        topBar = {
            MedicalReportTopBar(
                goBack = goBack
            )
        }
    ) { paddingValues ->
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
    }
}

/**
 * Composable card representing a single medical report.
 *
 * Displays diagnosis text and provides a modal bottom sheet to download or view the report PDF.
 *
 * @param medicalReport The medical report data to display in the card.
 */
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
            val context = LocalContext.current

            TextButton(onClick = {
                Log.d("PDF_URL", "URL is: ${medicalReport.pdfUrl}")
                downloadPdfOnly(context, medicalReport.pdfUrl)
                showSheet = false
            }) {
                Text("Download")
            }
            TextButton(onClick = {
                viewPdf(context, medicalReport.pdfUrl)
                showSheet = false
            }) {
                Text("View")
            }
        }
    }
}

fun viewPdf(context: Context, pdfUrl: String) {
    val uri = Uri.parse(pdfUrl)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app found to open PDF", Toast.LENGTH_SHORT).show()
    }
}


fun downloadPdfOnly(context: Context, url: String) {


    val fileName = "report_${System.currentTimeMillis()}.pdf"
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadDir, fileName)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("PDF_DOWNLOAD", "Server returned HTTP ${connection.responseCode}")
                return@launch
            }

            val input = connection.inputStream
            val output = FileOutputStream(file)

            input.use { inputStream ->
                output.use { fileOut ->
                    inputStream.copyTo(fileOut)
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "PDF downloaded to Downloads folder", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Log.e("PDF_DOWNLOAD", "Download failed", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


/**
 * Top app bar composable for the medical report screen.
 *
 * Shows a title and a back button.
 *
 * @param goBack Lambda callback invoked when back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalReportTopBar(
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
                "View Medical Reports",
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
fun MedicalReportPreview() {
    CareConnectTheme {
        MedicalReportScreenContent(
            medicalReports = listOf(

            )
        )
    }
}
