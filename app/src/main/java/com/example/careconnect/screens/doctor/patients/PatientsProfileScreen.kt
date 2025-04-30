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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun PatientsProfileScreen(

){
    PatientsProfileScreenContent()
}

@Composable
fun PatientsProfileScreenContent(
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        SmallTopAppBarExample3()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = CenterHorizontally
        ) {
            item {
                ElevatedCard(
                    modifier = Modifier.padding(top = 85.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Name Surname",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = "Phone",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = "Address",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Text(
                    text = "Medical Information",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 20.dp)
                )

                ElevatedCard(
                    modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Gender",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = "Height",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = "Weight",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = "Date of Birth",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
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
                        // Navigate to Medical History Screen
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
                        // Navigate to Medical History Screen
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
        PatientsProfileScreenContent()
    }
}

