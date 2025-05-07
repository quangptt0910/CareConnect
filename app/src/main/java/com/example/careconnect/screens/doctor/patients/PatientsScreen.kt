package com.example.careconnect.screens.doctor.patients

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun PatientsScreen(
    viewModel: PatientsViewModel = hiltViewModel(),
    openPatientsProfile: (patientId: String) -> Unit = {}
){
    val patientList by viewModel.patientList.collectAsStateWithLifecycle(emptyList())
    PatientsScreenContent(
        patientList = patientList,
        openPatientsProfile = openPatientsProfile
    )
}

@Composable
fun PatientsScreenContent(
    patientList: List<Patient> = emptyList(),
    openPatientsProfile: (patientId: String) -> Unit = {}
){
   Surface(
       modifier = Modifier.fillMaxSize(),
       color = MaterialTheme.colorScheme.background
   ){
       Column(modifier = Modifier.padding(16.dp)) {

           Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween
           ) {
               Text(
                   text = stringResource(R.string.patients),
                   style = MaterialTheme.typography.headlineLarge,
                   modifier = Modifier
                       .align(Alignment.CenterVertically)
               )
           }

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



@Preview
@Composable
fun PatientsScreenPreview(){
    CareConnectTheme {
        PatientsScreenContent()
    }
}