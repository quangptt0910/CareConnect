package com.example.careconnect.screens.patient.doctorsoverview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch


@Composable
fun DoctorsProfileViewScreen(
    doctorId: String,
    viewModel: DoctorsProfileViewModel = hiltViewModel(),
    openChatScreen: (chatId: String, patientId: String, doctorId: String) -> Unit = {_, _ , _->},
    openBookingScreen: (doctorId: String) -> Unit = {},
    goBack: () -> Unit = {}
){
    LaunchedEffect(doctorId) {
        viewModel.setDoctorId(doctorId)
    }

    val coroutineScope = rememberCoroutineScope()
    val doctor by viewModel.doctor.collectAsState()
    //val patientId by viewModel.patientId.collectAsState()
    println("DoctorsProfileViewScreen: doctorId=$doctorId, doctor=$doctor")

    doctor?.let {
        DoctorsProfileViewScreenContent(
            doctor = it,
            doctorId = doctorId,
            getChatId = {
                viewModel.getCurrentPatient().let { patient ->
                    viewModel.getOrCreateChatRoomId(patient, it)
                }
            },

            openChatScreen = { chatId ->
                coroutineScope.launch {
                    openChatScreen(chatId, viewModel.getCurrentPatient().id, doctorId)
                }
            },
            openBookingScreen = {
                openBookingScreen(doctorId)
            },
            addPatient = {
                viewModel.addPatient(doctorId)
            },
            goBack = goBack
        )
    }
}


@Composable
fun DoctorsProfileViewScreenContent(
    doctor: Doctor,
    doctorId: String,
    getChatId: suspend () -> String,
    openBookingScreen: (doctorId: String) -> Unit = {},
    openChatScreen: (chatId: String) -> Unit = {},
    addPatient: () -> Unit,
    goBack: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DoctorsOverviewTopBar(
                goBack = goBack
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = rememberAsyncImagePainter(doctor.profilePhoto),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.padding(top = 43.dp)
                    .size(160.dp)
                    .clip(CircleShape)
            )

        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ){
            Text(
                text = "Dr. ${doctor.name} ${doctor.surname}",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = doctor.specialization,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Experience since: ${doctor.experience}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(25.dp))
        }



        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 440.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = "Located: ${doctor.address}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Phone: ${doctor.phone}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Email: ${doctor.email}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Chat with me",
                    icon = Icons.Outlined.ChatBubbleOutline,
                    onClick = {
                        addPatient()
                        coroutineScope.launch {
                            val chatId = getChatId()

                            openChatScreen(chatId)
                        }
                    }
                )

                ActionButton(
                    text = "Book Appointment",
                    icon = Icons.Outlined.CalendarMonth,
                    onClick = {
                        addPatient()
                        openBookingScreen(doctorId)
                    }
                )
            }
        }
    }
}




@Preview
@Composable
fun DoctorsProfileViewScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        DoctorsProfileViewScreenContent(
            doctorId = "1",
            doctor = Doctor(
                id = "1",
                name = "John",
                surname = "Smith",
                email = "john.smith@example.com",
                phone = "+123456789",
                address = "123 Drive",
                specialization = "Cardiologist",
                experience = 2010,
                profilePhoto = "https://example.com/images/doctor1.jpg"
            ),
            getChatId = { "123" },
            openBookingScreen = {},
            addPatient = {},
            openChatScreen = {}
        )
    }
}