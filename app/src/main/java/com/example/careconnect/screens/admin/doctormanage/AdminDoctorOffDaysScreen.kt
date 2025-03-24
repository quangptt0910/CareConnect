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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
fun AdminDoctorOffDaysScreen() {

}

@Composable
fun AdminDoctorOffDaysScreenContent(onNextStep: () -> Unit, onBack: () -> Unit) {
    var selectedOffDays by remember { mutableStateOf(setOf<LocalDate>()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {


        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Progress Stepper
            StepperIndicator(currentStep = 3)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Step 3: Select Doctor's Off Days",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Calendar View for Selecting Off Days
            CalendarSelector2(selectedOffDays) { date ->
                selectedOffDays = if (selectedOffDays.contains(date)) {
                    selectedOffDays - date
                } else {
                    selectedOffDays + date
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    Text("Back")
                }
                Button(onClick = { onNextStep() }) {
                    Text("Finish")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Finish Setup")
                }
            }
        }
    }
}



// 📅 Calendar Selector for Off Days
@Composable
fun CalendarSelector2(selectedDays: Set<LocalDate>, onDaySelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    val currentMonth = YearMonth.of(today.year, today.month)
    val daysInMonth = currentMonth.lengthOfMonth()

    Column {
        Text("Select Off Days", fontSize = 18.sp)
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
                                    if (selectedDays.contains(date)) Color.Red else Color.LightGray,
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
fun AdminDoctorOffDaysScreenPreview() {
    CareConnectTheme {
        AdminDoctorOffDaysScreenContent(onNextStep = {}, onBack = {})
    }
}
