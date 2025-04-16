package com.example.careconnect.screens.patient.doctorsoverview

import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun DoctorsOverviewScreen(
    specialty: String,
    openBookingScreen: () -> Unit = {},
    openDoctorProfileScreen: () -> Unit = {}
){
    DoctorsOverviewScreenContent(
        specialty = specialty,
        openBookingScreen = openBookingScreen,
        openDoctorProfileScreen = openDoctorProfileScreen
    )
}


@Composable
fun DoctorsOverviewScreenContent(
    specialty: String,
    openBookingScreen: () -> Unit = {},
    openDoctorProfileScreen: () -> Unit = {}
) {
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



    val filteredDoctors = doctors.filter { it.specialization.equals(specialty, ignoreCase = true) }

    Log.d("DoctorsOverview", "Filtered Doctors: $filteredDoctors")
    Log.d("DoctorsOverview", "Specialty: $specialty")

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
            modifier = Modifier.fillMaxWidth().padding(top = 100.dp)
        ) {

            if (filteredDoctors.isEmpty()) {
                // Handle no matches found
                androidx.compose.material3.Text(
                    text = "No doctors found for \"$specialty\"",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                filteredDoctors.forEach { doctor ->
                    FilledCardExample(
                        modifier = Modifier,
                        name = doctor.name,
                        speciality = doctor.specialization,
                        address = doctor.address,
                        imageRes = R.drawable.carousel_image_1,
                        openBookingScreen = openBookingScreen,
                        openDoctorProfileScreen = openDoctorProfileScreen
                    )

                    Spacer(modifier = Modifier.height(20.dp))
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
        DoctorsOverviewScreenContent(
            specialty = "Cardiologist"
        )
    }
}