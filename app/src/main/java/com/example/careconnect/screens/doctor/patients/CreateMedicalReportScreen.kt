package com.example.careconnect.screens.doctor.patients

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun CreateMedicalReportScreen(

){
    CreateMedicalReportScreenContent()
}

@Composable
fun CreateMedicalReportScreenContent(
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                TextFieldDoctor(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier,
                    label = "Symptoms"
                )
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
        CreateMedicalReportScreenContent()
    }
}