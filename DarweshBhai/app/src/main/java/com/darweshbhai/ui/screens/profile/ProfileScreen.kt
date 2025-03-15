package com.darweshbhai.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var strictMode by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("English") }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile & Settings",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // User Profile Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "User Name",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Text(
                            text = "user@example.com",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            item {
                // App Settings Section
                SettingsSection(title = "App Settings") {
                    // Theme Toggle
                    SettingsItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Switch between light and dark theme",
                        trailing = {
                            Switch(
                                checked = darkMode,
                                onCheckedChange = { darkMode = it }
                            )
                        }
                    )

                    // Notifications Toggle
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Enable or disable app notifications",
                        trailing = {
                            Switch(
                                checked = notifications,
                                onCheckedChange = { notifications = it }
                            )
                        }
                    )

                    // Language Selection
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = language,
                        onClick = { showLanguageDialog = true }
                    )
                }
            }

            item {
                // Digital Wellbeing Settings
                SettingsSection(title = "Digital Wellbeing") {
                    // Strict Mode Toggle
                    SettingsItem(
                        icon = Icons.Default.Security,
                        title = "Strict Mode",
                        subtitle = "Prevent easy bypassing of app limits",
                        trailing = {
                            Switch(
                                checked = strictMode,
                                onCheckedChange = { strictMode = it }
                            )
                        }
                    )

                    // Usage Stats
                    SettingsItem(
                        icon = Icons.Default.Timeline,
                        title = "Usage Statistics",
                        subtitle = "View your app usage data",
                        onClick = { /* Navigate to usage stats */ }
                    )

                    // Focus Settings
                    SettingsItem(
                        icon = Icons.Default.Timer,
                        title = "Focus Settings",
                        subtitle = "Customize focus mode preferences",
                        onClick = { /* Navigate to focus settings */ }
                    )
                }
            }

            item {
                // About Section
                SettingsSection(title = "About") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "App Version",
                        subtitle = "1.0.0"
                    )

                    SettingsItem(
                        icon = Icons.Default.Policy,
                        title = "Privacy Policy",
                        onClick = { /* Open privacy policy */ }
                    )

                    SettingsItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        onClick = { /* Open help section */ }
                    )
                }
            }
        }

        // Language Selection Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text("Select Language") },
                text = {
                    Column {
                        listOf("English", "Hindi", "Gujarati").forEach { lang ->
                            RadioButton(
                                selected = language == lang,
                                onClick = {
                                    language = lang
                                    showLanguageDialog = false
                                }
                            )
                            Text(lang)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            trailing?.let {
                Spacer(modifier = Modifier.width(16.dp))
                it()
            }
            
            if (onClick != null && trailing == null) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
