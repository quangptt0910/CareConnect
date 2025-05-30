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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.careconnect.screens.patient.chat.SmallTopAppBarExample1
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun DoctorProfileScreen(
    viewModel: DoctorProfileViewModel = hiltViewModel(),
    openScheduleScreen: () -> Unit = {}
){
    val doctor by viewModel.doctor.collectAsState()

    DoctorProfileScreenContent(
        doctor = doctor,
        onPhotoSelected = { uri -> viewModel.updateDoctorPhoto(uri) },
        openScheduleScreen = openScheduleScreen
    )
}

@Composable
fun DoctorProfileScreenContent(
    doctor: Doctor? = null,
    onPhotoSelected: (Uri) -> Unit = {},
    openScheduleScreen: () -> Unit = {}
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        SmallTopAppBarExample1()
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample1() {
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
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {  // <-- Add actions here (right side of the TopAppBar)
                    Spacer(modifier = Modifier.width(100.dp))  // Add spacing before the icon
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.Outlined.Notifications,
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