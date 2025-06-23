package com.example.careconnect.screens.patient.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.careconnect.R
import com.example.careconnect.common.AppointmentCard
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Specialization
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch
import java.time.LocalDate


data class DoctorCarouselItem(
    val doctor: Doctor,
    val imageUrl: String,
    val specialization: String
)

data class MedicalHistoryQuickActionItem(
    val type: String,
    val label: String,
    val iconRes: Int
)

@Composable
fun HomeScreenPatient(
    openSettingsScreen: () -> Unit,
    openDoctorsOverviewScreen: (specialty: String) -> Unit,
    openDoctorProfileScreen: (doctorId: String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    openMedicalHistoryScreen: (type: String) -> Unit = {}
) {

    val doctorList by viewModel.doctorList.collectAsState()
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()

    HomeScreenPatientContent(
        uiState = HomeUiState(),
        openSettingsScreen = openSettingsScreen,
        onDoctorSelected = { _, _ -> },
        onSearchQueryChange = {},
        openDoctorsOverviewScreen = openDoctorsOverviewScreen,
        openDoctorProfileScreen = openDoctorProfileScreen,
        doctorList = doctorList,
        upcomingAppointments = upcomingAppointments,
        openMedicalHistoryScreen = openMedicalHistoryScreen
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenPatientContent(
    uiState: HomeUiState,
    openSettingsScreen: () -> Unit,
    onDoctorSelected: (Doctor, Boolean) -> Unit = { _, _ -> },
    onSearchQueryChange: (String) -> Unit = {},
    openDoctorsOverviewScreen: (specialty: String) -> Unit = {},
    openDoctorProfileScreen: (doctorId: String) -> Unit = {},
    doctorList: List<Doctor>,
    upcomingAppointments: List<Appointment> = emptyList(),
    openMedicalHistoryScreen: (type: String) -> Unit = {}

) {
    val randomDoctors = remember(doctorList) {
        doctorList.shuffled().take(5)
    }

    val doctorCarouselItems = remember(doctorList) {
        doctorList.shuffled().take(5).map {
            DoctorCarouselItem(
                doctor = it,
                imageUrl = if (it.profilePhoto.isNotBlank()) it.profilePhoto else "drawable://${R.drawable.carousel_image_1}",
                specialization = it.specialization
            )
        }
    }


    println("DEBUG$randomDoctors")

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded, // Starts in collapsed mode
        skipHiddenState = true // Prevents dismissal
    )
    val scope = rememberCoroutineScope()
    val date = LocalDate.now()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)

                ) {
                    Text(
                        text = stringResource(R.string.specialization),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(0.5f) // Expandable up to 50% screen height
                    ) {
                        items(Specialization.all()) { specialty ->
                            val displayName = specialty.displayName().toString()
                            Text(
                                text = displayName,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .clickable{
                                        openDoctorsOverviewScreen(displayName)
                                    }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            },
            sheetPeekHeight = 100.dp, // The visible height when collapsed
            scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Welcome back!",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Go to notifications screen
                    IconButton(
                        onClick = { scope.launch { sheetState.expand() } },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ){
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                    }

                    // Go to settings screen
                    IconButton(
                        onClick = { openSettingsScreen() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }

                Text(
                    text = "$date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))



                println("DEBUG: doctorCarouselItems.size = ${doctorCarouselItems.size}")

                println("DEBUG: ${randomDoctors.size}")
                if (doctorCarouselItems.isNotEmpty()) {
                    HorizontalMultiBrowseCarousel(
                        state = rememberCarouselState { doctorCarouselItems.size },
                        modifier = Modifier.width(412.dp).height(280.dp),
                        preferredItemWidth = 186.dp,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) { index ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openDoctorProfileScreen(doctorCarouselItems[index].doctor.id) }
                        ) {
                            Image(
                                modifier = Modifier
                                    .height(200.dp)
                                    .maskClip(MaterialTheme.shapes.extraLarge),
                                painter = rememberAsyncImagePainter(model = doctorCarouselItems[index].imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Text("Loading doctors...")
                }

                Text(
                    text = "Upcoming appointments",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Start).padding(16.dp)
                )

                println("DEBUG: upcomingAppointments = ${upcomingAppointments}")
                if (upcomingAppointments.isEmpty()) {
                    Text(
                        text = "No upcoming appointments",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    upcomingAppointments.forEach { appointment ->
                        AppointmentCard(
                            appt = appointment,
                            displayFields = listOf(
                                "Doctor" to { it.doctorName },
                                "Type" to { it.type },
                                "Address" to { it.address }
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                val quickActions = listOf(
                    MedicalHistoryQuickActionItem("MEDICATION", "Medication", R.drawable.medicine),
                    MedicalHistoryQuickActionItem("ALLERGY", "Allergy", R.drawable.allergies),
                    MedicalHistoryQuickActionItem("CONDITION", "Condition", R.drawable.conditions),
                    MedicalHistoryQuickActionItem("SURGERY", "Surgery", R.drawable.surgeries),
                    MedicalHistoryQuickActionItem("IMMUNIZATION", "Immunization", R.drawable.immunizations)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Your Medical History",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                MedicalHistoryQuickActionsCarousel(
                    actions = quickActions,
                    onActionClick = { type ->
                        openMedicalHistoryScreen(type)
                    }
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHistoryQuickActionsCarousel(
    actions: List<MedicalHistoryQuickActionItem>,
    onActionClick: (String) -> Unit
) {
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { actions.size },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        preferredItemWidth = 160.dp,
        itemSpacing = 12.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { index ->
        val item = actions[index]
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onActionClick(item.type) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(item.iconRes),
                    contentDescription = item.label,
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}


@Preview
@Composable
fun HomeScreenPatientPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        HomeScreenPatientContent(
            uiState = uiState,
            openSettingsScreen = {},
            onDoctorSelected = { _, _ -> },
            onSearchQueryChange = {},
            openDoctorsOverviewScreen = {},
            openDoctorProfileScreen = {},
            doctorList = listOf()
        )
    }
}