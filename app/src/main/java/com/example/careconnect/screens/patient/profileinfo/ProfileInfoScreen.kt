package com.example.careconnect.screens.patient.profileinfo


import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.ui.theme.CareConnectTheme
import java.util.Calendar
import java.util.Locale

/**
 * TODO() Adjust the onGenderChange, onDobChange and onAge change - to remove it
 */
@Composable
fun ProfileInfoScreen(
    viewModel: ProfileInforViewModel = hiltViewModel(),
    openSplashScreen: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit
) {

    val shouldRestartApp by viewModel.shouldRestartApp.collectAsStateWithLifecycle()
    LaunchedEffect(shouldRestartApp) {
        if (shouldRestartApp) {
            openSplashScreen()
        }
    }

    ProfileInfoScreenContent(
        linkAccount = viewModel::linkAccount,
        onGenderChange = {},
        onDobChange = {},
        onAgeChange = {},
        showErrorSnackbar = showErrorSnackbar
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreenContent(
    linkAccount: (String, Double, Double, String, String, (ErrorMessage) -> Unit) -> Unit,
    onGenderChange: (String) -> Unit = {},
    onDobChange: (String) -> Unit = {},
    onAgeChange: (Int) -> Unit = {},
    showErrorSnackbar: (ErrorMessage) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") } // Store the selected date as a string
    var ageText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderList = listOf("MALE", "FEMALE", "OTHER")
    var showDatePicker by remember { mutableStateOf(false) }
    var dob by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()

    // Context for the date picker
    val context = LocalContext.current

    Surface {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "A little more about you",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Tell us about yourself",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Gender Selection using ExposedDropdownMenuBox
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = if (genderExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { genderExpanded = !genderExpanded }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true), // Ensures dropdown appears directly below the text field
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    genderList.forEach { label ->
                        DropdownMenuItem(
                            text = { Text(text = label) },
                            onClick = {
                                gender = label
                                //onGenderChange(label)
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            InformationTextField(
                value = weight,
                onValueChange = { weight = it },
                label = "Weight",
                unit = "kg",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            InformationTextField(
                value = height,
                onValueChange = { height = it },
                label = "Height",
                unit = "cm",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            InformationTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                unit = "",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Date of Birth Picker (Instead of Age Input)
            OutlinedTextField(
                value = dob,
                onValueChange = {},
                label = { Text("Date of Birth") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                val formattedDate = convertMillisToDate(selectedMillis)
                                val calculatedAge = calculateAge(selectedMillis)
                                dob = formattedDate
                                // onDobChange(formattedDate)
                                //onAgeChange(calculatedAge)
                            }
                        }) {
                            Text("OK")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                        linkAccount(
                            gender,
                            weight.toDoubleOrNull() ?: 0.0,
                            height.toDoubleOrNull() ?: 0.0,
                            dob,
                            address,
                            showErrorSnackbar)
                    },
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

/**
 * Converts a given timestamp (milliseconds) to a formatted date string (DD/MM/YYYY).
 *
 * @param millis The timestamp in milliseconds.
 * @return The formatted date as a string.
 */
fun convertMillisToDate(millis: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = millis }
    return String.format(
        Locale.getDefault(), "%02d/%02d/%04d",
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR)
    )
}

/**
 * Calculates the age based on the given timestamp (milliseconds).
 *
 * @param millis The timestamp representing the date of birth.
 * @return The calculated age as an integer.
 */
fun calculateAge(millis: Long): Int {
    val dobCalendar = Calendar.getInstance().apply { timeInMillis = millis }
    val today = Calendar.getInstance()

    var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
        age -= 1
    }
    return age
}


/**
 * Displays a date picker dialog, allowing users to select their date of birth.
 * Once a date is selected, the age is calculated and passed to the provided callback.
 *
 * @param context The application context for displaying the dialog.
 * @param onDateSelected Callback function that receives the selected date and calculated age.
 */
fun showDatePicker(context: android.content.Context, onDateSelected: (String, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
        val dob = Calendar.getInstance()
        dob.set(selectedYear, selectedMonth, selectedDay)

        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age -= 1 // Adjust for birthday not yet reached
        }

        val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)

        // Pass selected date and calculated age to ViewModel
        onDateSelected(formattedDate, age)
    }, year, month, day).show()
}

/**
 * Preview function for the "More About You" screen.
 * Displays a sample UI for testing and design purposes.
 */
@Preview(showBackground = true)
@Composable
fun ProfileInfoPreview() {

    CareConnectTheme {
        ProfileInfoScreenContent(
            linkAccount = { _, _, _, _, _, _ -> },
            onGenderChange = {},
            onDobChange = {},
            onAgeChange = {},
            showErrorSnackbar = {}
        )
    }
}