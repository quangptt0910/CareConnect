package com.example.careconnect.screens.doctor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.NotificationData
import com.example.careconnect.NotificationType
import com.example.careconnect.data.datasource.AuthRemoteDataSource
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.doctor.appointments.DoctorAppointmentScreen
import com.example.careconnect.screens.doctor.home.DoctorHomeScreen
import com.example.careconnect.screens.doctor.navigation.BarRoutesDoctor
import com.example.careconnect.screens.doctor.navigation.BottomBarDoctor
import com.example.careconnect.screens.doctor.patients.PatientsProfileScreen
import com.example.careconnect.screens.doctor.patients.PatientsScreen
import com.example.careconnect.screens.doctor.patients.medicalhistory.MedicalHistorySectionScreen
import com.example.careconnect.screens.doctor.patients.medicalreports.CreateMedicalReportScreen
import com.example.careconnect.screens.doctor.patients.medicalreports.MedicalReportsScreen
import com.example.careconnect.screens.doctor.patients.prescriptions.CreatePrescriptionScreen
import com.example.careconnect.screens.doctor.patients.prescriptions.PrescriptionScreen
import com.example.careconnect.screens.doctor.profile.DoctorProfileScreen
import com.example.careconnect.screens.doctor.profile.ScheduleScreen
import com.example.careconnect.screens.patient.chat.ChatMenuScreen
import com.example.careconnect.screens.patient.chat.ChatScreen
import com.example.careconnect.screens.settings.NotificationSettingsScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.ui.navigation.Route.DOCTOR_CHAT_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_CREATE_MEDICAL_REPORT_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_CREATE_PRESCRIPTIONS_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_MEDICAL_HISTORY_SECTION_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_MEDICAL_REPORT_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_PRESCRIPTIONS_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_PROFILE_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_SCHEDULE_ROUTE
import com.example.careconnect.ui.navigation.Route.NOTIFICATION_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.getDoctorChatRoute
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsCreateMedicalReportRoute
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsCreatePrescriptionsRoute
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsMedicalHistorySectionRoute
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsMedicalReportRoute
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsPrescriptionsRoute
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsProfileRoute
import kotlinx.coroutines.flow.first

@Composable
fun DoctorApp(
    openSplashScreen: () -> Unit = {},
    showSnackBar: (SnackBarMessage) -> Unit = {},
    notificationData: NotificationData? = null,
    viewModel: DoctorAppViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    var currentDoctorId by remember { mutableStateOf<String?>(null) }

    // Get current user ID
    LaunchedEffect(Unit) {
        val user = viewModel.authRepository.currentUserFlow.first()
        if (user is AuthRemoteDataSource.UserData.DoctorData) {
            currentDoctorId = user.doctor.id
        }
    }

    // Handle notifications
    LaunchedEffect(notificationData, currentDoctorId) {
        notificationData?.let { data ->
            when (val type = data.type) {
                is NotificationType.Chat -> {
                    // Navigate to chat screen
                    navController.navigate(
                        getDoctorChatRoute(
                            patientId = type.senderId,
                            doctorId = type.recipientId,
                            chatId = type.chatId
                        )
                    )
                }
                is NotificationType.Appointment -> {
                    // Handle appointment notification if needed
                    navController.navigate(BarRoutesDoctor.FEED.route)
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomBarDoctor(
                tabs = BarRoutesDoctor.entries.toTypedArray(),
                navController = navController,
                navigateToRoute = { route -> navController.navigate(route) }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = BarRoutesDoctor.FEED.route,
            modifier = Modifier.padding(it)
        ) {
            composable(BarRoutesDoctor.FEED.route) {
                DoctorHomeScreen(
                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) },
                    openNotificationsScreen = { navController.navigate(NOTIFICATION_ROUTE) }
                )
            }
            composable(BarRoutesDoctor.CHAT.route) {
                ChatMenuScreen(
                    openChatScreen = { chatId, patientId, doctorId ->
                        navController.navigate(getDoctorChatRoute(doctorId, patientId, chatId))
                    },
                    onBack = { navController.popBackStack() },
                    openNotificationsScreen = { navController.navigate(NOTIFICATION_ROUTE) }
                )
            }

            composable(BarRoutesDoctor.APPOINTMENTS.route) {
                DoctorAppointmentScreen()
            }

            composable(BarRoutesDoctor.PATIENTS.route) {
                PatientsScreen(
                    openPatientsProfile = { patientId ->
                        navController.navigate(getDoctorPatientsProfileRoute(patientId)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(BarRoutesDoctor.PROFILE.route) {

                DoctorProfileScreen(
                    openScheduleScreen = { navController.navigate(DOCTOR_SCHEDULE_ROUTE) },
                    goBack = { navController.popBackStack() }
                )
            }

            composable(DOCTOR_SCHEDULE_ROUTE) {
                ScheduleScreen(
                    //openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) },
                    showSnackBar = showSnackBar
                )
            }

            composable(DOCTOR_PATIENTS_PROFILE_ROUTE) { backStackEntry ->
                PatientsProfileScreen(
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    openMedicalReportsScreen = { patientId ->
                        navController.navigate(getDoctorPatientsMedicalReportRoute(patientId))
                    },
                    openPrescriptionsScreen = { patientId ->
                        navController.navigate(getDoctorPatientsPrescriptionsRoute(patientId))
                    },
                    openChatScreen = { chatId, patientId, doctorId ->
                        navController.navigate(getDoctorChatRoute(chatId, patientId, doctorId))
                    },
                    openMedicalHistoryScreen = { patientId, section ->
                        navController.navigate(getDoctorPatientsMedicalHistorySectionRoute(patientId, section))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(DOCTOR_CHAT_ROUTE) { backStackEntry ->
                ChatScreen(
                    chatId = backStackEntry.arguments?.getString("chatId") ?: "",
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    doctorId = backStackEntry.arguments?.getString("doctorId") ?: "",
                    openChatScreen = { chatId, patientId, doctorId ->
                        navController.navigate(getDoctorChatRoute(chatId, patientId, doctorId))
                    },
                    goBack = { navController.popBackStack() }
                )
            }

            composable(DOCTOR_PATIENTS_MEDICAL_REPORT_ROUTE) { backStackEntry ->
                MedicalReportsScreen(
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    openCreateMedicalReportScreen = { patientId ->
                        navController.navigate(getDoctorPatientsCreateMedicalReportRoute(patientId))
                    },
                    goBack = { navController.popBackStack() }
                )
            }

            composable(DOCTOR_PATIENTS_CREATE_MEDICAL_REPORT_ROUTE) { backStackEntry ->
                CreateMedicalReportScreen(
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    navController = navController
                )
            }

            composable(DOCTOR_PATIENTS_PRESCRIPTIONS_ROUTE) { backStackEntry ->
                PrescriptionScreen(
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    openCreatePrescriptionsScreen = { patientId ->
                        navController.navigate(getDoctorPatientsCreatePrescriptionsRoute(patientId))
                    },
                    goBack = { navController.popBackStack() }
                )
            }

            composable(DOCTOR_PATIENTS_CREATE_PRESCRIPTIONS_ROUTE) { backStackEntry ->
                CreatePrescriptionScreen(
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    navController = navController
                )
            }

            composable(DOCTOR_PATIENTS_MEDICAL_HISTORY_SECTION_ROUTE) { backStackEntry ->
                MedicalHistorySectionScreen(
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    sectionType = backStackEntry.arguments?.getString("sectionType") ?: "",
                    onBack = { navController.popBackStack() },
                    showSnackbar = showSnackBar
                )
            }

            composable(SETTINGS_ROUTE) {
                SettingsScreen(
                    openSplashScreen = openSplashScreen,
                    showSnackBar = showSnackBar
                )
            }

            composable(NOTIFICATION_ROUTE) {
                NotificationSettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
