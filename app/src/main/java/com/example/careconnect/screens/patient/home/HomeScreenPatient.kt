package com.example.careconnect.screens.patient.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import java.time.LocalDate

/**
 * Data class representing an item in the doctor carousel.
 *
 * @property doctor The doctor represented by this carousel item.
 * @property imageUrl URL or resource path for the doctor's image.
 * @property specialization The doctor's specialization as a string.
 */
data class DoctorCarouselItem(
    val doctor: Doctor,
    val imageUrl: String,
    val specialization: String
)

/**
 * Data class representing a quick action item for medical history.
 *
 * @property type The type identifier of the medical history item (e.g., "MEDICATION").
 * @property label The display label for the action.
 * @property iconRes The resource ID of the icon representing the action.
 */
data class MedicalHistoryQuickActionItem(
    val type: String,
    val label: String,
    val iconRes: Int
)

/**
 * The main home screen composable for patients.
 *
 * Displays welcome message, doctor carousel, upcoming appointments, and medical history quick actions.
 *
 * @param openSettingsScreen Lambda to navigate to the settings screen.
 * @param openDoctorsOverviewScreen Lambda to open the doctors overview filtered by specialty.
 * @param openDoctorProfileScreen Lambda to open an individual doctor's profile by ID.
 * @param viewModel The ViewModel providing doctors and appointments data.
 * @param openMedicalHistoryScreen Lambda to open medical history details by type.
 * @param openNotificationsScreen Lambda to open the notifications screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenPatient(
    openSettingsScreen: () -> Unit,
    openDoctorsOverviewScreen: (specialty: String) -> Unit,
    openDoctorProfileScreen: (doctorId: String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    openMedicalHistoryScreen: (type: String) -> Unit = {},
    openNotificationsScreen: () -> Unit = {},
    openChatbotScreen: () -> Unit = {}
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
        openMedicalHistoryScreen = openMedicalHistoryScreen,
        openNotificationsScreen = openNotificationsScreen,
        openChatbotScreen = openChatbotScreen
    )

}

/**
 * The content composable for the patient home screen.
 *
 * Displays UI elements such as welcome text, doctor carousel, upcoming appointments, medical history quick actions,
 * and a bottom sheet with a list of specializations.
 *
 * @param uiState UI state for search and selection (currently unused in this implementation).
 * @param openSettingsScreen Lambda to open settings.
 * @param onDoctorSelected Lambda invoked when a doctor is selected or deselected.
 * @param onSearchQueryChange Lambda invoked when search query changes.
 * @param openDoctorsOverviewScreen Lambda to open doctors overview filtered by specialty.
 * @param openDoctorProfileScreen Lambda to open individual doctor profile.
 * @param doctorList List of doctors to display.
 * @param upcomingAppointments List of upcoming appointments to display.
 * @param openMedicalHistoryScreen Lambda to open medical history by type.
 * @param openNotificationsScreen Lambda to open notifications.
 */
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
    openMedicalHistoryScreen: (type: String) -> Unit = {},
    openNotificationsScreen: () -> Unit = {},
    openChatbotScreen: () -> Unit
) {

    val doctorCarouselItems = remember(doctorList) {
        doctorList.shuffled().take(5).map {
            DoctorCarouselItem(
                doctor = it,
                imageUrl = it.profilePhoto.ifBlank { "drawable://${R.drawable.carousel_image_1}" },
                specialization = it.specialization
            )
        }
    }

    val specializationSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )


    val scope = rememberCoroutineScope()
    val date = LocalDate.now()

    Box(
        modifier = Modifier.fillMaxSize(),
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
                        modifier = Modifier.fillMaxHeight(0.5f)
                    ) {
                        items(Specialization.all()) { specialty ->
                            val displayName = specialty.displayName().toString()
                            Text(
                                text = displayName,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .clickable {
                                        openDoctorsOverviewScreen(displayName)
                                    }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            },
            sheetPeekHeight = 100.dp,
            scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = specializationSheetState),
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Welcome back!",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Row {
                            IconButton(
                                onClick = { openNotificationsScreen() },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                            }

                            IconButton(
                                onClick = { openSettingsScreen() },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        }
                    }

                    Text(
                        text = "$date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    if (doctorCarouselItems.isNotEmpty()) {
                        HorizontalMultiBrowseCarousel(
                            state = rememberCarouselState { doctorCarouselItems.size },
                            modifier = Modifier
                                .width(412.dp)
                                .height(280.dp),
                            preferredItemWidth = 186.dp,
                            itemSpacing = 8.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) { index ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        openDoctorProfileScreen(doctorCarouselItems[index].doctor.id)
                                    }
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
                }

                item {
                    Text(
                        text = "Upcoming appointments",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                if (upcomingAppointments.isEmpty()) {
                    item {
                        Text(
                            text = "No upcoming appointments",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    items(upcomingAppointments) { appointment ->
                        AppointmentCard(
                            appt = appointment,
                            displayFields = listOf(
                                "Doctor" to { it.doctorName },
                                "Type" to { it.type },
                                "Address" to { it.address }
                            ),
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "Your Medical History",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                }

                item {
                    MedicalHistoryQuickActionsCarousel(
                        actions = listOf(
                            MedicalHistoryQuickActionItem("MEDICATION", "Medication", R.drawable.medicine),
                            MedicalHistoryQuickActionItem("ALLERGY", "Allergy", R.drawable.allergies),
                            MedicalHistoryQuickActionItem("CONDITION", "Condition", R.drawable.conditions),
                            MedicalHistoryQuickActionItem("SURGERY", "Surgery", R.drawable.surgeries),
                            MedicalHistoryQuickActionItem("IMMUNIZATION", "Immunization", R.drawable.immunizations)
                        ),
                        onActionClick = openMedicalHistoryScreen
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { openChatbotScreen() },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chatbot")
            }
        }


    }
}

/**
 * Displays a horizontal carousel of quick action items related to medical history.
 *
 * @param actions List of medical history quick action items to display.
 * @param onActionClick Lambda invoked when a quick action is clicked, passing the type of action.
 */
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
            .height(80.dp),
        preferredItemWidth = 170.dp,
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

/**
 * Preview composable for the HomeScreenPatientContent.
 */
@Preview(showBackground = true)
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
            doctorList = listOf(),
            openChatbotScreen = {}
        )
    }
}