package com.example.careconnect.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.common.ext.card
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.screens.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    openSplashScreen: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val shouldRestart by viewModel.shouldRestartApp.collectAsStateWithLifecycle()

    if (shouldRestart) {
        openSplashScreen()
    } else {
        SettingsContent(
            signOut = viewModel::signOut,
            showErrorSnackbar = showErrorSnackbar
        )
    }
}


@Composable
fun SettingsContent(
    signOut: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit
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

        Card(
            modifier = Modifier.card(),
            onClick = signOut
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sign Out")
            }
        }
    }
}

///**
// * Composable function for displaying a sign-out card with a confirmation dialog.
// *
// * @param signOut Function triggered when the user confirms sign-out.
// */
//@ExperimentalMaterial3Api
//@Composable
//private fun SignOutCard(signOut: () -> Unit) {
//    var showWarningDialog by remember { mutableStateOf(false) }
//
//    RegularCardEditor(
//        AppText.sign_out,
//        Icons.Filled.Settings,
//        "",
//        Modifier.card()) {
//        showWarningDialog = true
//    }
//
//    if (showWarningDialog) {
//        AlertDialog(
//            title = { Text(stringResource(AppText.sign_out_title)) },
//            text = { Text(stringResource(AppText.sign_out_description)) },
//            dismissButton = { DialogCancelButton(AppText.cancel) { showWarningDialog = false } },
//            confirmButton = {
//                DialogConfirmButton(AppText.sign_out) {
//                    signOut()
//                    showWarningDialog = false
//                }
//            },
//            onDismissRequest = { showWarningDialog = false }
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CareConnectTheme {
        SettingsContent(
            signOut = {},
            showErrorSnackbar = {}
        )
    }
}