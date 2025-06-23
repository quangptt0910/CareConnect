package com.example.careconnect.screens.patient

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.NotificationData
import com.example.careconnect.NotificationType
import com.example.careconnect.data.datasource.AuthRemoteDataSource
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.patient.appointment.BookAppointmentScreen
import com.example.careconnect.screens.patient.appointment.PatientAppointmentScreen
import com.example.careconnect.screens.patient.chat.ChatMenuScreen
import com.example.careconnect.screens.patient.chat.ChatScreen
import com.example.careconnect.screens.patient.doctorsoverview.DoctorsOverviewScreen
import com.example.careconnect.screens.patient.doctorsoverview.DoctorsProfileViewScreen
import com.example.careconnect.screens.patient.home.HomeScreenPatient
import com.example.careconnect.screens.patient.navigation.BarRoutes
import com.example.careconnect.screens.patient.navigation.BottomBar
import com.example.careconnect.screens.patient.profile.PatientProfileScreen
import com.example.careconnect.screens.patient.profile.medicalhistory.PatientMedicalHistoryScreen
import com.example.careconnect.screens.patient.profile.medicalreport.MedicalReportScreen
import com.example.careconnect.screens.patient.profile.prescription.PrescriptionScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.ui.navigation.Route.PATIENT_BOOKING_APPOINTMENTS_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_CHAT_MENU_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_CHAT_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_PROFILE_MEDICAL_HISTORY_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_PROFILE_MEDICAL_REPORT_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_PROFILE_PRESCRIPTION_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.getPatientBookingAppointmentsBookRoute
import com.example.careconnect.ui.navigation.Route.getPatientChatRoute
import com.example.careconnect.ui.navigation.Route.getPatientDoctorsOverviewRoute
import com.example.careconnect.ui.navigation.Route.getPatientDoctorsProfileRoute
import kotlinx.coroutines.flow.first

@Composable
fun PatientApp(
    openSplashScreen: () -> Unit = {},
    showSnackBar: (SnackBarMessage) -> Unit = {},
    notificationData: NotificationData? = null,
    viewModel: PatientAppViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val navController = rememberNavController()
    var currentPatientId by remember { mutableStateOf<String?>(null) }

    // Get current user ID
    LaunchedEffect(Unit) {
        val user = viewModel.authRepository.currentUserFlow.first()
        if (user is AuthRemoteDataSource.UserData.PatientData) {
            currentPatientId = user.patient.id
        }
    }

    // Handle notifications
    LaunchedEffect(notificationData, currentPatientId) {
        notificationData?.let { data ->
            when (val type = data.type) {
                is NotificationType.Chat -> {
                    // Navigate to chat screen
                    navController.navigate(
                        getPatientChatRoute(
                            doctorId = type.senderId,
                            patientId = type.recipientId,
                            chatId = type.chatId
                        )
                    )
                }
                is NotificationType.Appointment -> {
                    // Handle appointment notification if needed
                    navController.navigate(BarRoutes.FEED.route)
                }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomBar(
            tabs = BarRoutes.entries.toTypedArray(),
            navController = navController,
            navigateToRoute = { route -> navController.navigate(route) }
        ) }
    ){
        NavHost(
            navController = navController,
            startDestination = BarRoutes.FEED.route,
            modifier = Modifier.padding(it)
        ) {
            composable(BarRoutes.FEED.route) {
                HomeScreenPatient(
                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) },
                    openDoctorsOverviewScreen = { specialty ->
                        navController.navigate(getPatientDoctorsOverviewRoute(specialty)) },
                    openDoctorProfileScreen = { doctorId ->
                        navController.navigate(getPatientDoctorsProfileRoute(doctorId)) }
                )
            }
            composable(BarRoutes.CHAT.route) {
                ChatMenuScreen(
                    openChatScreen = { chatId, patientId, doctorId ->
                        navController.navigate(getPatientChatRoute(doctorId, patientId, chatId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(BarRoutes.PROFILE.route) {
                PatientProfileScreen(
                    openPrescriptionsScreen = { navController.navigate(PATIENT_PROFILE_PRESCRIPTION_ROUTE) },
                    openMedicalReportsScreen = { navController.navigate(PATIENT_PROFILE_MEDICAL_REPORT_ROUTE) },
                    openMedicalHistoryScreen = { navController.navigate(PATIENT_PROFILE_MEDICAL_HISTORY_ROUTE) }
                )
            }
            composable(BarRoutes.APPOINTMENTS.route) {
                PatientAppointmentScreen()
            }

//            composable(HOME_PATIENT_ROUTE){
//                HomeScreenPatient(
//                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) },
//                    openDoctorsOverviewScreen = { specialty -> navController.navigate(getPatientDoctorsOverviewRoute(specialty)) }
//                )
//            }

            composable(SETTINGS_ROUTE) {
                SettingsScreen(
                    openSplashScreen = openSplashScreen,
                    showSnackBar = showSnackBar
                )
            }

            composable(PATIENT_CHAT_MENU_ROUTE){
                ChatMenuScreen(
                    openChatScreen = { doctorId, patientId, chatId ->
                        navController.navigate(getPatientChatRoute(doctorId, patientId, chatId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(PATIENT_CHAT_ROUTE){ backStackEntry ->
                ChatScreen(
                    chatId = backStackEntry.arguments?.getString("chatId") ?: "",
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                )
            }

            composable("patient/doctors/overview/{specialty}"){ backStackEntry ->
                DoctorsOverviewScreen(
                    openBookingScreen = { doctorId -> navController.navigate(getPatientBookingAppointmentsBookRoute(doctorId)) },
                    openDoctorProfileScreen = { doctorId -> navController.navigate(getPatientDoctorsProfileRoute(doctorId)) },
                    specialty = backStackEntry.arguments?.getString("specialty") ?: "",
                    goBack = { navController.popBackStack() }
                )
            }

            composable("patient/doctors/profile/{doctorId}"){ backStackEntry ->
                DoctorsProfileViewScreen(
                    openChatScreen = { chatId, patientId, doctorId ->
                        navController.navigate(getPatientChatRoute(doctorId, patientId, chatId)) },
                    doctorId = backStackEntry.arguments?.getString("doctorId") ?: "",
                    openBookingScreen = { doctorId -> navController.navigate(getPatientBookingAppointmentsBookRoute(doctorId)) },
                    goBack = { navController.popBackStack() }
                )
            }



            composable(PATIENT_BOOKING_APPOINTMENTS_ROUTE){backStackEntry ->
                BookAppointmentScreen(
                    doctorId = backStackEntry.arguments?.getString("doctorId") ?: "",
                    showSnackBar = showSnackBar
                )
            }

            composable(PATIENT_PROFILE_PRESCRIPTION_ROUTE) {
                PrescriptionScreen(
                    goBack = { navController.popBackStack() }
                )
            }

            composable(PATIENT_PROFILE_MEDICAL_REPORT_ROUTE) {
                MedicalReportScreen(
                    goBack = { navController.popBackStack() }
                )
            }

            composable(PATIENT_PROFILE_MEDICAL_HISTORY_ROUTE) {
                PatientMedicalHistoryScreen(
                    goBack = { navController.popBackStack() }
                )
            }
        }

    }
}

@Preview
@Composable
fun PatientAppPreview() {
    PatientApp()
}