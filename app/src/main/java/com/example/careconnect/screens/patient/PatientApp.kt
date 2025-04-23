package com.example.careconnect.screens.patient

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.ErrorMessage
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
import com.example.careconnect.ui.navigation.Route.PATIENT_APPOINTMENTS_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_CHAT_MENU_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_CHAT_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.getPatientChatRoute
import com.example.careconnect.ui.navigation.Route.getPatientDoctorsOverviewRoute
import com.example.careconnect.ui.navigation.Route.getPatientDoctorsProfileRoute

@Composable
fun PatientApp(
    openSplashScreen: () -> Unit = {},
    showErrorSnackbar: (ErrorMessage) -> Unit = {}
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
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
                    openChatScreen = { chatId, doctorId ->
                        navController.navigate(getPatientChatRoute(doctorId, chatId))
                    }
                )
            }
            composable(BarRoutes.PROFILE.route) {

            }
            composable(BarRoutes.BOOKING.route) {
                BookAppointmentScreen(
                )
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
                    showErrorSnackbar = showErrorSnackbar
                )
            }

            composable(PATIENT_CHAT_MENU_ROUTE){
                ChatMenuScreen(
                    openChatScreen = { doctorId, chatId ->
                        navController.navigate(getPatientChatRoute(doctorId, chatId))
                    }
                )
            }

            composable(PATIENT_CHAT_ROUTE){ backStackEntry ->
                ChatScreen(
                    chatId = backStackEntry.arguments?.getString("chatId") ?: "",
                    doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                )
            }

            composable("patient/doctors/overview/{specialty}"){ backStackEntry ->
                DoctorsOverviewScreen(
                    openBookingScreen = { navController.navigate(PATIENT_APPOINTMENTS_ROUTE) },
                    openDoctorProfileScreen = { doctorId -> navController.navigate(getPatientDoctorsProfileRoute(doctorId)) },
                    specialty = backStackEntry.arguments?.getString("specialty") ?: ""
                )
            }

            composable("patient/doctors/profile/{doctorId}"){ backStackEntry ->
                DoctorsProfileViewScreen(
                    openChatScreen = { chatId, doctorId ->
                        navController.navigate(getPatientChatRoute(doctorId, chatId)) },
                    doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                )
            }

            composable(PATIENT_APPOINTMENTS_ROUTE){
                BookAppointmentScreen(

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