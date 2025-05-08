package com.example.careconnect.screens.doctor.patients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Surface
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
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch

@Composable
fun PatientsProfileScreen(
    patientId: String,
    viewModel: PatientsProfileViewModel = hiltViewModel(),
    openMedicalReportsScreen: (patientId: String) -> Unit = {},
    openChatScreen: (chatId: String, patientId: String, doctorId: String) -> Unit = {_, _, _ ->}
){
    LaunchedEffect(patientId) {
        viewModel.loadPatient(patientId)
    }

    val patient by viewModel.patient.collectAsStateWithLifecycle()
    val doctorId by viewModel.doctorId.collectAsStateWithLifecycle()

    PatientsProfileScreenContent(

        patientId = patientId,
        patient = patient,
        openMedicalReportsScreen = openMedicalReportsScreen,
        openChatScreen = { chatId ->
            doctorId?.let { openChatScreen(chatId, patientId, it) }
        },
        getChatId = {
            val doctor = viewModel.getCurrentDoctor()
            patient?.let { viewModel.getOrCreateChatRoomId(it, doctor) } ?: ""
        }
    )
}

@Composable
fun PatientsProfileScreenContent(
    patientId: String,
    patient: Patient? = null,
    openMedicalReportsScreen: (patientId: String) -> Unit = {},
    getChatId: suspend () -> String = {""},
    openChatScreen: (chatId: String) -> Unit = {}
){
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        SmallTopAppBarExample3()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize().padding(top = 90.dp),
            horizontalAlignment = CenterHorizontally
        ) {

            item {
                ElevatedCard(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
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
                            text = "Height: ${patient?.height}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = "Weight: ${patient?.weight}",
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
                        // Navigate to Medications Screen
                    }
                    MedicalCategoryCard(R.drawable.allergies, "Allergies") {
                        // Navigate to Allergies Screen
                    }
                    MedicalCategoryCard(R.drawable.conditions, "Conditions") {
                        // Navigate to Medical History Screen
                    }
                }
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MedicalCategoryCard(R.drawable.surgeries, "Surgeries") {
                        // Navigate to Immunizations Screen
                    }
                    MedicalCategoryCard(R.drawable.immunizations, "Immunizations") {
                        // Navigate to Immunizations Screen
                    }
                    MedicalCategoryCard(R.drawable.medical_report, "Medical Reports") {
                        openMedicalReportsScreen(patientId)
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
                        // Navigate to Medical History Screen
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
fun SmallTopAppBarExample3() {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier,
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
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ){
        Box(modifier = Modifier.padding(it))
    }
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

