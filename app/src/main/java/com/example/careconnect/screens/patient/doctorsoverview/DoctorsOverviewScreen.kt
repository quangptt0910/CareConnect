package com.example.careconnect.screens.patient.doctorsoverview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun DoctorsOverviewScreen(
    specialty: String,
    openBookingScreen: (doctorId: String) -> Unit = {},
    openDoctorProfileScreen: (doctorId: String) -> Unit = {},
    viewModel: DoctorsOverviewViewModel = hiltViewModel()
){
    LaunchedEffect(specialty) {
        viewModel.setSpecialty(specialty)
    }

    val doctors by viewModel.doctors.collectAsStateWithLifecycle()

    DoctorsOverviewScreenContent(
        doctors = doctors,
        specialty = specialty,
        openBookingScreen = openBookingScreen,
        openDoctorProfileScreen = openDoctorProfileScreen
    )
}


@Composable
fun DoctorsOverviewScreenContent(
    doctors: List<Doctor> = emptyList(),
    specialty: String,
    openBookingScreen: (doctorId: String) -> Unit = {},
    openDoctorProfileScreen: (doctorId: String) -> Unit = {}
) {

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

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 70.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                text = "$specialty Doctors",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            if (doctors.isEmpty()) {
                // Handle no matches found
               Text(
                    text = "No doctors found for \"$specialty\"",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                doctors.forEach { doctor ->
                    FilledCardExample(
                        modifier = Modifier,
                        name = doctor.name,
                        speciality = doctor.specialization,
                        address = doctor.address,
                        doctorId = doctor.id,
                        imageRes = doctor.profilePhoto,
                        openBookingScreen = { openBookingScreen(doctor.id) },
                        openDoctorProfileScreen = { openDoctorProfileScreen(doctor.id) }
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}


@Preview
@Composable
fun DoctorsOverviewScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        val doctors = listOf(
            Doctor(
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
            Doctor(
                id = "2",
                name = "Lily",
                surname = "Jones",
                email = "lily.jones@example.com",
                phone = "+987654321",
                address = "456 Heart Ave",
                specialization = "Cardiologist",
                experience = 2015,
                profilePhoto = "https://example.com/images/doctor2.jpg"
            ),
            Doctor(
                id = "3",
                name = "Emily",
                surname = "Rose",
                email = "emily.rose@example.com",
                phone = "+192837465",
                address = "789 Skin Blvd",
                specialization = "Dermatologist",
                experience = 2012,
                profilePhoto = "https://example.com/images/doctor3.jpg"
            ),
            Doctor(
                id = "4",
                name = "Michael",
                surname = "West",
                email = "michael.west@example.com",
                phone = "+109283746",
                address = "321 Child St",
                specialization = "Pediatrician",
                experience = 2008,
                profilePhoto = "https://example.com/images/doctor4.jpg"
            ),
            Doctor(
                id = "5",
                name = "Sarah",
                surname = "Lee",
                email = "sarah.lee@example.com",
                phone = "+5647382910",
                address = "654 Brain Road",
                specialization = "Neurologist",
                experience = 2013,
                profilePhoto = "https://example.com/images/doctor5.jpg"
            )
        )

        DoctorsOverviewScreenContent(
            doctors = doctors,
            specialty = "Cardiologist"
        )
    }
}