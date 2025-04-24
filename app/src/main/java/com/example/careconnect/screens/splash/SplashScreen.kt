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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    openAdminScreen: () -> Unit,
    openDoctorScreen: () -> Unit,
    openPatientScreen: () -> Unit,
    openLoginScreen: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    println("Debug: SplashScreen")
    val navigateRoute by viewModel.navigationRoute.collectAsStateWithLifecycle()

    SplashScreenContent(showSnackBar = showSnackBar)

    LaunchedEffect(navigateRoute) {
        delay(500L)
        if (navigateRoute != null) {
            when (navigateRoute) {
                "admin" -> openAdminScreen()
                "doctor" -> openDoctorScreen()
                "patient" -> openPatientScreen()
                "login" -> openLoginScreen()
                else -> openLoginScreen()
            }
        }
    }
}


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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    CareConnectTheme {
        SplashScreenContent(
            showSnackBar = {}
        )
    }
}