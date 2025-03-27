package com.example.careconnect.screens.admin.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.serialization.Serializable

@Serializable
object HomeAdminRoute

@Composable
fun HomeScreenAdmin(
    openSettingsScreen: () -> Unit,
){
    HomeScreenAdminContent(
        openSettingsScreen = openSettingsScreen
    )
}


@Composable
fun HomeScreenAdminContent(
    openSettingsScreen: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        DetailedDrawerExample(
            content = { padding ->
                Text(stringResource(R.string.admin),
                    modifier = Modifier.padding(padding))
            },
            openSettingsScreen = {
                openSettingsScreen()
            }
        )

    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        HomeScreenAdminContent(
            openSettingsScreen = {}
        )
    }
}