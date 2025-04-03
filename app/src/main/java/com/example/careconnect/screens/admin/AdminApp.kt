package com.example.careconnect.screens.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.screens.admin.navigation.AdminNavHost
import com.example.careconnect.screens.admin.navigation.AdminNavigationActions
import com.example.careconnect.ui.navigation.Route
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApp(
    openSplashScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navActions = remember(navController) {
        AdminNavigationActions(navController)
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Route.HOME_ADMIN_ROUTE

    // Define routes where the top bar and drawer should be hidden
    val noTopBarRoutes = setOf(Route.ADMIN_DOCTOR_ADD_ROUTE)

    ModalNavigationDrawer(
        drawerContent = {
            if (currentRoute !in noTopBarRoutes) {
                AdminDrawerContent(
                    currentRoute = currentRoute,
                    openSettingsScreen = { navActions.navigateToSettings() },
                    openOverviewScreen = { navActions.navigateToOverview() },
                    openDoctorManageScreen = { navActions.navigateToDoctorManage() },
                    openPatientManageScreen = { navActions.navigateToPatientManage() },
                    openAppointmentsScreen = { navActions.navigateToAppointments() },
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
            }
        },
        drawerState = drawerState,

    ) {
        Scaffold(
            topBar = {
                if (currentRoute !in noTopBarRoutes) {
                    AdminTopAppBar(
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }
                    )
                }
            }
        ) { padding ->
            AdminNavHost(
                navController = navController,
                openSplashScreen = openSplashScreen,
                modifier = Modifier.padding(padding)
            )
        }

    }
}

