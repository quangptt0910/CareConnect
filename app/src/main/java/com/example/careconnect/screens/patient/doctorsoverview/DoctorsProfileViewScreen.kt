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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch


@Composable
fun DoctorsProfileViewScreen(
    doctorId: String,
    viewModel: DoctorsProfileViewModel = hiltViewModel(),
    openChatScreen: (chatId: String, patientId: String, doctorId: String) -> Unit = {_, _ , _->}
){
    LaunchedEffect(doctorId) {
        viewModel.setDoctorId(doctorId)
    }

    val doctor by viewModel.doctor.collectAsState()
    val patientId by viewModel.patientId.collectAsState()

    doctor?.let {
        DoctorsProfileViewScreenContent(
        doctor = it,
        doctorId = doctorId,
            openChatScreen = { chatId -> // <-- accept chatId from the inner layer
                patientId?.let { it1 -> openChatScreen(chatId, it1, doctorId) }
            },
            getChatId = {
                viewModel.getCurrentPatient().let { patient ->
                    viewModel.getOrCreateChatRoomId(patient, it)
                }
            }
    )
    }
}


@Composable
fun DoctorsProfileViewScreenContent(
    doctor: Doctor,
    doctorId: String,
    getChatId: suspend () -> String,
    openChatScreen: (chatId: String) -> Unit = {}
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
            SmallTopAppBarExample2()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Half-moon shape (flat top, curved bottom)
//            Canvas(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .size(200.dp)
//            ) {
//                val arcHeight = size.height * 2
//
//                drawArc(
//                    color = Color(0xFF004C6A), // Light blue
//                    startAngle = 0f,           // Flat on top
//                    sweepAngle = 180f,         // Half circle
//                    useCenter = true,
//                    topLeft = Offset(0f, -size.height),
//                    size = Size(size.width, arcHeight)
//                )
//            }

            // Image in front of the arc
            // Todo()
            Image(
                painter = painterResource(id = R.drawable.carousel_image_1),
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                        val chatId = getChatId()
                        openChatScreen(chatId)
                    } },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(50.dp)
                ) {
                    Image(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Chat with me",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp)
                )

                Spacer(modifier = Modifier.width(15.dp))

                IconButton(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(50.dp)
                ) {
                    Image(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Book an appointment",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp)
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
            openChatScreen = {}
        )
    }
}