package com.example.careconnect.screens.home

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.careconnect.R
import com.example.careconnect.common.ext.fieldModifier
import com.example.careconnect.screens.login.EmailField
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.careconnect.R.string as AppText

data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val contentDescriptionResId: Int
)

@Composable
fun HomeScreen(

) {

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onSettingsClick: () -> Unit,

) {

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded, // Starts in collapsed mode
        skipHiddenState = true // Prevents dismissal
    )
    val scope = rememberCoroutineScope()

    // List of doctor specialties
    val specialties = listOf(
        "Cardiologist", "Dermatologist", "Neurologist", "Pediatrician",
        "Orthopedic Surgeon", "Gynecologist", "Ophthalmologist", "Dentist"
    )
    val date = LocalDate.now()

    val items =
        listOf(
            CarouselItem(0, R.drawable.carousel_image_1, R.string.carousel_image_1_description),
            CarouselItem(1, R.drawable.carousel_image_2, R.string.carousel_image_2_description),
            CarouselItem(2, R.drawable.carousel_image_3, R.string.carousel_image_3_description),
            CarouselItem(3, R.drawable.carousel_image_4, R.string.carousel_image_4_description),
            CarouselItem(4, R.drawable.carousel_image_5, R.string.carousel_image_5_description),
        )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)

                ) {
                    Text(
                        text = "Doctor Specialties",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(0.5f) // Expandable up to 50% screen height
                    ) {
                        items(specialties) { specialty ->
                            Text(
                                text = specialty,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                }
            },
            sheetPeekHeight = 300.dp, // The visible height when collapsed
            scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
        ) { paddingValues ->
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


                HorizontalMultiBrowseCarousel(
                    state = rememberCarouselState { items.count() },
                    modifier = Modifier.width(412.dp).height(221.dp),
                    preferredItemWidth = 186.dp,
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) { i ->
                    val item = items[i]
                    Image(
                        modifier = Modifier.height(205.dp)
                            .maskClip(MaterialTheme.shapes.extraLarge),
                        painter = painterResource(id = item.imageResId),
                        contentDescription = stringResource(item.contentDescriptionResId),
                        contentScale = ContentScale.Crop
                    )
                }
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