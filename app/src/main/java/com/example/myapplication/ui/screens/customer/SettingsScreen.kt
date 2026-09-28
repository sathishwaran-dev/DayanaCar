package com.example.myapplication.ui.screens.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()
    val isNotificationsEnabled by mainViewModel.isNotificationsEnabled.collectAsState()
    val selectedLanguage by mainViewModel.selectedLanguage.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Section
            Text("Account Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsActionRow(
                        title = "Edit Profile",
                        subtitle = "Update Full Name",
                        icon = Icons.Default.Edit,
                        onClick = { navController.navigate(Screen.EditProfile.route) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    SettingsActionRow(
                        title = "Change Mobile Number",
                        subtitle = "Update registered phone number",
                        icon = Icons.Default.Phone,
                        onClick = { navController.navigate(Screen.EditProfile.route) }
                    )
                }
            }

            // Preferences Section
            Text("App Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsSwitchRow(
                        title = "Push Notifications",
                        subtitle = "Booking & offer alerts",
                        icon = Icons.Default.Notifications,
                        checked = isNotificationsEnabled,
                        onCheckedChange = { mainViewModel.setNotificationsEnabled(it) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    SettingsSwitchRow(
                        title = "Dark Mode Theme",
                        subtitle = "Switch dark/light Material 3 theme",
                        icon = Icons.Default.DarkMode,
                        checked = isDarkMode,
                        onCheckedChange = { mainViewModel.setDarkMode(it) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    SettingsActionRow(
                        title = "Language",
                        subtitle = selectedLanguage,
                        icon = Icons.Default.Language,
                        onClick = { showLanguageDialog = true }
                    )
                }
            }

            // Security & Legal Section
            Text("Security & Legal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsActionRow(
                        title = "Privacy & Security",
                        subtitle = "Data protection & encrypted security",
                        icon = Icons.Default.Security,
                        onClick = { navController.navigate(Screen.PrivacySecurity.route) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    SettingsActionRow(
                        title = "Help & Support",
                        subtitle = "Contact customer care & support",
                        icon = Icons.Default.Help,
                        onClick = { navController.navigate(Screen.HelpSupport.route) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    SettingsActionRow(
                        title = "About DayanaCar",
                        subtitle = "App info, version & release notes",
                        icon = Icons.Default.Info,
                        onClick = { navController.navigate(Screen.AboutApp.route) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    SettingsActionRow(
                        title = "Terms & Conditions",
                        subtitle = "User agreement & usage terms",
                        icon = Icons.Default.Gavel,
                        onClick = { navController.navigate(Screen.TermsConditions.route) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    SettingsActionRow(
                        title = "Privacy Policy",
                        subtitle = "Customer data privacy disclosure",
                        icon = Icons.Default.Policy,
                        onClick = { navController.navigate(Screen.PrivacyPolicy.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showLanguageDialog) {
        val languages = listOf("English", "Tamil", "Hindi", "Spanish")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select App Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    mainViewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang,
                                onClick = {
                                    mainViewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary, checkedTrackColor = GreenPrimary.copy(alpha = 0.3f))
        )
    }
}
