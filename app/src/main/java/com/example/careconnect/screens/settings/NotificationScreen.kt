package com.example.careconnect.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Displays the Notification Settings screen where users can configure
 * notification preferences for chat messages and appointments.
 *
 * This screen allows toggling notification enabling, sounds, vibrations,
 * previews, and specific appointment notification types such as confirmations,
 * reminders, cancellations, and completions. It also allows setting the
 * reminder time before appointments and sending a test notification.
 *
 * @param onNavigateBack Callback to handle navigation back action.
 * @param viewModel The [NotificationViewModel] that holds UI state and handles
 * updates to notification settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Chat Notifications Section
            NotificationSection(
                title = "Chat Messages",
                icon = Icons.Default.Chat,
                enabled = uiState.settings.chatNotifications.enabled,
                onEnabledChange = { enabled ->
                    viewModel.updateChatSettings(
                        uiState.settings.chatNotifications.copy(enabled = enabled)
                    )
                }
            ) {
                SettingItem(
                    title = "Sound",
                    subtitle = "Play sound for new messages",
                    icon = Icons.Default.VolumeUp,
                    checked = uiState.settings.chatNotifications.sound,
                    enabled = uiState.settings.chatNotifications.enabled,
                    onCheckedChange = { sound ->
                        viewModel.updateChatSettings(
                            uiState.settings.chatNotifications.copy(sound = sound)
                        )
                    }
                )

                SettingItem(
                    title = "Vibration",
                    subtitle = "Vibrate for new messages",
                    icon = Icons.Default.Vibration,
                    checked = uiState.settings.chatNotifications.vibration,
                    enabled = uiState.settings.chatNotifications.enabled,
                    onCheckedChange = { vibration ->
                        viewModel.updateChatSettings(
                            uiState.settings.chatNotifications.copy(vibration = vibration)
                        )
                    }
                )

                SettingItem(
                    title = "Show Preview",
                    subtitle = "Display message content in notification",
                    icon = Icons.Default.Preview,
                    checked = uiState.settings.chatNotifications.showPreview,
                    enabled = uiState.settings.chatNotifications.enabled,
                    onCheckedChange = { preview ->
                        viewModel.updateChatSettings(
                            uiState.settings.chatNotifications.copy(showPreview = preview)
                        )
                    }
                )
            }

            // Appointment Notifications Section
            NotificationSection(
                title = "Appointments",
                icon = Icons.Default.Event,
                enabled = uiState.settings.appointmentNotifications.enabled,
                onEnabledChange = { enabled ->
                    viewModel.updateAppointmentSettings(
                        uiState.settings.appointmentNotifications.copy(enabled = enabled)
                    )
                }
            ) {
                SettingItem(
                    title = "Sound",
                    subtitle = "Play sound for appointment updates",
                    icon = Icons.Default.VolumeUp,
                    checked = uiState.settings.appointmentNotifications.sound,
                    enabled = uiState.settings.appointmentNotifications.enabled,
                    onCheckedChange = { sound ->
                        viewModel.updateAppointmentSettings(
                            uiState.settings.appointmentNotifications.copy(sound = sound)
                        )
                    }
                )

                SettingItem(
                    title = "Vibration",
                    subtitle = "Vibrate for appointment updates",
                    icon = Icons.Default.Vibration,
                    checked = uiState.settings.appointmentNotifications.vibration,
                    enabled = uiState.settings.appointmentNotifications.enabled,
                    onCheckedChange = { vibration ->
                        viewModel.updateAppointmentSettings(
                            uiState.settings.appointmentNotifications.copy(vibration = vibration)
                        )
                    }
                )

                // Divider for notification types
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Text(
                    text = "Notification Types",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SettingItem(
                    title = "Confirmations",
                    subtitle = "When appointments are confirmed",
                    icon = Icons.Default.CheckCircle,
                    checked = uiState.settings.appointmentNotifications.confirmations,
                    enabled = uiState.settings.appointmentNotifications.enabled,
                    onCheckedChange = { confirmations ->
                        viewModel.updateAppointmentSettings(
                            uiState.settings.appointmentNotifications.copy(confirmations = confirmations)
                        )
                    }
                )

                SettingItem(
                    title = "Reminders",
                    subtitle = "Upcoming appointment reminders",
                    icon = Icons.Default.Alarm,
                    checked = uiState.settings.appointmentNotifications.reminders,
                    enabled = uiState.settings.appointmentNotifications.enabled,
                    onCheckedChange = { reminders ->
                        viewModel.updateAppointmentSettings(
                            uiState.settings.appointmentNotifications.copy(reminders = reminders)
                        )
                    }
                )

                SettingItem(
                    title = "Cancellations",
                    subtitle = "When appointments are cancelled",
                    icon = Icons.Default.Cancel,
                    checked = uiState.settings.appointmentNotifications.cancellations,
                    enabled = uiState.settings.appointmentNotifications.enabled,
                    onCheckedChange = { cancellations ->
                        viewModel.updateAppointmentSettings(
                            uiState.settings.appointmentNotifications.copy(cancellations = cancellations)
                        )
                    }
                )

                SettingItem(
                    title = "Completions",
                    subtitle = "When appointments are completed",
                    icon = Icons.Default.Done,
                    checked = uiState.settings.appointmentNotifications.completions,
                    enabled = uiState.settings.appointmentNotifications.enabled,
                    onCheckedChange = { completions ->
                        viewModel.updateAppointmentSettings(
                            uiState.settings.appointmentNotifications.copy(completions = completions)
                        )
                    }
                )

                // Reminder timing
                ReminderTimingSelector(
                    selectedMinutes = uiState.settings.appointmentNotifications.reminderTimeBefore,
                    enabled = uiState.settings.appointmentNotifications.enabled &&
                            uiState.settings.appointmentNotifications.reminders,
                    onTimeSelected = { minutes ->
                        viewModel.updateAppointmentSettings(
                            uiState.settings.appointmentNotifications.copy(reminderTimeBefore = minutes)
                        )
                    }
                )
            }

            // Test notification button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Test Notifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Send a test notification to verify your settings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.sendTestNotification(context)
                        }
                    ) {
                        Text("Test")
                    }
                }
            }
        }
    }
}

/**
 * Represents a notification settings section with a title, icon, and
 * an enable/disable switch. If enabled, the detailed settings content
 * will be displayed.
 *
 * @param title The title of the notification section (e.g., "Chat Messages").
 * @param icon The icon representing the section.
 * @param enabled Whether the section's notifications are enabled.
 * @param onEnabledChange Callback invoked when the enabled state changes.
 * @param content The detailed settings composable content displayed if enabled.
 */
@Composable
fun NotificationSection(
    title: String,
    icon: ImageVector,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange
                )
            }

            if (enabled) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

/**
 * Displays a single notification setting item with a title, subtitle,
 * icon, and a toggle switch.
 *
 * This composable reflects the enabled state and disables interaction
 * when the parent notification section is disabled.
 *
 * @param title The setting title (e.g., "Sound").
 * @param subtitle A description or explanation of the setting.
 * @param icon Icon representing the setting.
 * @param checked Whether the setting is enabled.
 * @param enabled Whether the setting is interactable (typically the parent section's enabled state).
 * @param onCheckedChange Callback triggered when the toggle state changes.
 */
@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (enabled) 1f else 0.38f
                    )
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}


/**
 * Provides a dropdown selector for choosing the reminder timing before
 * an appointment notification.
 *
 * The dropdown is enabled only if reminders and appointment notifications
 * are enabled.
 *
 * @param selectedMinutes The currently selected reminder time in minutes.
 * @param enabled Whether the selector is enabled for user interaction.
 * @param onTimeSelected Callback invoked when a new time is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTimingSelector(
    selectedMinutes: Int,
    enabled: Boolean,
    onTimeSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        15 to "15 minutes",
        30 to "30 minutes",
        60 to "1 hour",
        120 to "2 hours",
        1440 to "1 day"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Column {
                Text(
                    text = "Reminder Time",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
                Text(
                    text = "How far before the appointment",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (enabled) 1f else 0.38f
                    )
                )
            }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded && enabled },
            modifier = Modifier.width(120.dp)
        ) {
            OutlinedTextField(
                value = options.find { it.first == selectedMinutes }?.second ?: "30 minutes",
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (minutes, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onTimeSelected(minutes)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Preview composable for [NotificationSettingsScreen] to visualize the UI
 * during development.
 */
@Preview(showBackground = true)
@Composable
fun NotificationSettingsScreenPreview() {
    MaterialTheme {
        NotificationSettingsScreen(onNavigateBack = {})
    }
}