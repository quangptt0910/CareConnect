package com.example.careconnect.screens.doctor.navigation

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
 * A composable function that displays a bottom navigation bar for the doctor's section of the app.
 *
 * This bottom navigation bar allows the user to navigate between different screens
 * within the doctor's workflow.
 *
 * @param tabs An array of [BarRoutesDoctor] defining the tabs to be displayed in the navigation bar.
 *             Each [BarRoutesDoctor] object contains the route and icon for a tab.
 * @param navController The [NavController] used for handling navigation events.
 * @param navigateToRoute A lambda function that is invoked when a navigation item is clicked.
 *                        It takes the route string of the selected tab as a parameter.
 * @param modifier An optional [Modifier] to be applied to the navigation bar.
 */
@Composable
fun BottomBarDoctor(
    tabs: Array<BarRoutesDoctor>,
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