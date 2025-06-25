package com.example.careconnect.screens.patient.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Composable that displays the bottom navigation bar for the Patient section.
 *
 * @param tabs An array of [BarRoutes] defining the navigation items to display.
 * @param navController The [NavController] responsible for navigation actions.
 * @param navigateToRoute A lambda to trigger navigation to a specific route.
 * @param modifier Optional [Modifier] for styling the navigation bar.
 */
@Composable
fun BottomBar(
    tabs: Array<BarRoutes>,
    navController: NavController,
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val routes = remember { tabs.map { it.route }}
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val currentRoute = currentDestination?.route // Get the actual route from backstack

    NavigationBar(modifier = modifier){
        routes.forEach { route ->
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick = { navigateToRoute(route) },
                icon = {
                    val icon = tabs.find { it.route == route }?.icon ?: Icons.Filled.Home
                    Icon(icon, contentDescription = null)
                }
            )
        }
    }
}