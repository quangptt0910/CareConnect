package com.example.careconnect.screens.admin.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.screens.home.HomeUiState
import com.example.careconnect.ui.theme.CareConnectTheme


@Composable
fun HomeScreenAdmin(

){

}


@Composable
fun HomeScreenAdminContent(
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

    DetailedDrawerExample {  }



    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        HomeScreenAdminContent(
        )
    }
}