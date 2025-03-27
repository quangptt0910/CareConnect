package com.example.careconnect.screens.patient.doctorsoverview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.screens.patient.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun DoctorsOverviewScreen(

){

}


@Composable
fun DoctorsOverviewScreenContent(
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SmallTopAppBarExample2()
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 100.dp)
        ){
            FilledCardExample(
                modifier = Modifier,
                name = "Dr. John Smith",
                speciality = "Cardiologist",
                address = "123 Drive",
                imageRes = R.drawable.carousel_image_1
            )

            Spacer(modifier = Modifier.height(20.dp))

            FilledCardExample(
                modifier = Modifier,
                name = "Dr. Lily Jones",
                speciality = "Cardiologist",
                address = "1234 Drive",
                imageRes = R.drawable.carousel_image_2
            )
        }
    }
}


@Preview
@Composable
fun DoctorsOverviewScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        DoctorsOverviewScreenContent(
        )
    }
}