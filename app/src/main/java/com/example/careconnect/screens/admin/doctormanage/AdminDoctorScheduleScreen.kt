package com.example.careconnect.screens.admin.doctormanage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.careconnect.ui.theme.CareConnectTheme
import java.time.LocalDate
import java.time.YearMonth


@Composable
fun AddDoctorScheduleScreen() {
    AddDoctorScheduleScreenContent(onNextStep = {}, onBack = {})
}

@Composable
fun AddDoctorScheduleScreenContent(onNextStep: () -> Unit, onBack: () -> Unit) {
    var selectedDays by remember { mutableStateOf(setOf<LocalDate>()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        SmallTopAppBarExample()

        Column(modifier = Modifier.fillMaxSize().padding(top = 100.dp, start = 16.dp, end = 16.dp)) {
            // Progress Stepper
            StepperIndicator(currentStep = 2)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Step 2: Select Work Days for the Doctor",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(25.dp))

            // Calendar View for Selecting Work Days
            CalendarSelector(selectedDays) { date ->
                selectedDays = if (selectedDays.contains(date)) {
                    selectedDays - date
                } else {
                    selectedDays + date
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onNextStep() }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Next Step")
                }

                IconButton(
                    onClick = { onNextStep() }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Step")
                }
            }
        }
    }
}


// 📅 Calendar Selector for Work Days
@Composable
fun CalendarSelector(selectedDays: Set<LocalDate>, onDaySelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    val currentMonth = YearMonth.of(today.year, today.month)
    val daysInMonth = currentMonth.lengthOfMonth()

    Column {
        Text("Select Available Work Days", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Display days in a grid
        Column {
            var dayIndex = 1
            while (dayIndex <= daysInMonth) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 1..7) {
                        if (dayIndex > daysInMonth) break
                        val date = today.withDayOfMonth(dayIndex)

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (selectedDays.contains(date)) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { onDaySelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = dayIndex.toString(), color = Color.White, fontSize = 14.sp)
                        }
                        dayIndex++
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Preview
@Composable
fun AdminDoctorScheduleScreenPreview() {
    CareConnectTheme {
        AddDoctorScheduleScreenContent(onNextStep = {}, onBack = {})
    }
}
