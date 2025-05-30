package com.example.careconnect.screens.signup

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.careconnect.R
import com.example.careconnect.common.ext.fieldModifier
import com.example.careconnect.screens.login.EmailField
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.R.string as AppText

/**
 * A password field with visibility toggle and strength validation.
 * @param value The current password value.
 * @param onValueChange Callback when the password value changes.
 * @param modifier Modifier for styling.
 * @param textFieldLabel The label text for the field.
 * @param onHasStrongPassword Callback to indicate if the password is strong.
 */
@Composable
fun PasswordSignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldLabel: String = "Enter your password",
    onHasStrongPassword: (isStrong: Boolean) -> Unit = {},

    ) {
    // State variables to manage password visibility and validity
    var showPassword by remember { mutableStateOf(false) }
    //var isPasswordError by remember { mutableState(true) }
    var isPasswordError by remember { mutableStateOf(true) }


    val uiColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.primary

    // OutlinedTextField for entering user password
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),

            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),

            shape = RoundedCornerShape(10.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            //visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // Password visibility toggle icon
                PasswordVisibilityToggleIcon(
                    showPassword = showPassword,
                    onTogglePasswordVisibility = { showPassword = !showPassword })
            },
            isError = !isPasswordError,

            label = {
                Text(
                    text = textFieldLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
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
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = AppText.password_requirement),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp
        )
        // check for strong password
        if (value.isNotEmpty()) {
            val strongPassword = value.isValidPassword()
            onHasStrongPassword(strongPassword)

            Text(
                modifier = Modifier.semantics { contentDescription = "StrengthPasswordMessage" },
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                        )
                    ) {

                        append(stringResource(id = AppText.password_level))
                        withStyle(
                            style = SpanStyle(
                                fontSize = 13.sp,
                                fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
                                color = if (strongPassword) /*MaterialTheme.colorScheme.primary*/ Color(0xFF2B6A46) else MaterialTheme.colorScheme.error,
                            )
                        ) {
                            if (strongPassword) {
                                append(stringResource(id = AppText.password_valid))
                            } else {
                                append(stringResource(id = AppText.password_not_valid))

                            }
                        }
                    }

                }
            )
        }
    }
}

/**
 * A composable function that creates an outlined text field with a label and an optional trailing icon.
 *
 * @param value The current text value of the text field.
 * @param onValueChange Callback invoked when the text value changes.
 * @param modifier Modifier to be applied to the text field.
 * @param label The label text displayed inside the text field.
 * @param trailing An optional composable function for the trailing icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    trailing: @Composable (() -> Unit)? = null,
) {
    val uiColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            modifier = modifier.fillMaxWidth(),
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                // Check if trailing composable is provided
                trailing?.invoke()
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

            // Keyboard options for text
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
    }
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


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SignUpFieldPreview() {
    CareConnectTheme {

        var name by remember { mutableStateOf("") }
        var surname by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val fieldModifier = Modifier.fieldModifier()

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                SignUpTopSection()
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LabelTextField(
                        name,
                        { name = it },
                        fieldModifier,
                        stringResource(R.string.name)
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    LabelTextField(
                        surname,
                        { surname = it },
                        fieldModifier,
                        stringResource(R.string.surname)
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    EmailField(email, { email = it }, fieldModifier)
                    Spacer(modifier = Modifier.height(15.dp))

                    PasswordSignUpTextField(password, { password = it }, fieldModifier)
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
