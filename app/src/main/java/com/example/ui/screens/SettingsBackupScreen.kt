package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.AppSettings
import com.example.data.local.LockType
import com.example.data.local.ThemeMode
import com.example.localization.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBackupScreen(
    settings: AppSettings,
    onSetLanguage: (String) -> Unit,
    onSetThemeMode: (String) -> Unit,
    onSetLockCredential: (String, String, String) -> Unit,
    onDisableLock: (String) -> Unit,
    onLockNow: () -> Unit,
    onExportBackupToUri: (Uri, String, String) -> Unit,
    onShareBackup: (String) -> Unit,
    onRestoreBackupFromUri: (Uri, String, String) -> Unit,
    onBack: () -> Unit
) {
    val s = LocalAppStrings.current
    BackHandler(onBack = onBack)

    var showSetupLockDialog by remember { mutableStateOf(false) }

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let {
                onExportBackupToUri(it, s.backupSavedSuccess, s.backupError)
            }
        }
    )

    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                onRestoreBackupFromUri(it, s.restoreSuccess, s.backupError)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.settingsTitle) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("settings_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.close)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .widthIn(max = 760.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Language Selection
            item {
                SettingsSectionCard(
                    icon = Icons.Default.Translate,
                    title = s.languageSection
                ) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = settings.language == "hi",
                            onClick = { onSetLanguage("hi") },
                            shape = SegmentedButtonDefaults.itemShape(0, 2),
                            modifier = Modifier.testTag("lang_hi_button")
                        ) { Text(s.hindiLabel) }

                        SegmentedButton(
                            selected = settings.language == "en",
                            onClick = { onSetLanguage("en") },
                            shape = SegmentedButtonDefaults.itemShape(1, 2),
                            modifier = Modifier.testTag("lang_en_button")
                        ) { Text(s.englishLabel) }
                    }
                }
            }

            // 2. Theme Mode
            item {
                SettingsSectionCard(
                    icon = Icons.Default.DarkMode,
                    title = s.themeSection
                ) {
                    val modes = listOf(
                        ThemeMode.SYSTEM to s.themeSystem,
                        ThemeMode.LIGHT to s.themeLight,
                        ThemeMode.DARK to s.themeDark
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        modes.forEachIndexed { idx, (key, label) ->
                            SegmentedButton(
                                selected = settings.themeMode == key,
                                onClick = { onSetThemeMode(key) },
                                shape = SegmentedButtonDefaults.itemShape(idx, modes.size),
                                modifier = Modifier.testTag("theme_$key")
                            ) {
                                Text(label, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // 3. PIN / Password Security Lock
            item {
                SettingsSectionCard(
                    icon = if (settings.lockEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                    title = s.securityLockSection
                ) {
                    if (!settings.lockEnabled) {
                        Button(
                            onClick = { showSetupLockDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("enable_lock_button")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(s.enableAppLock)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                FilledTonalButton(
                                    onClick = { showSetupLockDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("change_lock_button")
                                ) {
                                    Text(s.changeLockCredential)
                                }
                                OutlinedButton(
                                    onClick = { onDisableLock(s.lockDisabledSuccess) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("disable_lock_button")
                                ) {
                                    Text(s.disableLock)
                                }
                            }
                            Button(
                                onClick = onLockNow,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("lock_now_button")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(s.lockApp)
                            }
                        }
                    }
                }
            }

            // 4. Backup & Download Data
            item {
                SettingsSectionCard(
                    icon = Icons.Default.CloudDownload,
                    title = s.backupSection
                ) {
                    Text(
                        text = s.backupDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Button(
                        onClick = {
                            createBackupLauncher.launch("MyLife_Backup_${System.currentTimeMillis()}.json")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("download_backup_button")
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(s.downloadBackupJson)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onShareBackup(s.backupError) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("share_backup_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(s.shareBackupJson)
                        }
                        OutlinedButton(
                            onClick = {
                                restoreBackupLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restore_backup_button")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(s.restoreBackupJson)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }

    if (showSetupLockDialog) {
        SetupLockCredentialDialog(
            initialType = settings.lockType,
            onConfirm = { type, secret ->
                onSetLockCredential(type, secret, s.lockEnabledSuccess)
                showSetupLockDialog = false
            },
            onDismiss = { showSetupLockDialog = false }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.secondary)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

@Composable
private fun SetupLockCredentialDialog(
    initialType: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    var lockType by remember { mutableStateOf(initialType) }
    var secret by remember { mutableStateOf("") }
    var confirmSecret by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(s.securityLockSection, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(s.lockMethod, style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = lockType == LockType.PIN,
                        onClick = {
                            lockType = LockType.PIN
                            secret = ""
                            confirmSecret = ""
                            errorMsg = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) { Text(s.pinOption) }
                    SegmentedButton(
                        selected = lockType == LockType.PASSWORD,
                        onClick = {
                            lockType = LockType.PASSWORD
                            secret = ""
                            confirmSecret = ""
                            errorMsg = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) { Text(s.passwordOption) }
                }

                val isPin = lockType == LockType.PIN
                OutlinedTextField(
                    value = secret,
                    onValueChange = { value ->
                        secret = if (isPin) value.filter { it.isDigit() } else value
                        errorMsg = null
                    },
                    label = { Text(if (isPin) s.setNewPin else s.setNewPassword) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isPin) KeyboardType.NumberPassword else KeyboardType.Password
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_lock_secret_input")
                )

                OutlinedTextField(
                    value = confirmSecret,
                    onValueChange = { value ->
                        confirmSecret = if (isPin) value.filter { it.isDigit() } else value
                        errorMsg = null
                    },
                    label = { Text(s.confirmCredential) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isPin) KeyboardType.NumberPassword else KeyboardType.Password
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_lock_confirm_input")
                )

                errorMsg?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (secret.length < 4) {
                                errorMsg = if (isPin) s.pinMinLengthError else s.passwordMinLengthError
                                return@Button
                            }
                            if (secret != confirmSecret) {
                                errorMsg = s.credentialsMismatch
                                return@Button
                            }
                            onConfirm(lockType, secret)
                        },
                        modifier = Modifier.testTag("save_lock_button")
                    ) {
                        Text(s.save)
                    }
                }
            }
        }
    }
}
