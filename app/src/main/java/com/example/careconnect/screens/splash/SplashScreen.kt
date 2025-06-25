package com.example.careconnect.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.NotificationData
import com.example.careconnect.R
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.delay

/**
 * Composable that represents the splash screen of the app.
 *
 * This screen shows the app logo, name, a catchphrase, and a loading indicator.
 * It listens to navigation route changes from [SplashViewModel] and triggers
 * navigation callbacks accordingly.
 *
 * It also handles incoming [notificationData] to pass along to subsequent screens.
 *
 * @param openAdminScreen Callback to navigate to the Admin screen.
 * @param openDoctorScreen Callback to navigate to the Doctor screen, optionally with notification data.
 * @param openPatientScreen Callback to navigate to the Patient screen, optionally with notification data.
 * @param openLoginScreen Callback to navigate to the Login screen.
 * @param showSnackBar Callback to show snack bar messages.
 * @param notificationData Optional notification data passed from outside (e.g. push notification).
 * @param viewModel The [SplashViewModel] providing navigation state and logic.
 */
@Composable
fun SplashScreen(
    openAdminScreen: () -> Unit,
    openDoctorScreen: (NotificationData?) -> Unit,
    openPatientScreen: (NotificationData?) -> Unit,
    openLoginScreen: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    notificationData: NotificationData? = null,
    viewModel: SplashViewModel = hiltViewModel()
) {
    println("Debug: SplashScreen")
    val navigateRoute by viewModel.navigationRoute.collectAsStateWithLifecycle()
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(notificationData) {
        notificationData?.let { viewModel.handleNotificationData(it) }
    }

    SplashScreenContent(showSnackBar = showSnackBar)

    LaunchedEffect(navigateRoute) {
        if(navigateRoute != null && !hasNavigated) {
            hasNavigated = true
            delay(100L)
            if (navigateRoute != null) {
                when (navigateRoute) {
                    "admin" -> openAdminScreen()
                    "doctor" -> openDoctorScreen(viewModel.getNotificationForNavigation())
                    "patient" -> openPatientScreen(viewModel.getNotificationForNavigation())
                    "login" -> openLoginScreen()
                    else -> openLoginScreen()
                }
            }
        }
    }
}

/**
 * Private composable that renders the content of the splash screen.
 *
 * Displays the app icon, name, catchphrase, and a circular progress indicator.
 *
 * @param showSnackBar Callback to show snack bar messages (currently unused in UI).
 */
@Composable
private fun SplashScreenContent(
    showSnackBar: (SnackBarMessage) -> Unit
) {
    ConstraintLayout {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.home_health_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.app_catchphrase),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(30.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

/**
 * Preview of [SplashScreenContent] for UI design tools.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    CareConnectTheme {
        SplashScreenContent(
            showSnackBar = {}
        )
    }
}