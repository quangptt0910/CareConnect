package com.example.careconnect.screens.doctor.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ScheduleScreen(){
    ScheduleScreenContent()
}

@Composable
fun ScheduleScreenContent(){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){

    }

}

@Preview
@Composable
fun ScheduleScreenPreview(){
    ScheduleScreenContent()
}