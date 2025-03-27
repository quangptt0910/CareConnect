package com.example.careconnect.screens.admin.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.R
import com.example.careconnect.screens.admin.navigation.AdminNavigationActions
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme



@Composable
fun HomeScreenAdmin(
    openSettingsScreen: () -> Unit,
){
    val navController = rememberNavController()
    val navActions = remember(navController) {
        AdminNavigationActions(navController)
    }

    HomeScreenAdminContent(
        openSettingsScreen = openSettingsScreen,
        openDoctorManageScreen = {},
        openPatientManageScreen = {},
        openAppointmentsScreen = {},
    )
}


@Composable
fun HomeScreenAdminContent(
    openSettingsScreen: () -> Unit,
    openDoctorManageScreen: () -> Unit,
    openPatientManageScreen: () -> Unit,
    openAppointmentsScreen: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        DetailedDrawerExample(
            content = { padding ->
                Text(
                    stringResource(R.string.admin),
                    modifier = Modifier.padding(padding)
                )
            },
            openSettingsScreen = { openSettingsScreen() },
            openOverviewScreen = TODO(),
            openDoctorManageScreen = { openDoctorManageScreen() },
            openPatientManageScreen = { openPatientManageScreen },
            openAppointmentsScreen = { openAppointmentsScreen },
            modifier = TODO(),
        )

    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        HomeScreenAdminContent(
            openSettingsScreen = {},
            openDoctorManageScreen = {},
            openPatientManageScreen = {},
            openAppointmentsScreen = {},
        )
    }
}