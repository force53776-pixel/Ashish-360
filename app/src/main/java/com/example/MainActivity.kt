package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.ThemeMode
import com.example.localization.LocalAppStrings
import com.example.localization.StringsEn
import com.example.localization.StringsHi
import com.example.ui.components.EditPersonalProfileDialog
import com.example.ui.screens.AboutMeScreen
import com.example.ui.screens.DreamsGoalsScreen
import com.example.ui.screens.FamilyEducationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.MemoriesScreen
import com.example.ui.screens.SettingsBackupScreen
import com.example.ui.theme.MyLifeTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isUnlocked by viewModel.isUnlocked.collectAsStateWithLifecycle()
            val lockError by viewModel.lockError.collectAsStateWithLifecycle()

            val strings = if (uiState.settings.language == "en") StringsEn else StringsHi
            val darkTheme = when (uiState.settings.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                else -> isSystemInDarkTheme()
            }

            CompositionLocalProvider(LocalAppStrings provides strings) {
                MyLifeTheme(darkTheme = darkTheme) {
                    if (!isUnlocked && uiState.settings.lockEnabled) {
                        LockScreen(
                            lockType = uiState.settings.lockType,
                            userName = uiState.personalProfile.fullName,
                            userPhotoPath = uiState.personalProfile.photoPath,
                            hasError = lockError,
                            onClearError = viewModel::clearLockError,
                            onAttemptUnlock = viewModel::attemptUnlock
                        )
                    } else {
                        MyLifeAppShell(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

private enum class AppTab(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", Icons.Filled.Home, Icons.Outlined.Home),
    ABOUT_ME("about_me", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    FAMILY_EDU("family_edu", Icons.Filled.FamilyRestroom, Icons.Outlined.FamilyRestroom),
    MEMORIES("memories", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary),
    DREAMS("dreams", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLifeAppShell(viewModel: MainViewModel) {
    val s = LocalAppStrings.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var familySubTab by remember { mutableIntStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    var showEditPersonalDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShareBackupText -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_SUBJECT, "My Life – मेरी पहचान Backup")
                        putExtra(Intent.EXTRA_TEXT, event.jsonContent)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, s.shareBackupJson))
                }
            }
        }
    }

    if (currentTab != AppTab.HOME && !showSettings) {
        BackHandler { currentTab = AppTab.HOME }
    }

    if (showSettings) {
        SettingsBackupScreen(
            settings = uiState.settings,
            onSetLanguage = viewModel::setLanguage,
            onSetThemeMode = viewModel::setThemeMode,
            onSetLockCredential = viewModel::setLockCredential,
            onDisableLock = viewModel::disableLock,
            onLockNow = {
                showSettings = false
                viewModel.lockNow()
            },
            onExportBackupToUri = viewModel::exportBackupToUri,
            onShareBackup = viewModel::shareBackup,
            onRestoreBackupFromUri = viewModel::restoreBackupFromUri,
            onBack = { showSettings = false }
        )
        return
    }

    val navItems = listOf(
        AppTab.HOME to s.navHome,
        AppTab.ABOUT_ME to s.navAboutMe,
        AppTab.FAMILY_EDU to s.navFamilyEdu,
        AppTab.MEMORIES to s.navMemories,
        AppTab.DREAMS to s.navDreams
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = s.appTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = viewModel::toggleLanguage,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("topbar_language_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = s.languageSection
                            )
                        }
                        if (uiState.settings.lockEnabled) {
                            IconButton(
                                onClick = viewModel::lockNow,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("topbar_lock_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = s.lockApp
                                )
                            }
                        }
                        IconButton(
                            onClick = { showSettings = true },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("topbar_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = s.settingsTitle
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        modifier = Modifier.navigationBarsPadding(),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        navItems.forEach { (tab, label) ->
                            val selected = currentTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.testTag("nav_${tab.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                        navItems.forEach { (tab, label) ->
                            val selected = currentTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag("rail_${tab.route}")
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        AppTab.HOME -> HomeScreen(
                            uiState = uiState,
                            onEditPersonalProfile = { showEditPersonalDialog = true },
                            onNavigateAboutMe = { currentTab = AppTab.ABOUT_ME },
                            onNavigateFamilyTab = { subTab ->
                                familySubTab = subTab
                                currentTab = AppTab.FAMILY_EDU
                            },
                            onNavigateMemories = { currentTab = AppTab.MEMORIES },
                            onNavigateDreams = { currentTab = AppTab.DREAMS },
                            onNavigateSettings = { showSettings = true },
                            onToggleLanguage = viewModel::toggleLanguage,
                            onLockApp = viewModel::lockNow
                        )

                        AppTab.ABOUT_ME -> AboutMeScreen(
                            uiState = uiState,
                            onEditPersonalProfile = { showEditPersonalDialog = true },
                            onSaveAboutMe = viewModel::saveAboutMe,
                            onSaveAchievement = viewModel::saveAchievement,
                            onDeleteAchievement = viewModel::deleteAchievement
                        )

                        AppTab.FAMILY_EDU -> FamilyEducationScreen(
                            uiState = uiState,
                            selectedSubTab = familySubTab,
                            onSelectSubTab = { familySubTab = it },
                            onPickFamilyPhoto = viewModel::updateFamilyPhoto,
                            onSaveFamilyInfo = viewModel::saveFamilyInfo,
                            onSaveFamilyMember = viewModel::saveFamilyMember,
                            onDeleteFamilyMember = viewModel::deleteFamilyMember,
                            onSaveEducation = viewModel::saveEducation,
                            onDeleteEducation = viewModel::deleteEducation,
                            onSaveContactLink = viewModel::saveContactLink,
                            onDeleteContactLink = viewModel::deleteContactLink
                        )

                        AppTab.MEMORIES -> MemoriesScreen(
                            uiState = uiState,
                            onCopyMedia = viewModel::savePickedMedia,
                            onGetVideoThumb = viewModel::getVideoThumbnail,
                            onSaveMemory = viewModel::saveMemory,
                            onDeleteMemory = viewModel::deleteMemory
                        )

                        AppTab.DREAMS -> DreamsGoalsScreen(
                            uiState = uiState,
                            onSaveDreamGoal = viewModel::saveDreamGoal,
                            onCycleStatus = viewModel::cycleGoalStatus,
                            onDeleteDreamGoal = viewModel::deleteDreamGoal
                        )
                    }
                }
            }
        }
    }

    if (showEditPersonalDialog) {
        EditPersonalProfileDialog(
            initial = uiState.personalProfile,
            onPickPhoto = viewModel::updateProfilePhoto,
            onRemovePhoto = viewModel::removeProfilePhoto,
            onSave = viewModel::savePersonalProfile,
            onClearAll = viewModel::clearPersonalProfile,
            onDismiss = { showEditPersonalDialog = false }
        )
    }
}
