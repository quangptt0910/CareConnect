package com.example.careconnect.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object HomeRoute

data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val contentDescriptionResId: Int
)

@Composable
fun HomeScreen(

) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onSettingsClick: () -> Unit,

) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val date = LocalDate.now()

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Welcome back!",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

                // Go to settings screen
                IconButton(
                    onClick = { onSettingsClick() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            }

            Text(
                text = "$date",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))




            val items =
                listOf(
                    CarouselItem(0, R.drawable.carousel_image_1, R.string.carousel_image_1_description),
                    CarouselItem(1, R.drawable.carousel_image_2, R.string.carousel_image_2_description),
                    CarouselItem(2, R.drawable.carousel_image_3, R.string.carousel_image_3_description),
                    CarouselItem(3, R.drawable.carousel_image_4, R.string.carousel_image_4_description),
                    CarouselItem(4, R.drawable.carousel_image_5, R.string.carousel_image_5_description),
                )

            HorizontalMultiBrowseCarousel(
                state = rememberCarouselState { items.count() },
                modifier = Modifier.width(412.dp).height(221.dp),
                preferredItemWidth = 186.dp,
                itemSpacing = 8.dp,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) { i ->
                val item = items[i]
                Image(
                    modifier = Modifier.height(205.dp).maskClip(MaterialTheme.shapes.extraLarge),
                    painter = painterResource(id = item.imageResId),
                    contentDescription = stringResource(item.contentDescriptionResId),
                    contentScale = ContentScale.Crop
                )
            }
        }
    
    }
}




@Preview
@Composable
fun HomeScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        HomeScreenContent(
            uiState = uiState,
            onSettingsClick = {}
        )
    }
}