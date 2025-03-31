package com.example.careconnect.screens.admin.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.careconnect.ui.navigation.Route

/**
 * Models the navigation actions in the app.
 */
class AdminNavigationActions(val navController: NavHostController) {
    fun navigateTo(route: String) {
        navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    // Specific navigation methods if needed
    val navigateToOverview: () -> Unit = { navigateTo(Route.HOME_ADMIN_ROUTE) }
    val navigateToDoctorManage: () -> Unit = { navigateTo(Route.ADMIN_DOCTOR_MANAGE_ROUTE) }
    val navigateToPatientManage: () -> Unit = { navigateTo(Route.ADMIN_PATIENT_MANAGE_ROUTE) }
    val navigateToAppointments: () -> Unit = { navigateTo(Route.ADMIN_APPOINTMENTS_ROUTE) }
    val navigateToSettings: () -> Unit = { navigateTo(Route.SETTINGS_ROUTE) }
}