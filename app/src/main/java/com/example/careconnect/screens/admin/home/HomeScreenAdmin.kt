package com.example.careconnect.screens.admin.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme


// Overview admin screen
@Composable
fun HomeScreenAdmin(
){
    HomeScreenAdminContent()
}


@Composable
fun HomeScreenAdminContent(
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Card {
            Text(text = stringResource(R.string.overview))
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    CareConnectTheme {

    }
}
