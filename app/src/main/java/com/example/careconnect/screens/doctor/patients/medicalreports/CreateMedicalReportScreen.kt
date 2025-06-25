package com.example.careconnect.screens.doctor.patients.medicalreports

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.dataclass.MedicalReport
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.screens.doctor.patients.SymptomsSection
import com.example.careconnect.screens.doctor.patients.TextFieldDoctor
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Composable screen for creating a new medical report.
 * Displays patient details, symptom input, and other report fields.
 * Handles submission and navigation upon success.
 *
 * @param patientId The ID of the patient for whom the report is being created.
 * @param viewModel ViewModel responsible for managing medical report creation logic.
 * @param navController Navigation controller used to navigate back on successful submission.
 */
@Composable
fun CreateMedicalReportScreen(
    patientId: String,
    viewModel: CreateMedicalReportViewModel = hiltViewModel(),
    navController: NavController
){
    val context = LocalContext.current

    LaunchedEffect(patientId) {
        viewModel.loadPatient(patientId)
    }

    val patient by viewModel.patient.collectAsStateWithLifecycle()
    val medicalReportCreated by viewModel.medicalReportCreated.collectAsStateWithLifecycle()

    LaunchedEffect(medicalReportCreated) {
        if (medicalReportCreated) {
            Toast.makeText(context, "Medical report submitted", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    CreateMedicalReportScreenContent(
        patientId = patientId,
        patient = patient,
        onCreateMedicalReport = viewModel::createMedicalReport
    )
}

/**
 * Composable content for the medical report creation screen.
 * Allows input for symptoms, diagnosis, prognosis, treatment, and care plan.
 *
 * @param patientId The ID of the patient being viewed.
 * @param patient Optional patient object to display personal details.
 * @param onCreateMedicalReport Callback invoked upon submitting the report.
 */
@Composable
fun CreateMedicalReportScreenContent(
    patientId: String,
    patient: Patient? = null,
    onCreateMedicalReport: (String, MedicalReport, Context) -> Unit
){
    var symptoms by rememberSaveable { mutableStateOf("") }
    val symptomsList = remember { mutableStateListOf<String>() }

    var diagnosis by remember { mutableStateOf("") }
    var prognosis by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var recommendations by remember { mutableStateOf("") }
    var planOfCare by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = "Create Medical Report",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )

                if (patient != null) {
                    Text(
                        text = "${patient.name} ${patient.surname}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(3.dp)
                    )

                    Text(
                        text = "Date of Birth: ${patient.dateOfBirth}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(3.dp)
                    )
                    Text(
                        text = "Gender: ${patient.gender}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(3.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                SymptomsSection(
                    symptomsList = symptomsList,
                    symptoms = symptoms,
                    onSymptomChange = { symptoms = it },
                    onAddSymptom = {
                        symptomsList.add(it)
                    },
                    onRemoveSymptom = {
                        symptomsList.remove(it)
                    }
                )
            }
        item {
            TextFieldDoctor(
                value = diagnosis,
                onValueChange = { diagnosis = it },
                modifier = Modifier,
                label = "Diagnosis"
            )
            TextFieldDoctor(
                value = prognosis,
                onValueChange = { prognosis = it },
                modifier = Modifier,
                label = "Prognosis"
            )
            TextFieldDoctor(
                value = treatment,
                onValueChange = { treatment = it },
                modifier = Modifier,
                label = "Treatment"
            )
            TextFieldDoctor(
                value = recommendations,
                onValueChange = { recommendations = it },
                modifier = Modifier,
                label = "Recommendations"
            )
            TextFieldDoctor(
                value = planOfCare,
                onValueChange = { planOfCare = it },
                modifier = Modifier,
                label = "Plan of Care"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
            item {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.width(200.dp).height(40.dp).padding(16.dp)
                ) {
                    Text("Submit medical report")
                }


                if (showDialog) {
                    AlertDialogExample(
                        onDismissRequest = { showDialog = false },
                        onConfirmation = {
                            showDialog = false

                            val medicalReport = MedicalReport(
                                patientId = patientId,
                                symptoms = symptomsList,
                                diagnosis = diagnosis,
                                prognosis = prognosis,
                                treatment = treatment,
                                recommendations = recommendations,
                                plan = planOfCare,
                                reportDate = com.google.firebase.Timestamp.now(),
                                reportPdfUrl = null
                            )

                            onCreateMedicalReport(patientId, medicalReport, context)
                        },
                        dialogTitle = "Submit medical report",
                        dialogText = "Are you sure you want to submit this medical report?",
                        icon = androidx.compose.material.icons.Icons.Default.Check
                    )
                }
            }

        }
    }
}

/**
 * Composable function that displays a confirmation dialog with title, text, and icon.
 * Includes confirm and dismiss actions.
 *
 * @param onDismissRequest Callback invoked when the dialog is dismissed.
 * @param onConfirmation Callback invoked when the confirm button is pressed.
 * @param dialogTitle The title text of the dialog.
 * @param dialogText The message content shown in the dialog.
 * @param icon The icon displayed in the dialog header.
 */
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

/**
 * Preview function for the [CreateMedicalReportScreenContent] composable.
 * Used in Android Studio to render a UI preview during design time.
 */
@Preview
@Composable
fun CreateMedicalReportScreenPreview(){
    CareConnectTheme {
        CreateMedicalReportScreenContent(
            patientId = "123",
            onCreateMedicalReport = { _, _ , _-> }
        )
    }
}