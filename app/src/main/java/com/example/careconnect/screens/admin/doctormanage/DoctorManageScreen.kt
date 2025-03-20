package com.example.careconnect.screens.admin.doctormanage

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.careconnect.R
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.screens.home.CarouselItem
import com.example.careconnect.screens.home.HomeUiState
import com.example.careconnect.screens.home.SearchSection
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch
import java.time.LocalDate


@Composable
fun DoctorManageScreen(

){

}


@Composable
fun DoctorManageScreenContent(
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Doctors",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val doctors = listOf(
                Doctor("John", "Doe", "133","123","123","Cardiologist"),
                Doctor("Jane", "Smith","133","123","123", "Dermatologist"),
                Doctor("Bob", "Johnson","133","123","123", "Neurologist"),
                Doctor("Alice", "Williams","133","123","123", "Pediatrician"),
                Doctor("David", "Brown","133","123","123", "Orthopedic Surgeon"),
                Doctor("Emily", "Jones","133","123","123", "Gynecologist"),
                //Doctor("Michael", "Davis","133","123","123", "Ophthalmologist"),
                //Doctor("Sarah", "Miller","133","123","123", "Dentist")
            )


            FilledCardExample(
                title = "Doctors",
                userProducts = doctors,
                onDeleteProduct = {}
            )

            FilledCardStats(
                title = "Total hours worked",
                userProducts = doctors,
                onDeleteProduct = {}
            )
        }



    }
}


@Preview
@Composable
fun DoctorManageScreenPreview() {
    CareConnectTheme {
        val uiState = HomeUiState()
        DoctorManageScreenContent(
        )
    }
}