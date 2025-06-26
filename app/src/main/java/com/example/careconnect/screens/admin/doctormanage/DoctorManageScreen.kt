package com.example.careconnect.screens.admin.doctormanage

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Role
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch

/**
 * Screen composable to manage the list of doctors in the admin interface.
 *
 * This screen fetches and displays all doctors, showing each doctor in a card with options to edit or delete.
 * It also provides a floating action button to navigate to the screen to add a new doctor.
 *
 * @param openAddDoctorScreen Lambda function triggered when the user clicks the add doctor button.
 * @param viewModel ViewModel to manage doctors data and state.
 * @param showSnackBar Function to display snackbar messages, e.g., on errors or actions.
 */
@Composable
fun DoctorManageScreen(
    openAddDoctorScreen: () -> Unit,
    viewModel: DoctorManageViewModel = hiltViewModel(),
    showSnackBar: (SnackBarMessage) -> Unit
){
    val allDoctors by viewModel.allDoctors.collectAsStateWithLifecycle(emptyList())
    println("DEBUG:: DoctorManageScreen: doctorsList = $allDoctors")
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.loadDoctors()
    }

    DoctorManageScreenContent(
        openAddDoctorScreen = openAddDoctorScreen,
        doctors = allDoctors,
        deleteDoctor = { doctor -> scope.launch{viewModel.deleteDoctor(doctor) }}
    )
}

/**
 * Composable to render the UI content of the Doctor Manage Screen.
 *
 * Displays a list of doctors with a title and a floating action button to add new doctors.
 *
 * @param openAddDoctorScreen Callback triggered when the add doctor button is clicked.
 * @param doctors List of doctors to display.
 */
@Composable
fun DoctorManageScreenContent(
    openAddDoctorScreen: () -> Unit = {},
    doctors: List<Doctor>,
    deleteDoctor: (Doctor) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.doctors),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
            LazyColumn {
                items(doctors) { doctor ->
                    DoctorCard(doctor = doctor, onOpenProfile = {}, onDeleteDoc = { deleteDoctor(doctor)})
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
//
//            FilledCardStats(
//                title = "Total hours worked",
//                userProducts = doctors,
//                onDeleteProduct = {}
//            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            FloatingActionButton(
                onClick = openAddDoctorScreen,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(60.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Doctor")
            }
        }

    }
}


/**
 * Preview for the DoctorManageScreenContent composable with sample data.
 */
@Preview(showBackground = true)
@Composable
fun DoctorManageScreenPreview() {
    val doctors = listOf(
        Doctor(
            id = "doctor123",
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            role = Role.DOCTOR,
            phone = "123-456-7890",
            address = "123 Medical St, Health City",
            specialization = "Cardiology",
            experience = 2015,
        ),
        Doctor(
            id = "doctor456",
            name = "Jane",
            surname = "Smith",
            email = "jane.smith@example.com",
            role = Role.DOCTOR,
            phone = "987-654-3210",
            address = "456 Health Rd, Cityville",
            specialization = "Dermatology",
            experience = 2010,
        )
    )
    CareConnectTheme {
        DoctorManageScreenContent(
            doctors = doctors
        )

    }

}