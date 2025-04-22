package com.example.careconnect.screens.admin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.screens.admin.doctormanage.AddDoctorScheduleScreen
import com.example.careconnect.screens.admin.doctormanage.AddDoctorScreen
import com.example.careconnect.screens.admin.doctormanage.DoctorManageScreen
import com.example.careconnect.screens.admin.home.AdminHomeScreen
import com.example.careconnect.screens.admin.patientsmanage.PatientManageScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.ui.navigation.Route

@Composable
fun AdminNavHost(
    navController: NavHostController,
    openSplashScreen: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
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
                showErrorSnackbar = showErrorSnackbar
            )
        }

        composable(Route.ADMIN_PATIENT_MANAGE_ROUTE) {
            PatientManageScreen()
        }

        composable(Route.ADMIN_APPOINTMENTS_ROUTE) {
            PatientManageScreen()
        }

        composable(Route.ADMIN_DOCTOR_ADD_ROUTE){
            AddDoctorScreen(
                openDoctorManageScreen = { navController.navigate(Route.ADMIN_DOCTOR_MANAGE_ROUTE) },
                showErrorSnackbar = showErrorSnackbar
            )
        }

        composable(Route.ADMIN_DOCTOR_SCHEDULE_ROUTE){
            AddDoctorScheduleScreen()
        }
        composable(Route.SETTINGS_ROUTE) {
            SettingsScreen(
                openSplashScreen = openSplashScreen,
                showErrorSnackbar = showErrorSnackbar
            )
        }
    }
}
