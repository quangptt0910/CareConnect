package com.example.careconnect.screens.doctor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.doctor.home.DoctorHomeScreen
import com.example.careconnect.screens.doctor.navigation.BarRoutesDoctor
import com.example.careconnect.screens.doctor.navigation.BottomBarDoctor
import com.example.careconnect.screens.doctor.patients.PatientsProfileScreen
import com.example.careconnect.screens.doctor.patients.PatientsScreen
import com.example.careconnect.screens.doctor.patients.medicalreports.CreateMedicalReportScreen
import com.example.careconnect.screens.doctor.patients.medicalreports.MedicalReportsScreen
import com.example.careconnect.screens.doctor.profile.ScheduleScreen
import com.example.careconnect.screens.patient.chat.ChatMenuScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_CREATE_MEDICAL_REPORT_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_MEDICAL_REPORT_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_PROFILE_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsCreateMedicalReportRoute
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsMedicalReportRoute
import com.example.careconnect.ui.navigation.Route.getDoctorPatientsProfileRoute
import com.example.careconnect.ui.navigation.Route.getPatientChatRoute

@Composable
fun DoctorApp(
    openSplashScreen: () -> Unit = {},
    showSnackBar: (SnackBarMessage) -> Unit = {}
) {
    val navController = rememberNavController()

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
                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) }
                )
            }
            composable(BarRoutesDoctor.CHAT.route) {
                ChatMenuScreen(
                    openChatScreen = { chatId, doctorId ->
                        navController.navigate(getPatientChatRoute(doctorId, chatId))
                    }
                )
            }

            composable(BarRoutesDoctor.PATIENTS.route) {
                PatientsScreen(
                    openPatientsProfile = { patientId ->
                        navController.navigate(getDoctorPatientsProfileRoute(patientId)) }
                )
            }

            composable(BarRoutesDoctor.PROFILE.route) {
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
                    }
                )
            }

            composable(DOCTOR_PATIENTS_MEDICAL_REPORT_ROUTE) { backStackEntry ->
                MedicalReportsScreen(
                    patientId = backStackEntry.arguments?.getString("patientId") ?: "",
                    openCreateMedicalReportScreen = { patientId ->
                        navController.navigate(getDoctorPatientsCreateMedicalReportRoute(patientId))
                    }
                )
            }

            composable(DOCTOR_PATIENTS_CREATE_MEDICAL_REPORT_ROUTE) { backStackEntry ->
                CreateMedicalReportScreen(
                    patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                )
            }

            composable(SETTINGS_ROUTE) {
                SettingsScreen(
                    openSplashScreen = openSplashScreen,
                    showSnackBar = showSnackBar
                )
            }

//            composable(HOME_DOCTOR_ROUTE) {
//                DoctorHomeScreen(
//                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) }
//                )
//            }
        }
    }
}