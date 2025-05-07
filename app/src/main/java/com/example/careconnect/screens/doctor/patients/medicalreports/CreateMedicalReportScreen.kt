package com.example.careconnect.screens.doctor.patients.medicalreports

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.screens.doctor.patients.SymptomsSection
import com.example.careconnect.screens.doctor.patients.TextFieldDoctor
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun CreateMedicalReportScreen(
    patientId: String,
){
    CreateMedicalReportScreenContent(
        patientId = patientId
    )
}

@Composable
fun CreateMedicalReportScreenContent(
    patientId: String,
){
    var symptoms by rememberSaveable { mutableStateOf("") }
    val symptomsList = remember { mutableStateListOf<String>() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = "Create Medical Report",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )

                Text(
                    text = "Name Surname:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(3.dp)
                )
                Text(
                    text = "Date of Birth:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(3.dp)
                )
                Text(
                    text = "Gender:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(3.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SymptomsSection(
                    symptomsList = symptomsList,
                    symptoms = symptoms,
                    onSymptomChange = { symptoms = it },
                    onAddSymptom = {
                        symptomsList.add(it)
                    },
                    onRemoveSymptom = {
                        symptomsList.remove(it)
                    }
                )
            }
        item {
            TextFieldDoctor(
                value = "",
                onValueChange = {},
                modifier = Modifier,
                label = "Diagnosis"
            )
            TextFieldDoctor(
                "",
                onValueChange = {},
                modifier = Modifier,
                label = "Prognosis"
            )
            TextFieldDoctor(
                value = "",
                onValueChange = {},
                modifier = Modifier,
                label = "Treatment"
            )
            TextFieldDoctor(
                value = "",
                onValueChange = {},
                modifier = Modifier,
                label = "Recommendations"
            )
            TextFieldDoctor(
                value = "",
                onValueChange = {},
                modifier = Modifier,
                label = "Plan of Care"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Handle button click */ },
                modifier = Modifier.width(200.dp).height(40.dp)
            ) {
                Text("Submit medical report")
            }
        }
        }
    }
}

@Preview
@Composable
fun CreateMedicalReportScreenPreview(){
    CareConnectTheme {
        CreateMedicalReportScreenContent(
            patientId = "123"
        )
    }
}