package com.example.careconnect.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.common.RegularCardEditor
import com.example.careconnect.common.ext.card
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.admin.doctormanage.DialogCancelButton
import com.example.careconnect.screens.admin.doctormanage.DialogConfirmButton
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Composable representing the Settings screen.
 *
 * Displays user settings and handles app restart on sign out.
 *
 * @param openSplashScreen Callback to open the splash screen when app needs to restart.
 * @param showSnackBar Function to display snack bar messages.
 * @param viewModel ViewModel to manage settings logic, defaults to [SettingsViewModel].
 */
@Composable
fun SettingsScreen(
    openSplashScreen: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val shouldRestart by viewModel.shouldRestartApp.collectAsStateWithLifecycle()
    if (shouldRestart) {
        println("DEBUG: Triggering navigation to splash screen")
        LaunchedEffect(Unit) {
            viewModel.onRestart()
            openSplashScreen()
        }
        return
    }
   
    SettingsContent(
        signOut = viewModel::signOut,
        showSnackBar = showSnackBar
    )

}

/**
 * Composable displaying the content of the Settings screen.
 *
 * Shows profile info and sign-out option.
 *
 * @param signOut Callback invoked when user chooses to sign out.
 * @param showSnackBar Function to display snack bar messages.
 */
@Composable
fun SettingsContent(
    signOut: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Profile",
            modifier = Modifier.padding(16.dp, 8.dp).align(Alignment.Start),
            style = MaterialTheme.typography.headlineMedium,
        )
        SignOutCard { signOut() }
    }
}

/**
 * Composable showing a card with a sign-out option.
 *
 * Displays a confirmation dialog when triggered.
 *
 * @param signOut Lambda invoked when user confirms sign out.
 */
@Composable
private fun SignOutCard(signOut: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(false) }

    RegularCardEditor(
        R.string.sign_out,
        Icons.Filled.Settings,
        "",
        Modifier.card()) {
        showWarningDialog = true
    }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.sign_out_title)) },
            text = { Text(stringResource(R.string.sign_out_description)) },
            dismissButton = { DialogCancelButton(R.string.cancel) { showWarningDialog = false } },
            confirmButton = {
                DialogConfirmButton(R.string.sign_out) {
                    signOut()
                    showWarningDialog = false
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}

/**
 * Preview for SettingsContent composable.
 */
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CareConnectTheme {
        SettingsContent(
            signOut = {},
            showSnackBar = {}
        )
    }
}