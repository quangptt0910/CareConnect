package com.example.careconnect.screens.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.careconnect.common.ext.fieldModifier
import com.example.careconnect.ui.theme.AppTheme

@Composable
fun LoginScreen(

){

}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,

){
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
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }

            Column(
                modifier = Modifier.padding(top = 160.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Welcome to CareConnect",
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(60.dp))

                EmailField(uiState.email, onEmailChange, Modifier.fieldModifier()
                )

                Spacer(modifier = Modifier.height(14.dp))

                PasswordTextField(uiState.password, onPasswordChange, Modifier.fieldModifier())

                Spacer(modifier = Modifier.height(30.dp))

                LoginButton(
                    text = R.string.login,
                    modifier = Modifier.fieldModifier(),
                ){
                    onSignInClick()
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


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    AppTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onSignInClick = {}


        )
    }
}