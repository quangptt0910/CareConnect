package com.example.careconnect.screens.admin.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.careconnect.ui.navigation.Route

/**
 * Models the navigation actions in the app.
 */
class AdminNavigationActions(navController: NavHostController) {
    val openDoctorManageScreen: () -> Unit = {
        navController.navigate(Route.DoctorManageRoute) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    val openPatientManageScreen: () -> Unit = {
        navController.navigate(Route.PatientManageRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val openAppointmentsScreen: () -> Unit = {
        navController.navigate(Route.AppointmentManageRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }
}