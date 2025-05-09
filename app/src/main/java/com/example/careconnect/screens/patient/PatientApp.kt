package com.example.careconnect.screens.patient

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.patient.appointment.BookAppointmentScreen
import com.example.careconnect.screens.patient.chat.ChatMenuScreen
import com.example.careconnect.screens.patient.chat.ChatScreen
import com.example.careconnect.screens.patient.doctorsoverview.DoctorsOverviewScreen
import com.example.careconnect.screens.patient.doctorsoverview.DoctorsProfileViewScreen
import com.example.careconnect.screens.patient.home.HomeScreenPatient
import com.example.careconnect.screens.patient.navigation.BarRoutes
import com.example.careconnect.screens.patient.navigation.BottomBar
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.ui.navigation.Route.HOME_PATIENT_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_BOOKING_APPOINTMENTS_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_CHAT_MENU_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_CHAT_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.getPatientBookingAppointmentsBookRoute
import com.example.careconnect.ui.navigation.Route.getPatientChatRoute
import com.example.careconnect.ui.navigation.Route.getPatientDoctorsOverviewRoute
import com.example.careconnect.ui.navigation.Route.getPatientDoctorsProfileRoute

@Composable
fun PatientApp(
    openSplashScreen: () -> Unit = {},
    showSnackBar: (SnackBarMessage) -> Unit = {}
) {
    val navController = rememberNavController()

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
                        navController.navigate(getPatientDoctorsOverviewRoute(specialty)) }
                )
            }
            composable(BarRoutes.CHAT.route) {
                ChatMenuScreen(
                    openChatScreen = { chatId, patientId, doctorId ->
                        navController.navigate(getPatientChatRoute(doctorId, patientId, chatId))
                    }
                )
            }
            composable(BarRoutes.PROFILE.route) {

            }
            composable(BarRoutes.APPOINTMENTS.route) {
//                BookAppointmentScreen(
//                )
            }

            composable(HOME_PATIENT_ROUTE){
                HomeScreenPatient(
                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) },
                    openDoctorsOverviewScreen = { specialty -> navController.navigate(getPatientDoctorsOverviewRoute(specialty)) }
                )
            }

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
                    }
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
                    specialty = backStackEntry.arguments?.getString("specialty") ?: ""
                )
            }

            composable("patient/doctors/profile/{doctorId}"){ backStackEntry ->
                DoctorsProfileViewScreen(
                    openChatScreen = { chatId, patientId, doctorId ->
                        navController.navigate(getPatientChatRoute(doctorId, patientId, chatId)) },
                    doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                )
            }



            composable(PATIENT_BOOKING_APPOINTMENTS_ROUTE){backStackEntry ->
                BookAppointmentScreen(
                    doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
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