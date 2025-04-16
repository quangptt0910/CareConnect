package com.example.careconnect.screens.patient.home

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch
import java.time.LocalDate


data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val contentDescriptionResId: Int
)

@Composable
fun HomeScreenPatient(
    openSettingsScreen: () -> Unit,
    openDoctorsOverviewScreen: (specialty: String) -> Unit
) {
    HomeScreenPatientContent(
        uiState = HomeUiState(),
        openSettingsScreen = openSettingsScreen,
        onDoctorSelected = { _, _ -> },
        onSearchQueryChange = {},
        openDoctorsOverviewScreen = openDoctorsOverviewScreen,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenPatientContent(
    uiState: HomeUiState,
    openSettingsScreen: () -> Unit,
    onDoctorSelected: (Doctor, Boolean) -> Unit = { _, _ -> },
    onSearchQueryChange: (String) -> Unit = {},
    openDoctorsOverviewScreen: (specialty: String) -> Unit = {}

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
                        text = stringResource(R.string.specialization),
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
                                    .clickable(
                                        onClick = {
                                            openDoctorsOverviewScreen(specialty)
                                        }
                                    )
                            )
                            HorizontalDivider()
                        }
                    }
                }
            },
            sheetPeekHeight = 400.dp, // The visible height when collapsed
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

                    Spacer(modifier = Modifier.width(8.dp))

                    // Go to notifications screen
                    IconButton(
                        onClick = { scope.launch { sheetState.expand() } },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ){
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                    }

                    // Go to settings screen
                    IconButton(
                        onClick = { openSettingsScreen() },
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

                SearchSection(
                    uiState = uiState,
                    onDoctorSelected = onDoctorSelected,
                    onSearchQueryChange = onSearchQueryChange
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

                    Column(
                        modifier = Modifier.fillMaxWidth(),

                    ){
                        Image(
                            modifier = Modifier.height(205.dp)
                                .maskClip(MaterialTheme.shapes.extraLarge),
                            painter = rememberAsyncImagePainter(model = item.imageResId),
                            contentDescription = stringResource(item.contentDescriptionResId),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = stringResource(item.contentDescriptionResId),
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                }
            }
        }
    }
}



@Preview
@Composable
fun HomeScreenPatientPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        HomeScreenPatientContent(
            uiState = uiState,
            openSettingsScreen = {}
        )
    }
}