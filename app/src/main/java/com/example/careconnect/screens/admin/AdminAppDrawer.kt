package com.example.careconnect.screens.admin


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDrawerContent(
    currentRoute: String,
    openSettingsScreen: () -> Unit,
    openOverviewScreen: () -> Unit,
    openDoctorManageScreen: () -> Unit,
    openPatientManageScreen: () -> Unit,
    openAppointmentsScreen: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.menu),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider()
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.overview)) },
                selected = currentRoute == Route.HOME_ADMIN_ROUTE,
                onClick = { openOverviewScreen(); closeDrawer() }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.doctors)) },
                selected = currentRoute == Route.ADMIN_DOCTOR_MANAGE_ROUTE,
                onClick = { openDoctorManageScreen(); closeDrawer() }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.patients)) },
                selected = currentRoute == Route.ADMIN_PATIENT_MANAGE_ROUTE,
                onClick = { openPatientManageScreen(); closeDrawer() }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.appointments)) },
                selected = currentRoute == Route.ADMIN_APPOINTMENTS_ROUTE,
                onClick = { openAppointmentsScreen(); closeDrawer() }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.settings)) },
                selected = currentRoute == Route.SETTINGS_ROUTE,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                badge = { Text("20") }, // Placeholder
                onClick = { openSettingsScreen(); closeDrawer() }
            )
//                    NavigationDrawerItem(
//                        label = { Text(stringResource(R.string.help_and_feedback)) },
//                        selected = currentRoute == Route.SETTINGS_ROUTE,
//                        icon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null) },
//                        onClick = { openSettingsScreen() },
//                    ) TODO() implement this route

            Spacer(Modifier.height(12.dp))
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopAppBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.admin)) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        }
    )
}

@Preview("Drawer contents")
@Composable
fun PreviewAdminDrawer() {
    AdminDrawerContent(
        currentRoute = Route.HOME_ADMIN_ROUTE,
        openSettingsScreen = {},
        openOverviewScreen = {},
        openDoctorManageScreen = {},
        openPatientManageScreen = {},
        openAppointmentsScreen = {},
        closeDrawer = {}

    )

}