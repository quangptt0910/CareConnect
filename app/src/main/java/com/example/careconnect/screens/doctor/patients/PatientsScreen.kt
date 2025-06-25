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


/**
 * Composable screen displaying a list of patients assigned to the current doctor.
 *
 * This screen observes the [PatientsViewModel] for the list of patients and
 * allows navigation to individual patient profiles.
 *
 * @param viewModel ViewModel to provide patient data and business logic, injected by Hilt by default.
 * @param onBack Callback invoked when the back button is pressed.
 * @param openPatientsProfile Callback to navigate to a patient's detailed profile screen. Receives the patient's ID.
 */
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

/**
 * Composable that renders the UI content of the patients list screen.
 *
 * Displays a list of patients inside a [LazyColumn] with a top app bar.
 *
 * @param patientList List of patients to display.
 * @param onBack Callback invoked when the back button is pressed.
 * @param openPatientsProfile Callback to navigate to a patient's profile. Receives the patient's ID.
 */
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

/**
 * Top app bar for the patients list screen.
 *
 * Displays the screen title and a back navigation button.
 *
 * @param onBack Callback invoked when the back button is pressed.
 */
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