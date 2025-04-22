package com.example.careconnect.screens.login

import android.R.attr.action
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.R.string as AppText


/**
 * A composable function that creates a password input field with a label and a visibility toggle button.
 *
 * @param value The current password value of the text field.
 * @param onValueChange Callback invoked when the password value changes.
 * @param modifier Modifier to be applied to the password text field.
 * @param textFieldLabel The label text displayed inside the text field.
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldLabel: String = "Enter your password",
) {
    // State variables to manage password visibility and validity
    var showPassword by remember { mutableStateOf(false) }

    val uiColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.primary

    // OutlinedTextField for entering user password
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = textFieldLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        },
        modifier = modifier
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,

            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),

            cursorColor = MaterialTheme.colorScheme.primary,

            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = uiColor.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),

        // Keyboard options for password input
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),

        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            // Password visibility toggle icon
            PasswordVisibilityToggleIcon(
                showPassword = showPassword,
                onTogglePasswordVisibility = { showPassword = !showPassword })
        }
    )
}

/**
 * A text field for email input.
 * @param value The current text value.
 * @param onNewValue Callback when the text value changes.
 * @param modifier Modifier for styling.
 */
@Composable
fun EmailField(
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.primary

    // Adjust the icon tint for visibility in both light and dark themes
    if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onPrimary // Dark mode: use a light icon color
    } else {
        MaterialTheme.colorScheme.onSurface // Light mode: use a dark icon color
    }
    OutlinedTextField(
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onNewValue(it) },
        label = {
            Text(
                text = stringResource(AppText.email),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        },
        colors = TextFieldDefaults.colors(
            // Text colors
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Gray,

            // Container colors
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,

            // Border/Indicator colors
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),

            // Label colors
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = uiColor.copy(alpha = 0.8f),

            // Cursor color
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),

        trailingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") }
    )
}

/**
 * A button to toggle password visibility.
 * @param showPassword Whether the password is currently visible.
 * @param onTogglePasswordVisibility Callback to toggle visibility.
 */
@Composable
fun PasswordVisibilityToggleIcon(
    showPassword: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.primary
    // Determine the icon based on password visibility
    val image = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
    val contentDescription = if (showPassword) "Hide password icon" else "Show password icon"

    // IconButton to toggle password visibility
    IconButton(onClick = onTogglePasswordVisibility) {
        Icon(imageVector = image,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordTextField() {

    CareConnectTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            EmailField(
                value = "",
                onNewValue = {},
                modifier = Modifier
            )

            PasswordTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
            )

            PasswordVisibilityToggleIcon(
                showPassword = true,
                onTogglePasswordVisibility = {}
            )
        }
    }
}

/**
 * A login button composable that is typically used in login screens.
 * The button has a fixed height, rounded corners, and uses the primary color from the Material theme.
 *
 * @param text The string resource ID for the button text.
 * @param modifier The modifier to be applied to the button.
 * @param action The action to be triggered when the button is clicked.
 */
@Composable
fun LoginButton(@StringRes text: Int, modifier: Modifier, onButtonClick: () -> Unit) {
    OutlinedButton(
        onClick = onButtonClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
        )
    }
}