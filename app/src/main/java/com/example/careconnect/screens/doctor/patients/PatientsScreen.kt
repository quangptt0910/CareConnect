package com.example.careconnect.screens.doctor.patients

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun PatientsScreen(
    viewModel: PatientsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    openPatientsProfile: (patientId: String) -> Unit = {}
){
    val patientList by viewModel.patientList.collectAsStateWithLifecycle(emptyList())
    PatientsScreenContent(
        patientList = patientList,
        onBack = onBack,
        openPatientsProfile = openPatientsProfile
    )
}

@Composable
fun PatientsScreenContent(
    patientList: List<Patient> = emptyList(),
    onBack: () -> Unit = {},
    openPatientsProfile: (patientId: String) -> Unit = {}
){
   Scaffold(
       topBar = {
           PatientsListTopBar(
               onBack = onBack
           )
       }
   ){ paddingValues ->

       LazyColumn(
           modifier = Modifier
               .fillMaxSize()
               .padding(paddingValues)
       ) {
           item {
               Column(
                   modifier = Modifier.padding(16.dp)
               ){
                   FilledCardPatientsView(
                       title = "Patients",
                       patients = patientList,
                       onEditClick = { patientId -> openPatientsProfile(patientId) },
                       onDeleteProduct = { /* Handle product deletion */ }
                   )
               }

           }

       }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsListTopBar(
    onBack: () -> Unit = {}
) {
            TopAppBar(
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(
                        "Patients",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
}

@Preview
@Composable
fun PatientsScreenPreview(){
    CareConnectTheme {
        PatientsScreenContent()
    }
}