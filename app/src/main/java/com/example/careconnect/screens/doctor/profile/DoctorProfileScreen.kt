package com.example.careconnect.screens.doctor.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun DoctorProfileScreen(
    viewModel: DoctorProfileViewModel = hiltViewModel(),
    openScheduleScreen: () -> Unit = {},
    goBack: () -> Unit = {}
){
    val doctor by viewModel.doctor.collectAsState()

    DoctorProfileScreenContent(
        doctor = doctor,
        onPhotoSelected = { uri -> viewModel.updateDoctorPhoto(uri) },
        openScheduleScreen = openScheduleScreen,
        goBack = goBack,
        onSaveDoctor = { updateDoctor -> viewModel.updateDoctor(updateDoctor) }
    )
}

@Composable
fun DoctorProfileScreenContent(
    doctor: Doctor? = null,
    onPhotoSelected: (Uri) -> Unit = {},
    openScheduleScreen: () -> Unit = {},
    goBack: () -> Unit = {},
    onSaveDoctor: (Doctor) -> Unit = {}
){
    val showEditDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        DoctorProfileTopBar(
            goBack = goBack
        )
        Column(
            modifier = Modifier.padding(top = 80.dp).fillMaxSize()
        ){
            if (doctor != null) {
                TopProfileLayout(
                    doctor = doctor,
                    onPhotoSelected = onPhotoSelected
                )
            }

            Column(
                modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier
                        .width(350.dp).height(50.dp).clickable{ showEditDialog.value = true },

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
                        .width(350.dp).height(50.dp).clickable{ openScheduleScreen() },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Change schedule"
                        )
                        Text(
                            text = "Change schedule",
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

    if (showEditDialog.value && doctor != null) {
        EditDoctorDialog(
            doctor = doctor,
            onDismiss = { showEditDialog.value = false },
            onSave = { updatedDoctor ->
                onSaveDoctor(updatedDoctor)
                showEditDialog.value = false
            }
        )
    }
}


@Composable
fun EditDoctorDialog(
    doctor: Doctor,
    onDismiss: () -> Unit,
    onSave: (Doctor) -> Unit
) {
    val name = remember { mutableStateOf(doctor.name) }
    val surname = remember { mutableStateOf(doctor.surname) }
    val specialization = remember { mutableStateOf(doctor.specialization) }
    val address = remember { mutableStateOf(doctor.address) }
    val phone = remember { mutableStateOf(doctor.phone) }

    val showConfirmDialog = remember { mutableStateOf(false) }

    if (showConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog.value = false },
            title = { Text("Confirm Changes") },
            text = { Text("Are you sure you want to save these changes?") },
            confirmButton = {
                Text(
                    "Yes",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onSave(
                                doctor.copy(
                                    name = name.value,
                                    surname = surname.value,
                                    specialization = specialization.value,
                                    address = address.value,
                                    phone = phone.value
                                )
                            )
                            showConfirmDialog.value = false
                        }
                )
            },
            dismissButton = {
                Text(
                    "Cancel",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showConfirmDialog.value = false }
                )
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text("Save", modifier = Modifier
                .padding(8.dp)
                .clickable { showConfirmDialog.value = true }
            )
        },
        dismissButton = {
            Text("Cancel", modifier = Modifier
                .padding(8.dp)
                .clickable { onDismiss() }
            )
        },
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("First Name") })
                OutlinedTextField(value = surname.value, onValueChange = { surname.value = it }, label = { Text("Last Name") })
                OutlinedTextField(value = specialization.value, onValueChange = { specialization.value = it }, label = { Text("Specialization") })
                OutlinedTextField(value = address.value, onValueChange = { address.value = it }, label = { Text("Address") })
                OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Phone") })
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileTopBar(
    goBack: () -> Unit = {}
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
                        "Profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
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

@Composable
fun TopProfileLayout(
    doctor: Doctor,
    onPhotoSelected: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onPhotoSelected(it)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = doctor.profilePhoto,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(55.dp)
                        .clickable{ launcher.launch("image/*") }
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = doctor.name + " " + doctor.surname,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = doctor.specialization,
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}



@Composable
@Preview
fun DoctorProfileScreenPreview(){
    CareConnectTheme {
        DoctorProfileScreenContent(
            doctor = Doctor(
                id = "1",
                name = "John",
                surname = "Doe",
                profilePhoto = "null",
                specialization = "Cardiologist"
            )
        )
    }
}