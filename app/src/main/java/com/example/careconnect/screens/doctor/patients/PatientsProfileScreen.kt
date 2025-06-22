package com.example.careconnect.screens.doctor.patients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.dataclass.MedicalHistoryType
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch

@Composable
fun PatientsProfileScreen(
    patientId: String,
    viewModel: PatientsProfileViewModel = hiltViewModel(),
    openMedicalReportsScreen: (patientId: String) -> Unit = {},
    openPrescriptionsScreen: (patientId: String) -> Unit = {},
    onBack: () -> Unit = {},
    openChatScreen: (chatId: String, patientId: String, doctorId: String) -> Unit = {_, _, _ ->},
    openMedicalHistoryScreen: (patientId: String, sectionType: String) -> Unit = {_, _ ->}
){
    LaunchedEffect(patientId) {
        viewModel.loadPatient(patientId)
    }

    val patient by viewModel.patient.collectAsStateWithLifecycle()
    val doctorId = viewModel.loadDoctorId()

    PatientsProfileScreenContent(
        patientId = patientId,
        patient = patient,
        openMedicalReportsScreen = openMedicalReportsScreen,
        openPrescriptionsScreen = openPrescriptionsScreen,
        openChatScreen = { chatId ->
            doctorId?.let { openChatScreen(chatId, patientId, it) }
        },
        getChatId = {
            val doctor = viewModel.getCurrentDoctor()
            patient?.let { viewModel.getOrCreateChatRoomId(it, doctor) } ?: ""
        },
        openMedicalHistoryScreen = openMedicalHistoryScreen,
        onBack = onBack
    )
}

@Composable
fun PatientsProfileScreenContent(
    patientId: String,
    onBack: () -> Unit = {},
    patient: Patient? = null,
    openMedicalReportsScreen: (patientId: String) -> Unit = {},
    getChatId: suspend () -> String = {""},
    openChatScreen: (chatId: String) -> Unit = {},
    openPrescriptionsScreen: (patientId: String) -> Unit = {},
    openMedicalHistoryScreen: (patientId: String, sectionType: String) -> Unit = {_, _ ->}
){
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            PatientsProfileDoctorTopBar(
                onBack = onBack
            )
        }
    ){ paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize().padding(paddingValues),
            horizontalAlignment = CenterHorizontally
        ) {

            item {
                ElevatedCard(
                    modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                ) {
                    if (patient != null) {
                        Text(
                            text = patient.name + " " + patient.surname,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )

                        Text(
                            text = "Email: ${patient.email}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = "Phone number: ${patient.phone}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = "Address: ${patient.address}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Medical Information",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 20.dp, start = 16.dp)
                    )

                    ElevatedCard(
                        modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Gender: ${patient?.gender}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = "Height: ${patient?.height} cm",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = "Weight: ${patient?.weight} kg",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = "Date of Birth: ${patient?.dateOfBirth}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MedicalCategoryCard(R.drawable.medicine, "Medications") {
                        openMedicalHistoryScreen(patientId, MedicalHistoryType.MEDICATION.collectionName)
                        println("DEBUG: medications clicked")
                    }
                    MedicalCategoryCard(R.drawable.allergies, "Allergies") {
                        openMedicalHistoryScreen(patientId, MedicalHistoryType.ALLERGY.collectionName)
                        println("DEBUG: allergies clicked")
                    }
                    MedicalCategoryCard(R.drawable.conditions, "Conditions") {
                        openMedicalHistoryScreen(patientId, MedicalHistoryType.CONDITION.collectionName)
                        println("DEBUG: conditions clicked")
                    }
                }
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MedicalCategoryCard(R.drawable.surgeries, "Surgeries") {
                        openMedicalHistoryScreen(patientId, MedicalHistoryType.SURGERY.collectionName)
                        println("DEBUG: surgeries clicked")
                    }
                    MedicalCategoryCard(R.drawable.immunizations, "Immunizations") {
                        openMedicalHistoryScreen(patientId, MedicalHistoryType.IMMUNIZATION.collectionName)
                        println("DEBUG: immunizations clicked")
                    }
                    MedicalCategoryCard(R.drawable.medical_report, "Medical Reports") {
                        openMedicalReportsScreen(patientId)
                        println("DEBUG: medical reports clicked")
                    }

                }

            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MedicalCategoryCard(R.drawable.appointments, "Appointments") {
                        // Navigate to Medical History Screen
                    }
                    MedicalCategoryCard(R.drawable.prescriptions, "Prescriptions") {
                        openPrescriptionsScreen(patientId)
                    }
                    MedicalCategoryCard(R.drawable.chat, "Chat") {
                        coroutineScope.launch {
                            println("Chat button clicked")
                            val chatId = getChatId()
                            println("Obtained chatId: $chatId")
                            openChatScreen(chatId)
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsProfileDoctorTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = {
            Text(
                "Patient Profile",
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    tint = MaterialTheme.colorScheme.onPrimary,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}


@Preview
@Composable
fun PatientsProfileScreenPreview(){
    CareConnectTheme {
        PatientsProfileScreenContent(
            patientId = "1"
        )
    }
}

