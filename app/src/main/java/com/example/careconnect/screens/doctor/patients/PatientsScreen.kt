package com.example.careconnect.screens.doctor.patients

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
   Surface(
       modifier = Modifier.fillMaxSize(),
       color = MaterialTheme.colorScheme.background
   ){
       PatientsListTopBar(
           onBack = onBack
       )

       Column(modifier = Modifier.padding(top = 90.dp, start = 16.dp, end = 16.dp).fillMaxSize()) {

           Spacer(modifier = Modifier.height(20.dp))


           FilledCardPatientsView(
               title = "Patients",
               patients = patientList,
               onEditClick = { patientId -> openPatientsProfile(patientId) },
               onDeleteProduct = { /* Handle product deletion */ }
           )
       }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsListTopBar(
    onBack: () -> Unit = {}
) {
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
        },
    ){
        Box(modifier = Modifier.padding(it))
    }
}

@Preview
@Composable
fun PatientsScreenPreview(){
    CareConnectTheme {
        PatientsScreenContent()
    }
}