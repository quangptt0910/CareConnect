package com.example.careconnect.screens.admin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.careconnect.screens.admin.doctormanage.DoctorManageScreen
import com.example.careconnect.screens.admin.home.HomeScreenAdmin
import com.example.careconnect.screens.admin.patientsmanage.PatientManageScreen
import com.example.careconnect.ui.navigation.Route

@Composable
fun AdminNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.HOME_ADMIN_ROUTE,
        modifier = modifier
    ) {
        composable(Route.HOME_ADMIN_ROUTE) {
            HomeScreenAdmin()
        }
        composable(Route.ADMIN_DOCTOR_MANAGE_ROUTE) {
            DoctorManageScreen()
        }
        composable(Route.ADMIN_PATIENT_MANAGE_ROUTE) {
            PatientManageScreen()
        }
        composable(Route.ADMIN_APPOINTMENTS_ROUTE) {
            PatientManageScreen()
        }

    }
}