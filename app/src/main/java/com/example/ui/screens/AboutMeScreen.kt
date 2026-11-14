package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.AboutMeEntity
import com.example.data.local.AchievementEntity
import com.example.localization.LocalAppStrings
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ProfileAvatar
import com.example.ui.components.SectionHeader
import com.example.ui.components.TagChipFlow
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.LuminousGold
import com.example.ui.theme.RoyalIndigo
import com.example.ui.viewmodel.MyLifeUiState

@Composable
fun AboutMeScreen(
    uiState: MyLifeUiState,
    onEditPersonalProfile: () -> Unit,
    onSaveAboutMe: (AboutMeEntity) -> Unit,
    onSaveAchievement: (AchievementEntity) -> Unit,
    onDeleteAchievement: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAppStrings.current
    val profile = uiState.personalProfile
    val about = uiState.aboutMe

    var showEditAboutDialog by remember { mutableStateOf(false) }
    var editingAchievement by remember { mutableStateOf<AchievementEntity?>(null) }
    var showAchievementDialog by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Editorial Identity Showcase
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(listOf(RoyalIndigo, DeepPlum))
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ProfileAvatar(
                                    photoPath = profile.photoPath,
                                    contentDescription = profile.fullName.ifBlank { s.tapToAddYourName },
                                    size = 84.dp,
                                    borderColor = LuminousGold,
                                    onClick = onEditPersonalProfile
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = s.aboutMeHeroTitle,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LuminousGold
                                    )
                                    Text(
                                        text = profile.fullName.ifBlank { s.tapToAddYourName },
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (profile.tagline.isNotBlank()) {
                                        Text(
                                            text = profile.tagline,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.82f)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = onEditPersonalProfile,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("about_edit_personal_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = s.editProfile,
                                        tint = LuminousGold
                                    )
                                }
                            }
                        }

                        // Personal Details Rows
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (profile.dateOfBirth.isBlank() &&
                                profile.mobile.isBlank() &&
                                profile.email.isBlank() &&
                                profile.address.isBlank()
                            ) {
                                Text(
                                    text = s.noPersonalInfoYet,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FilledTonalButton(
                                    onClick = onEditPersonalProfile,
                                    modifier = Modifier.testTag("about_add_personal_button")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(s.editProfile)
                                }
                            } else {
                                if (profile.dateOfBirth.isNotBlank()) {
                                    InfoDetailRow(Icons.Default.Cake, s.dateOfBirth, profile.dateOfBirth)
                                }
                                if (profile.mobile.isNotBlank()) {
                                    InfoDetailRow(Icons.Default.Phone, s.mobileNumber, profile.mobile)
                                }
                                if (profile.email.isNotBlank()) {
                                    InfoDetailRow(Icons.Default.Email, s.emailAddress, profile.email)
                                }
                                if (profile.address.isNotBlank()) {
                                    InfoDetailRow(Icons.Default.LocationOn, s.address, profile.address)
                                }
                            }
                        }
                    }
                }
            }

            // About Me Traits Header
            item {
                SectionHeader(
                    title = s.sectionAboutTitle,
                    actionLabel = s.edit,
                    actionTestTag = "edit_about_me_button",
                    onActionClick = { showEditAboutDialog = true }
                )
            }

            // Bio Summary + Core Life Goal
            item {
                val hasAboutDetails = about.bioSummary.isNotBlank() ||
                    about.likes.isNotBlank() ||
                    about.interests.isNotBlank() ||
                    about.hobbies.isNotBlank() ||
                    about.lifeGoal.isNotBlank()

                if (!hasAboutDetails) {
                    EmptyStateCard(
                        icon = Icons.Default.Favorite,
                        title = s.sectionAboutTitle,
                        subtitle = s.sectionAboutDesc,
                        actionLabel = s.editAboutMe,
                        actionTestTag = "empty_edit_about_button",
                        onActionClick = { showEditAboutDialog = true }
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (about.bioSummary.isNotBlank()) {
                            Card(
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = s.aboutMeSummary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = about.bioSummary,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        if (about.lifeGoal.isNotBlank()) {
                            Card(
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = s.myCoreGoal,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Column {
                                        Text(
                                            text = s.myCoreGoal,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = about.lifeGoal,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        if (about.likes.isNotBlank()) {
                            TraitsTagCard(
                                icon = Icons.Default.Favorite,
                                title = s.myLikes,
                                rawTags = about.likes,
                                chipBg = MaterialTheme.colorScheme.tertiaryContainer,
                                chipFg = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        if (about.interests.isNotBlank()) {
                            TraitsTagCard(
                                icon = Icons.Default.Interests,
                                title = s.myInterests,
                                rawTags = about.interests,
                                chipBg = MaterialTheme.colorScheme.primaryContainer,
                                chipFg = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        if (about.hobbies.isNotBlank()) {
                            TraitsTagCard(
                                icon = Icons.Default.Palette,
                                title = s.myHobbies,
                                rawTags = about.hobbies,
                                chipBg = MaterialTheme.colorScheme.secondaryContainer,
                                chipFg = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            // Achievements Section
            item {
                SectionHeader(
                    title = s.myAchievements,
                    actionLabel = "+ ${s.add}",
                    actionTestTag = "add_achievement_button",
                    onActionClick = {
                        editingAchievement = null
                        showAchievementDialog = true
                    }
                )
            }

            if (uiState.achievements.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.EmojiEvents,
                        title = s.myAchievements,
                        subtitle = s.noAchievementsYet,
                        actionLabel = s.addAchievement,
                        actionTestTag = "empty_add_achievement_button",
                        onActionClick = {
                            editingAchievement = null
                            showAchievementDialog = true
                        }
                    )
                }
            } else {
                items(uiState.achievements, key = { it.id }) { item ->
                    AchievementCard(
                        achievement = item,
                        onEdit = {
                            editingAchievement = item
                            showAchievementDialog = true
                        },
                        onDelete = { pendingDeleteId = item.id }
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    if (showEditAboutDialog) {
        EditAboutMeDialog(
            initial = about,
            onSave = onSaveAboutMe,
            onDismiss = { showEditAboutDialog = false }
        )
    }

    if (showAchievementDialog) {
        EditAchievementDialog(
            initial = editingAchievement,
            onSave = onSaveAchievement,
            onDismiss = { showAchievementDialog = false }
        )
    }

    pendingDeleteId?.let { id ->
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteAchievement(id)
                pendingDeleteId = null
            },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
private fun InfoDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun TraitsTagCard(
    icon: ImageVector,
    title: String,
    rawTags: String,
    chipBg: Color,
    chipFg: Color
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            TagChipFlow(rawText = rawTags, containerColor = chipBg, contentColor = chipFg)
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: AchievementEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val s = LocalAppStrings.current
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = achievement.title,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                val meta = listOfNotNull(
                    achievement.year.takeIf { it.isNotBlank() },
                    achievement.category.takeIf { it.isNotBlank() }
                ).joinToString(" • ")
                if (meta.isNotBlank()) {
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                if (achievement.description.isNotBlank()) {
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column {
                IconButton(onClick = onEdit, modifier = Modifier.minimumInteractiveComponentSize()) {
                    Icon(Icons.Default.Edit, contentDescription = s.edit, tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = s.delete, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun EditAboutMeDialog(
    initial: AboutMeEntity,
    onSave: (AboutMeEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    var bio by remember(initial) { mutableStateOf(initial.bioSummary) }
    var likes by remember(initial) { mutableStateOf(initial.likes) }
    var interests by remember(initial) { mutableStateOf(initial.interests) }
    var hobbies by remember(initial) { mutableStateOf(initial.hobbies) }
    var lifeGoal by remember(initial) { mutableStateOf(initial.lifeGoal) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(s.editAboutMe, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = s.close)
                    }
                }

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(s.aboutMeSummary) },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = likes,
                    onValueChange = { likes = it },
                    label = { Text(s.myLikes) },
                    supportingText = { Text(s.commaSeparatedHint) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = interests,
                    onValueChange = { interests = it },
                    label = { Text(s.myInterests) },
                    supportingText = { Text(s.commaSeparatedHint) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hobbies,
                    onValueChange = { hobbies = it },
                    label = { Text(s.myHobbies) },
                    supportingText = { Text(s.commaSeparatedHint) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lifeGoal,
                    onValueChange = { lifeGoal = it },
                    label = { Text(s.myCoreGoal) },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                initial.copy(
                                    bioSummary = bio.trim(),
                                    likes = likes.trim(),
                                    interests = interests.trim(),
                                    hobbies = hobbies.trim(),
                                    lifeGoal = lifeGoal.trim()
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_about_me_button")
                    ) {
                        Text(s.save)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditAchievementDialog(
    initial: AchievementEntity?,
    onSave: (AchievementEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    var title by remember(initial) { mutableStateOf(initial?.title ?: "") }
    var year by remember(initial) { mutableStateOf(initial?.year ?: "") }
    var category by remember(initial) { mutableStateOf(initial?.category ?: "") }
    var description by remember(initial) { mutableStateOf(initial?.description ?: "") }
    var titleError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (initial == null) s.addAchievement else s.editAchievement,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text(s.achievementTitle) },
                    isError = titleError,
                    supportingText = if (titleError) { { Text(s.requiredField) } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text(s.achievementYear) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(s.achievementCategory) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(s.achievementDesc) },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                                return@Button
                            }
                            onSave(
                                (initial ?: AchievementEntity(title = "")).copy(
                                    title = title.trim(),
                                    year = year.trim(),
                                    category = category.trim(),
                                    description = description.trim()
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_achievement_button")
                    ) {
                        Text(s.save)
                    }
                }
            }
        }
    }
}
