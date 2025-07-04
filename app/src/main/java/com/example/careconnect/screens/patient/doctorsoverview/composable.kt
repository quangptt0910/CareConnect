package com.example.careconnect.screens.patient.doctorsoverview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

/**
 * A card UI component that displays a doctor's profile summary including
 * their name, speciality, address, and profile image. It also provides buttons
 * to view the doctor's full profile or request an appointment.
 *
 * @param modifier Modifier to be applied to the card.
 * @param name The full name of the doctor.
 * @param speciality The doctor's medical speciality.
 * @param address The doctor's address or clinic location.
 * @param doctorId Unique identifier for the doctor.
 * @param imageRes URL or resource string for the doctor's profile image.
 * @param openDoctorProfileScreen Lambda to invoke when the "View Profile" button is clicked.
 * @param openBookingScreen Lambda to invoke when the "Request Appointment" button is clicked.
 */
@Composable
fun FilledCardExample(
    modifier: Modifier = Modifier,
    name: String,
    speciality: String,
    address: String,
    doctorId: String,
    imageRes: String, // Resource ID of the profile image
    openDoctorProfileScreen: (doctorId: String) -> Unit = {},
    openBookingScreen: () -> Unit = {}
) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
        ListItem(
            modifier = Modifier.padding(8.dp),
            headlineContent = { Text(text = name) },
            supportingContent = { Text(text = speciality + "\n" + address) },
            leadingContent = {
                Image(
                    painter = rememberAsyncImagePainter(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(55.dp) // Ensures width and height are the same
                        .clip(CircleShape) // Clips the image into a circle
                )
            }
        )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(
                    onClick = { openDoctorProfileScreen(doctorId) },
                    modifier = Modifier.padding(15.dp).height(40.dp)
                ){
                    Text(text = "View Profile")
                }

                Button(
                    onClick = { openBookingScreen() },
                    modifier = Modifier.padding(15.dp).height(40.dp)
                ){
                    Text(text = "Request Appointment")
                }
            }

    }
}

/**
 * A custom action button with an icon and text label, styled with
 * elevation and rounded corners.
 *
 * @param text The label text displayed on the button.
 * @param icon The icon displayed alongside the text.
 * @param onClick Callback triggered when the button is clicked.
 */
@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(150.dp)
            .height(60.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * A top app bar specifically for the Doctors Overview screen,
 * providing a title and a back navigation icon.
 *
 * @param goBack Lambda invoked when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsOverviewTopBar(
    goBack: () -> Unit = {}
) {
            TopAppBar(
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(
                        "View Doctors",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
}