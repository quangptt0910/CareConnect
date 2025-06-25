package com.example.careconnect.screens.admin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.admin.appointment.AppointmentManageScreen
import com.example.careconnect.screens.admin.doctormanage.AddDoctorScheduleScreen
import com.example.careconnect.screens.admin.doctormanage.AddDoctorScreen
import com.example.careconnect.screens.admin.doctormanage.DoctorManageScreen
import com.example.careconnect.screens.admin.home.AdminHomeScreen
import com.example.careconnect.screens.admin.patientsmanage.PatientManageScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.ui.navigation.Route

/**
 * Composable NavHost that defines the navigation graph for the admin section of the app.
 *
 * This hosts all admin-related screens and manages navigation between them.
 *
 * @param navController The NavHostController to manage navigation state.
 * @param openSplashScreen Lambda to navigate to the splash screen (used from settings).
 * @param showSnackBar Lambda to show snack bar messages in various screens.
 * @param modifier Optional [Modifier] to be applied to the NavHost.
 */
@Composable
fun AdminNavHost(
    navController: NavHostController,
    openSplashScreen: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Route.HOME_ADMIN_ROUTE,
        modifier = modifier
    ) {
        composable(Route.HOME_ADMIN_ROUTE) {
            AdminHomeScreen(
                openAddDoctorScreen = { navController.navigate(Route.ADMIN_DOCTOR_MANAGE_ROUTE) }
            )
        }
        composable(Route.ADMIN_DOCTOR_MANAGE_ROUTE) {
            DoctorManageScreen(
                openAddDoctorScreen = { navController.navigate(Route.ADMIN_DOCTOR_ADD_ROUTE) },
                showSnackBar = showSnackBar
            )
        }

        composable(Route.ADMIN_PATIENT_MANAGE_ROUTE) {
            PatientManageScreen()
        }

        composable(Route.ADMIN_APPOINTMENTS_ROUTE) {
            AppointmentManageScreen()
        }

        composable(Route.ADMIN_DOCTOR_ADD_ROUTE){
            AddDoctorScreen(
                openDoctorScheduleScreen = { doctorId ->
                    navController.navigate(Route.adminDoctorScheduleRoute(doctorId)) },
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = Route.ADMIN_DOCTOR_SCHEDULE_ROUTE,
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType } )
        ){ backStackEntry ->
            AddDoctorScheduleScreen(
                openDoctorManageScreen = { navController.navigate(Route.ADMIN_DOCTOR_MANAGE_ROUTE) },
                onBack = {}
            )
        }

        composable(Route.SETTINGS_ROUTE) {
            SettingsScreen(
                openSplashScreen = openSplashScreen,
                showSnackBar = showSnackBar
            )
        }
    }
}
