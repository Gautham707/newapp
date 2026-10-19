package com.example.ui.screens

import androidx.activity.compose.BackHandler

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.GlassPanel
import com.example.ui.components.InfoDialog
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryLight
import com.example.ui.theme.RoseDue
import com.example.viewmodel.MyDueViewModel
import com.example.viewmodel.Screen

@Composable
fun ProfileScreen(
    viewModel: MyDueViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // USER PROFILE CARD - FROSTED GLASS
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(26.dp),
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Glass Avatar Squircle
                    val initials = if (userProfile.name.isNotBlank()) userProfile.name.take(2).uppercase() else "OP"
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (userProfile.name.isNotBlank()) userProfile.name else "Business Owner",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("profile_user_name")
                        )
                        Text(
                            text = userProfile.title,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (userProfile.email.isNotBlank()) {
                            Text(
                                text = userProfile.email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Glass Edit Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .clickable { showEditProfileDialog = true }
                            .testTag("edit_profile_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // SETTINGS LIST CARD - FROSTED GLASS
        item {
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_list_card"),
                shape = RoundedCornerShape(26.dp),
                elevation = 6.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Glass Section Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "PREFERENCES & CONFIGURATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        // 1. Dark Mode
                        SettingRow(
                            icon = Icons.Default.Brightness4,
                            title = "Dark Mode",
                            subtitle = if (userProfile.isDarkMode) "Midnight Cosmic Glass" else "Frosted Pearl Light",
                            trailing = {
                                Switch(
                                    checked = userProfile.isDarkMode,
                                    onCheckedChange = { viewModel.toggleDarkMode() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.testTag("dark_mode_switch")
                                )
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // 2. Backup & Restore
                        SettingRow(
                            icon = Icons.Default.CloudDownload,
                            title = "Backup & Restore",
                            subtitle = "Export or restore JSON ledger file",
                            onClick = { showBackupDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // 3. Reset Records
                        SettingRow(
                            icon = Icons.Default.Refresh,
                            title = "Reset Records",
                            subtitle = "Clear ledger & reload sample books",
                            onClick = { showResetDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // 4. App Language
                        SettingRow(
                            icon = Icons.Default.Language,
                            title = "App Language",
                            subtitle = userProfile.language,
                            onClick = { showLanguageDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // 5. Notifications
                        SettingRow(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            subtitle = "Due reminders & settlement alerts",
                            onClick = { showNotificationsDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // 6. Privacy & Security
                        SettingRow(
                            icon = Icons.Default.Lock,
                            title = "Privacy & Security",
                            subtitle = "100% on-device private storage",
                            onClick = { showPrivacyDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // 7. Help & FAQ
                        SettingRow(
                            icon = Icons.Default.Help,
                            title = "Help & Manual",
                            subtitle = "Usage guides and ledger workflow",
                            onClick = { showHelpDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // 8. Logout
                        SettingRow(
                            icon = Icons.Default.ExitToApp,
                            title = "Logout Session",
                            subtitle = "Terminate operator access",
                            iconTint = RoseDue,
                            onClick = { showLogoutDialog = true }
                        )
                    }
                }
            }
        }

        // APP INFO FOOTER - RETRO HARDWARE STAMP
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MYDUE • GLASS MORPHISM EDITION",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Frosted Encrypted Ledger • Offline & Private",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(72.dp))
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            name = userProfile.name,
            title = userProfile.title,
            email = userProfile.email,
            phone = userProfile.phone,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, title, email, phone ->
                viewModel.updateProfile(name, title, email, phone) {
                    showEditProfileDialog = false
                }
            }
        )
    }

    if (showBackupDialog) {
        var jsonContent by remember { mutableStateOf("") }
        var isExportMode by remember { mutableStateOf(true) }

        if (jsonContent.isEmpty() && isExportMode) {
            viewModel.exportBackup { jsonContent = it }
        }

        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = {
                Text(
                    text = "Backup & Restore",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                isExportMode = true
                                viewModel.exportBackup { jsonContent = it }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isExportMode) BluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isExportMode) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                isExportMode = false
                                jsonContent = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isExportMode) BluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (!isExportMode) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Import", fontSize = 12.sp)
                        }
                    }

                    if (isExportMode) {
                        Text(
                            text = "Your ledger data in JSON format:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = jsonContent,
                            onValueChange = {},
                            readOnly = true,
                            maxLines = 6,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MyDue Backup", jsonContent)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToast("Backup JSON copied to clipboard!")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimaryLight)
                        ) {
                            Text("Copy Backup to Clipboard")
                        }
                    } else {
                        Text(
                            text = "Paste JSON backup data to restore:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = jsonContent,
                            onValueChange = { jsonContent = it },
                            placeholder = { Text("Paste JSON here...") },
                            maxLines = 6,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                viewModel.importBackup(jsonContent) { success ->
                                    if (success) showBackupDialog = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            Text("Restore Records")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBackupDialog = false }) { Text("Close") }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Records?", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Text("This will restore the fresh starter dataset with Rose Hotel and AnnaStore sample accounts. Current custom edits will be replaced.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetData {
                            showResetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDue)
                ) {
                    Text("Reset All")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showLanguageDialog) {
        val languages = listOf("English", "हिन्दी (Hindi)", "മലയാളം (Malayalam)", "தமிழ் (Tamil)")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select App Language", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    languages.forEach { lang ->
                        val cleanLang = lang.split(" ").first()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(cleanLang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userProfile.language.startsWith(cleanLang),
                                onClick = {
                                    viewModel.setLanguage(cleanLang)
                                    showLanguageDialog = false
                                }
                            )
                            Text(lang, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Close") }
            }
        )
    }

    if (showNotificationsDialog) {
        InfoDialog(
            title = "Notification Preferences",
            content = "Due alerts are generated daily for outstanding credit accounts exceeding 7 days. You can also send instant payment reminder messages to customers directly via WhatsApp or SMS from their detail screen.",
            onDismiss = { showNotificationsDialog = false }
        )
    }

    if (showPrivacyDialog) {
        InfoDialog(
            title = "100% Offline & Private",
            content = "MyDue is completely offline. None of your customer names, phones, or credit ledgers are transmitted to third-party tracking servers. Data is encrypted and stored locally on your device with full JSON export capability.",
            onDismiss = { showPrivacyDialog = false }
        )
    }

    if (showHelpDialog) {
        InfoDialog(
            title = "MyDue - How it Works",
            content = "1. Add Due: Record credit whenever a customer purchases goods on account.\n\n2. Record Payment: Mark received cash, UPI, or bank payments.\n\n3. Ledger: Filter by Pending, Today, or Settled accounts.\n\n4. Backup: Regularly export JSON backups from Settings to keep a secure copy of your ledger.",
            onDismiss = { showHelpDialog = false }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout?", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = { Text("Are you sure you want to log out of your session? Your offline ledger records remain safe on your device.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDue)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = BluePrimaryLight,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.12f))
                    .border(BorderStroke(1.2.dp, iconTint.copy(alpha = 0.5f)), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (trailing != null) {
            trailing()
        }
    }
}
