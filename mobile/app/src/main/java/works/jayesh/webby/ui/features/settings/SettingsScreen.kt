package works.jayesh.webby.ui.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit = {}, modifier: Modifier = Modifier) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    var smsNotifications by remember { mutableStateOf(false) }
    var pushNotifications by remember { mutableStateOf(true) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Settings") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            }
    ) { padding ->
        LazyColumn(modifier = modifier.fillMaxSize().padding(padding)) {
            // Account Section
            item { SectionHeader("Account") }

            item {
                SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        subtitle = "Change your name, email, phone",
                        onClick = {}
                )
            }

            item {
                SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        subtitle = "Update your password",
                        onClick = {}
                )
            }

            item {
                SettingsItem(
                        icon = Icons.Default.LocationOn,
                        title = "Manage Addresses",
                        subtitle = "Edit delivery addresses",
                        onClick = {}
                )
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // Notifications Section
            item { SectionHeader("Notifications") }

            item {
                SwitchSettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Enable Notifications",
                        subtitle = "Receive all notifications",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                )
            }

            if (notificationsEnabled) {
                item {
                    SwitchSettingsItem(
                            icon = Icons.Default.Email,
                            title = "Email Notifications",
                            subtitle = "Get notified via email",
                            checked = emailNotifications,
                            onCheckedChange = { emailNotifications = it }
                    )
                }

                item {
                    SwitchSettingsItem(
                            icon = Icons.Default.Sms,
                            title = "SMS Notifications",
                            subtitle = "Get notified via SMS",
                            checked = smsNotifications,
                            onCheckedChange = { smsNotifications = it }
                    )
                }

                item {
                    SwitchSettingsItem(
                            icon = Icons.Default.Notifications,
                            title = "Push Notifications",
                            subtitle = "Get notified on your device",
                            checked = pushNotifications,
                            onCheckedChange = { pushNotifications = it }
                    )
                }
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // Preferences Section
            item { SectionHeader("Preferences") }

            item {
                SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = "English (US)",
                        onClick = {}
                )
            }

            item {
                SettingsItem(
                        icon = Icons.Default.AttachMoney,
                        title = "Currency",
                        subtitle = "USD ($)",
                        onClick = {}
                )
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // Other Section
            item { SectionHeader("Other") }

            item {
                SettingsItem(
                        icon = Icons.Default.Info,
                        title = "About",
                        subtitle = "Version 1.0.0",
                        onClick = {}
                )
            }

            item {
                SettingsItem(
                        icon = Icons.Default.Description,
                        title = "Terms & Conditions",
                        subtitle = "Read our terms",
                        onClick = {}
                )
            }

            item {
                SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        subtitle = "Read our privacy policy",
                        onClick = {}
                )
            }

            item {
                SettingsItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        subtitle = "Get help with your account",
                        onClick = {}
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        title: String,
        subtitle: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(subtitle) },
            leadingContent = {
                Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
fun SwitchSettingsItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        title: String,
        subtitle: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
) {
    ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(subtitle) },
            leadingContent = {
                Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
            modifier = modifier
    )
}
