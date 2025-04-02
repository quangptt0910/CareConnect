package com.example.careconnect.screens.admin.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.careconnect.getErrorMessage
import com.example.careconnect.screens.admin.doctormanage.AddDoctorScreen
import com.example.careconnect.screens.admin.doctormanage.DoctorManageScreen
import com.example.careconnect.screens.admin.home.HomeScreenAdmin
import com.example.careconnect.screens.admin.patientsmanage.PatientManageScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.ui.navigation.Route
import kotlinx.coroutines.launch

@Composable
fun AdminNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navController,
        startDestination = Route.HOME_ADMIN_ROUTE,
        modifier = modifier
    ) {
        composable(Route.HOME_ADMIN_ROUTE) {
            HomeScreenAdmin()
        }
        composable(Route.ADMIN_DOCTOR_MANAGE_ROUTE) {
            DoctorManageScreen(
                onAddDoctorClick = { navController.navigate(Route.ADMIN_DOCTOR_ADD_ROUTE) }
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
            )
        }
        composable(Route.SETTINGS_ROUTE) {
            SettingsScreen(
                openSplashScreen = {
                    navController.navigate(Route.SPLASH_ROUTE) { launchSingleTop = true }
                },
                showErrorSnackbar = { errorMessage ->
                    val message = getErrorMessage(errorMessage)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            )
        }


    }
}