package com.example.careconnect.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.R
import com.example.careconnect.common.ext.buttonField
import com.example.careconnect.common.ext.fieldModifier
import com.example.careconnect.common.ext.textFieldModifier
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.login.EmailField
import com.example.careconnect.ui.theme.CareConnectTheme


/**
 * Screen composable handling user sign-up.
 *
 * Collects input data, triggers sign-up logic in [viewModel],
 * and navigates to profile screen upon success.
 *
 * @param openProfileScreen Lambda to navigate to profile screen after sign-up.
 * @param openLoginScreen Lambda to navigate to login screen.
 * @param showSnackBar Lambda to display snackbar messages.
 * @param viewModel ViewModel that manages sign-up logic (defaulted via Hilt).
 */
@Composable
fun SignUpScreen(
    openProfileScreen: () -> Unit,
    openLoginScreen: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val navigateToProfile by viewModel.navigateToProfile.collectAsStateWithLifecycle()
    println("Debug: SignUpScreen")
    if (navigateToProfile) {
        openProfileScreen()
    } else {
        SignUpScreenContent(
            signUp = viewModel::signUp,
            openLoginScreen = openLoginScreen,
            showSnackBar = showSnackBar
        )
    }
}


/**
 * The main content of the Sign-Up screen displaying input fields, sign-up button,
 * error messages, and navigation link to login screen.
 *
 * @param signUp Function invoked to perform sign-up with input data and snackbar callback.
 * @param openLoginScreen Lambda to navigate to login screen.
 * @param showSnackBar Lambda to show snackbar messages.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignUpScreenContent(
    signUp: (String, String, String, String, (SnackBarMessage) -> Unit) -> Unit,
    openLoginScreen: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var snackBarMessage by remember { mutableStateOf<String?>(null) }
    val isLoading by remember { mutableStateOf(false) }

    val fieldModifier = Modifier.fieldModifier()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                SignUpTopSection()
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LabelTextField(name, { name = it }, fieldModifier, stringResource(R.string.name) )
                    Spacer(modifier = Modifier.height(15.dp))

                    LabelTextField(surname, { surname = it }, fieldModifier, stringResource(R.string.surname))
                    Spacer(modifier = Modifier.height(15.dp))

                    EmailField(email, { email = it }, fieldModifier)
                    Spacer(modifier = Modifier.height(15.dp))

                    PasswordSignUpTextField(password, { password = it }, Modifier.textFieldModifier())
                    Spacer(modifier = Modifier.height(40.dp))

                    // Display error message if exists
                    snackBarMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    // Sign-up button
                    Button(
                        modifier = Modifier.buttonField(),
                        onClick = {
                            signUp(name, surname, email, password, showSnackBar)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(size = 10.dp)
                    ) {
                        Text(
                            fontSize = 14.sp,
                            text = "Sign Up",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }

                    Spacer(Modifier.height(30.dp))

                    // Navigate to login if already have an account
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                openLoginScreen()
                            },
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            ) {
                                append("Already have an account? ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append("Login")
                            }
                        }
                    )

                    Spacer(Modifier.height(30.dp))
                }
            }
        }
    }
}


/**
 * Composable representing the top section of the sign-up screen,
 * displaying the app logo and the title "Sign Up".
 */
@Composable
fun SignUpTopSection() {
    val uiColor = MaterialTheme.colorScheme.primary

    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        // Top Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp) // Set the desired height of the box
                .align(Alignment.TopCenter) // Position at the top
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.6f), // Adjust this height as needed
                painter = painterResource(id = R.drawable.wave_above),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(alignment = Alignment.BottomCenter),
            text = stringResource(R.string.signup),
            style = MaterialTheme.typography.headlineLarge,
            color = uiColor
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
}


/**
 * Preview composable for the sign-up screen content to visualize
 * the UI in Android Studio preview.
 */
@Composable
@Preview(showSystemUi = true)
fun SignUpScreenPreview() {
    CareConnectTheme {
        SignUpScreenContent(
            openLoginScreen = {},
            signUp = { _, _, _, _, _ ->},
            showSnackBar = {}
        )

    }

}
