package com.example.careconnect.screens.patient

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.screens.patient.chat.ChatMenuScreen
import com.example.careconnect.screens.patient.doctorsoverview.DoctorsOverviewScreen
import com.example.careconnect.screens.patient.home.HomeScreenPatient
import com.example.careconnect.screens.patient.navigation.BarRoutes
import com.example.careconnect.screens.patient.navigation.BottomBar
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE

@Composable
fun PatientApp() {
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
                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) }
                )
            }
            composable(BarRoutes.CHAT.route) {
                ChatMenuScreen(
                )
            }
            composable(BarRoutes.PROFILE.route) {

            }
            composable(BarRoutes.BOOKING.route) {
                DoctorsOverviewScreen()
            }
        }

    }
}

@Preview
@Composable
fun PatientAppPreview() {
    PatientApp()
}