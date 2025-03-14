package com.example.careconnect.screens.login

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.common.ext.fieldModifier
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.primaryLight
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Composable
fun LoginScreen(
    openHomeScreen: () -> Unit,
    openSignUpScreen: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
){
    val shouldRestartApp by viewModel.shouldRestartApp.collectAsStateWithLifecycle()
    if (shouldRestartApp) {
        openHomeScreen()
    } else {
        LoginScreenContent(
            openSignUpScreen = openSignUpScreen,
            login = viewModel::login,
            showErrorSnackbar = showErrorSnackbar
        )
    }
}

@Composable
fun LoginScreenContent(
    openSignUpScreen: () -> Unit,
    login: (String, String, (ErrorMessage) -> Unit) -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {},
    onFacebookSignInClick: () -> Unit = {},
    showErrorSnackbar: (ErrorMessage) -> Unit
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize() // This makes sure the content takes up the full screen
        ) {
            // Top Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Set the desired height of the box
                    .align(Alignment.TopCenter) // Position at the top
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.6f), // Adjust this height as needed
                    painter = painterResource(id = R.drawable.wave_above),
                    contentDescription = "wave above",
                    contentScale = ContentScale.FillBounds
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 160.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Welcome to CareConnect",
                    modifier = Modifier
                        .padding(15.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(60.dp))

                EmailField(email, { email = it }, Modifier.fieldModifier())

                Spacer(modifier = Modifier.height(14.dp))

                PasswordTextField(password, { password = it }, Modifier.fieldModifier())

                Spacer(modifier = Modifier.height(15.dp))

                // Forgot Password Section TODO()
//                Row(
//                    modifier = Modifier.width(280.dp),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    Text(
//                        modifier = Modifier.clickable { onForgotPasswordClick() },
//                        text = "Forgot password?",
//                        style = MaterialTheme.typography.labelMedium,
//                        color = MaterialTheme.colorScheme.primary,
//                        textDecoration = TextDecoration.Underline
//                    )
//                }

                Spacer(modifier = Modifier.height(30.dp))

                LoginButton(
                    text = R.string.login,
                    modifier = Modifier.fieldModifier(),
                    onButtonClick = {
                        login(email, password, showErrorSnackbar)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("Don't have an account?")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append(" ")
                            append("Create now")
                        }
                    },
                    modifier = Modifier.clickable {
                        openSignUpScreen
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Or continue with",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    SocialMediaLogIn(
                        icon = R.drawable.google,
                        modifier = Modifier.width(50.dp),
                        onSignInClick = onGoogleSignInClick
                    )

                    Spacer(modifier = Modifier.width(60.dp))

                    SocialMediaLogIn(
                        icon = R.drawable.facebook,
                        modifier = Modifier.width(50.dp),
                        onSignInClick = onFacebookSignInClick
                    )
                }
            }



            // Bottom Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter) // Move it to the bottom
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.3f), // Adjust this height as needed
                    painter = painterResource(id = R.drawable.wave_below),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}


/**
 * Composable function for a social media login button.
 *
 * @param modifier The modifier to be applied to the component.
 * @param icon The resource ID of the social media icon.
 * @param text The label for the login button.
 * @param onSignInClick The callback function when the button is clicked.
 */
@Composable
fun SocialMediaLogIn(
    icon: Int,
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(50.dp) // Ensures it's a square
            .clip(CircleShape) // Clips it into a circle
            .background(MaterialTheme.colorScheme.primary) // Background color
            .clickable { onSignInClick() },
        contentAlignment = Alignment.Center // Centers content
    ) {
        Row(
            modifier = Modifier.fillMaxSize(), // Ensures Row fills Box
            horizontalArrangement = Arrangement.Center, // Centers horizontally
            verticalAlignment = Alignment.CenterVertically // Centers vertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(30.dp) // Adjust size as needed
            )

            Spacer(modifier = Modifier.width(5.dp))

        }
    }
}

/**
 * Extension function that applies styling to social media login buttons.
 * Changes the background and border depending on the system theme.
 *
 * @return A modified [Modifier] instance with applied styling.
 */
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.socialMedia(): Modifier = composed {
    if (isSystemInDarkTheme()) {
        background(Color.Transparent).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(4.dp)
        )
    } else {
        background(primaryLight)
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    CareConnectTheme {
        LoginScreenContent(
            openSignUpScreen = {},
            login = { _, _, showErrorSnackbar -> },
            showErrorSnackbar = {}
        )
    }
}