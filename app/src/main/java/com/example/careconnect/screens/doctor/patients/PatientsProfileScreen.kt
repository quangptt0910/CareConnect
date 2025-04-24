package com.example.careconnect.screens.doctor.patients

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun PatientsProfileScreen(

){
    PatientsProfileScreenContent()
}

@Composable
fun PatientsProfileScreenContent(
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){

    }
}

@Preview
@Composable
fun PatientsProfileScreenPreview(){
    CareConnectTheme {
        PatientsProfileScreenContent()
    }
}

