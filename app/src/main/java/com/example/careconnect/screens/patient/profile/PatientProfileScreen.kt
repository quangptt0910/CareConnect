package com.example.careconnect.screens.patient.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun PatientProfileScreen(
    openPrescriptionsScreen: () -> Unit = {},
    openMedicalReportsScreen: () -> Unit = {},
    openMedicalHistoryScreen: (type: String) -> Unit = {}
){
    PatientProfileScreenContent(
        openPrescriptionScreen = openPrescriptionsScreen,
        openMedicalReportsScreen = openMedicalReportsScreen,
        openMedicalHistoryScreen = openMedicalHistoryScreen
    )
}

@Composable
fun PatientProfileScreenContent(
    openPrescriptionScreen: () -> Unit = {},
    openMedicalReportsScreen: () -> Unit = {},
    openMedicalHistoryScreen: (type: String) -> Unit = {}
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){


        Column(
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .width(350.dp).height(50.dp),

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Edit Profile"
                    )
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowForwardIos,
                        contentDescription = "Go",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .width(350.dp).height(50.dp).clickable{ openPrescriptionScreen() },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContactPage,
                        contentDescription = "View Prescriptions"
                    )
                    Text(
                        text = "View Prescriptions",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowForwardIos,
                        contentDescription = "Go",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .width(350.dp).height(50.dp).clickable{ openMedicalReportsScreen() },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "View Medical Reports"
                    )
                    Text(
                        text = "View Medical Reports",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowForwardIos,
                        contentDescription = "Go",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .width(350.dp).height(50.dp).clickable{ openMedicalHistoryScreen("MEDICATION") },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "View Medical History"
                    )
                    Text(
                        text = "View Medical History",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowForwardIos,
                        contentDescription = "Go",
                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PatientProfilePreview(){
    CareConnectTheme {
        PatientProfileScreenContent()
    }
}