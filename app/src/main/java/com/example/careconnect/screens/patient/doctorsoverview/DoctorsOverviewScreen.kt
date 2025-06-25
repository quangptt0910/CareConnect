package com.example.careconnect.screens.patient.doctorsoverview

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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


/**
 * Main screen composable displaying a list of doctors filtered by specialty.
 * Fetches the doctor list from the provided [viewModel].
 *
 * @param specialty The medical speciality to filter doctors by.
 * @param openBookingScreen Lambda to navigate to the booking screen for a given doctor ID.
 * @param openDoctorProfileScreen Lambda to navigate to the doctor's profile screen.
 * @param goBack Lambda to navigate back from this screen.
 * @param viewModel ViewModel managing the doctors data and filtering logic.
 */
@Composable
fun DoctorsOverviewScreen(
    specialty: String,
    openBookingScreen: (doctorId: String) -> Unit = {},
    openDoctorProfileScreen: (doctorId: String) -> Unit = {},
    goBack: () -> Unit,
    viewModel: DoctorsOverviewViewModel = hiltViewModel()
){
    LaunchedEffect(specialty) {
        viewModel.setSpecialty(specialty)
    }

    val doctors by viewModel.doctors.collectAsStateWithLifecycle()

    DoctorsOverviewScreenContent(
        doctors = doctors,
        specialty = specialty,
        goBack = goBack,
        openBookingScreen = openBookingScreen,
        openDoctorProfileScreen = openDoctorProfileScreen
    )
}


/**
 * UI content composable for the Doctors Overview screen showing
 * a top bar and a list of doctors. Handles empty state if no doctors
 * are found for the specified specialty.
 *
 * @param doctors List of doctors to display.
 * @param specialty The medical specialty being viewed.
 * @param goBack Lambda to handle back navigation.
 * @param openBookingScreen Lambda to open booking screen for a doctor.
 * @param openDoctorProfileScreen Lambda to open doctor's profile screen.
 */
@Composable
fun DoctorsOverviewScreenContent(
    doctors: List<Doctor> = emptyList(),
    specialty: String,
    goBack: () -> Unit = {},
    openBookingScreen: (doctorId: String) -> Unit = {},
    openDoctorProfileScreen: (doctorId: String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            DoctorsOverviewTopBar(goBack = goBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // this prevents overlap with topBar
        ) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                    text = "$specialty Doctors",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }

            if (doctors.isEmpty()) {
                item {
                    Text(
                        text = "No doctors found for \"$specialty\"",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(doctors) { doctor ->
                    FilledCardExample(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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


/**
 * Preview composable for the Doctors Overview screen,
 * showing a sample list of doctors to assist in UI development.
 */
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