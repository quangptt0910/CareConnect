package com.example.careconnect.screens.patient.profileinfo


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * A customizable text field used for numerical input (e.g., weight, height).
 *
 * @param value The current text input value.
 * @param onValueChange A callback function triggered when the input value changes.
 * @param label The label displayed inside the text field.
 * @param unit A string representing the measurement unit (e.g., "kg", "cm").
 * @param modifier Modifier for styling and layout customization.
 * @param isError Boolean indicating whether the field contains an error.
 * @param SnackBarMessage The error message displayed below the text field if `isError` is true.
 * @param keyboardType The type of keyboard to display (default is `KeyboardType.Number`).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    SnackBarMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, // No fill color when focused
                unfocusedContainerColor = Color.Transparent, // No fill color when unfocused
                disabledContainerColor = Color.Transparent, // No fill color when disabled
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            isError = isError,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
    }

    if (isError && SnackBarMessage != null) {
        Text(
            text = SnackBarMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}


/**
 * A customizable text field used for gender input, allowing for an optional trailing icon.
 *
 * @param value The current text input value.
 * @param onValueChange A callback function triggered when the input value changes.
 * @param label The label displayed inside the text field.
 * @param trailingIcon An optional composable displayed as an icon on the right side.
 * @param modifier Modifier for styling and layout customization.
 * @param isError Boolean indicating whether the field contains an error.
 * @param SnackBarMessage The error message displayed below the text field if `isError` is true.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationTextFieldGender(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    SnackBarMessage: String? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 14.sp
                )
            },
            trailingIcon = trailingIcon,
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, // No fill color when focused
                unfocusedContainerColor = Color.Transparent, // No fill color when unfocused
                disabledContainerColor = Color.Transparent, // No fill color when disabled
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            isError = isError,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
    }

    if (isError && SnackBarMessage != null) {
        Text(
            text = SnackBarMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
