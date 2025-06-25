package com.example.careconnect.screens.doctor.patients

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.careconnect.dataclass.Patient

/**
 * Displays a filled card view containing a list of patients.
 *
 * @param title The title of the card (currently unused).
 * @param modifier Modifier to be applied to the card.
 * @param patients List of [Patient] objects to display.
 * @param onEditClick Callback when the edit icon is clicked, passing the patient ID.
 * @param onDeleteProduct Callback for patient deletion (currently not triggered in UI).
 */
@Composable
fun FilledCardPatientsView(
    title : String,
    modifier: Modifier = Modifier,
    patients: List<Patient>,
    onEditClick: (String) -> Unit,
    onDeleteProduct: (Patient) -> Unit,
) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Patient?>(null) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                patients.forEach { patients ->
                    ListItem(
                        headlineContent = { Text(patients.name + " " + patients.surname) },

                        trailingContent = {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        onEditClick(patients.id)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                }

                                Spacer(modifier = Modifier.width(2.dp))
                            }

                        }
                    )
                }
            }

            if (expanded) {

            }
        }
    }
}

/**
 * A styled multiline [OutlinedTextField] for doctors to input information.
 *
 * @param value Current text value.
 * @param onValueChange Callback triggered when the text changes.
 * @param modifier Modifier to be applied to the text field.
 * @param label Label text displayed inside the field.
 */
@Composable
fun TextFieldDoctor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.wrapContentHeight(),
    label: String,
) {
    val uiColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            modifier = Modifier.width(300.dp).height(150.dp).align(Alignment.CenterHorizontally),
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
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

            // Keyboard options for text
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
    }
}

/**
 * Displays a clickable medical category card with an icon and label.
 *
 * @param iconResId Resource ID for the icon.
 * @param title Title or label of the category.
 * @param onClick Callback triggered when the card is clicked.
 */
@Composable
fun MedicalCategoryCard(iconResId: Int, title: String, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp).height(90.dp).width(95.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Displays a horizontal row of medical category cards.
 *
 * @param items List of pairs containing icon resource IDs and corresponding labels.
 */
@Composable
fun CategoryRow(items: List<Pair<Int, String>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { (icon, label) ->
            MedicalCategoryCard(icon, label) {
                // Handle click
            }
        }
    }
}

/**
 * Section to manage patient symptoms input and selection.
 *
 * Allows typing symptoms, selecting from suggestions, or adding custom entries.
 *
 * @param symptomsList Current list of selected symptoms.
 * @param symptoms Current input text.
 * @param onSymptomChange Callback when the symptom input changes.
 * @param onAddSymptom Callback to add a new symptom to the list.
 * @param onRemoveSymptom Callback to remove an existing symptom from the list.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SymptomsSection(
    symptomsList: MutableList<String>,
    symptoms: String,
    onSymptomChange: (String) -> Unit,
    onAddSymptom: (String) -> Unit,
    onRemoveSymptom: (String) -> Unit
) {
    val allSymptoms = listOf(
        "Headache", "Dizziness", "Numbness", "Tingling", "Seizures",
        "Confusion", "Cough", "Shortness of breath", "Chest tightness", "Wheezing",
        "Sore throat", "Chest pain", "Palpitations", "Fainting", "Swelling in legs",
        "Nausea", "Vomiting", "Diarrhea", "Constipation", "Abdominal pain",
        "Heartburn", "Fatigue", "Fever", "Rash", "Itching", "Anxiety", "Depression"
    )

    var expanded by remember { mutableStateOf(false) }
    val filteredSuggestions = allSymptoms.filter {
        it.contains(symptoms, ignoreCase = true) && it !in symptomsList
    }
    val uiColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary

    Column(modifier = Modifier
        .width(300.dp)
        .padding(0.dp)) {

        Text(
            text = "Symptoms",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = symptoms,
                onValueChange = {
                    onSymptomChange(it)
                    expanded = true
                },
                label = { Text("Type or select symptom") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
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
            )

            ExposedDropdownMenu(
                expanded = expanded && filteredSuggestions.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                filteredSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = {
                            onAddSymptom(suggestion)
                            onSymptomChange("")
                            expanded = false
                        }
                    )
                }
            }
        }

        if (symptoms.isNotBlank() && symptoms !in symptomsList) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                onAddSymptom(symptoms)
                onSymptomChange("")
                expanded = false
            }) {
                Text("Add Custom Symptom")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow( // Layout that wraps chips
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            symptomsList.forEach { tag ->
                AssistChip(
                    onClick = { onRemoveSymptom(tag) },
                    label = { Text(tag) },
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}


/**
 * Allows manual input and tagging of symptoms with display and removal support.
 *
 * @param value Current text input value.
 * @param onValueChange Callback triggered when the input text changes.
 * @param onAdd Callback to add a new tag.
 * @param tags List of current tags.
 * @param onRemove Callback to remove an existing tag.
 */
@Composable
fun TagInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: (String) -> Unit,
    tags: List<String>,
    onRemove: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Enter symptom and press 'Add'") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            tags.forEach { tag ->
                Card(
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { onRemove(tag) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onAdd(value) }, enabled = value.isNotBlank()) {
            Text("Add Symptom")
        }
    }
}
