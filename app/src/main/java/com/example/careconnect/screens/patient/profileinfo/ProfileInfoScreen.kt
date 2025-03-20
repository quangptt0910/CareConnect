package com.example.careconnect.screens.patient.profileinfo


import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.ui.theme.CareConnectTheme
import java.util.Calendar
import java.util.Locale



@Composable
fun MoreAboutYouScreen(

) {

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreAboutYouContent(
    weight: String,
    height: String,
    address: String,
    gender: String,
    dob: String,
    age: String,
    onContinueClick: () -> Unit = {},
    onWeightChange: (Double) -> Unit = {},
    onHeightChange: (Double) -> Unit = {},
    onGenderChange: (String) -> Unit = {},
    onDobChange: (String) -> Unit = {},
    onAgeChange: (Int) -> Unit = {},
    onAddressChange: (String) -> Unit = {},

    ) {
    var weightText by remember { mutableStateOf(weight) }
    var heightText by remember { mutableStateOf(height) }
    var addressText by remember { mutableStateOf(address) }
    var selectedDate by remember { mutableStateOf("") } // Store the selected date as a string
    var ageText by remember { mutableStateOf("") }
    var genderText by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderList = listOf("Male", "Female")
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf(dob) }

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
                    onValueChange = { genderText = it },
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
                        .menuAnchor(), // Ensures dropdown appears directly below the text field
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
                                onGenderChange(label)
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            InformationTextField(
                value = weightText,
                onValueChange = {
                    weightText = it
                    onWeightChange(it.toDoubleOrNull() ?: 0.0) // Update ViewModel
                },
                label = "Weight",
                unit = "kg",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            InformationTextField(
                value = heightText,
                onValueChange = {
                    heightText = it
                    onHeightChange(it.toDoubleOrNull() ?: 0.0)},
                label = "Height",
                unit = "cm",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            InformationTextField(
                value = addressText,
                onValueChange = {
                    addressText = it
                    onAddressChange((it.toDoubleOrNull() ?: 0.0).toString())},
                label = "Address",
                unit = "",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Date of Birth Picker (Instead of Age Input)
            OutlinedTextField(
                value = selectedDateText,
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
                                selectedDateText = formattedDate
                                onDobChange(formattedDate)
                                onAgeChange(calculatedAge)
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
                onClick = { onContinueClick()},
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
fun MoreAboutYouPreview() {

    CareConnectTheme {
        MoreAboutYouContent(
            onContinueClick = {},
            onWeightChange = {},
            weight = "70",
            height = "180",
            address = "Sample Address",
            onHeightChange = {},
            gender = "Male",
            onGenderChange = {},
            dob = "01/01/2000",
            onDobChange = {},
            age = "23",
            onAgeChange = {}
        )
    }
}