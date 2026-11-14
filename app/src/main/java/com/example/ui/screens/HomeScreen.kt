package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.localization.LocalAppStrings
import com.example.ui.components.ProfileAvatar
import com.example.ui.components.SectionHeader
import com.example.ui.theme.EmeraldSage
import com.example.ui.theme.LuminousGold
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.RoyalIndigo
import com.example.ui.theme.TerracottaRose
import com.example.ui.viewmodel.MyLifeUiState

@Composable
fun HomeScreen(
    uiState: MyLifeUiState,
    onEditPersonalProfile: () -> Unit,
    onNavigateAboutMe: () -> Unit,
    onNavigateFamilyTab: (Int) -> Unit,
    onNavigateMemories: () -> Unit,
    onNavigateDreams: () -> Unit,
    onNavigateSettings: () -> Unit,
    onToggleLanguage: () -> Unit,
    onLockApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAppStrings.current
    val profile = uiState.personalProfile

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 600.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Hero Identity Card (Photo & Name prominently displayed on Home!)
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = s.appTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            RoyalIndigo.copy(alpha = 0.55f),
                                            RoyalIndigo.copy(alpha = 0.92f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ProfileAvatar(
                                    photoPath = profile.photoPath,
                                    contentDescription = profile.fullName.ifBlank { s.tapToAddYourName },
                                    size = 92.dp,
                                    borderColor = LuminousGold,
                                    onClick = onEditPersonalProfile
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = s.welcomeBack,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = LuminousGold
                                    )
                                    Text(
                                        text = profile.fullName.ifBlank { s.tapToAddYourName },
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = profile.tagline.ifBlank { s.addYourBioTagline },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.82f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = onEditPersonalProfile,
                                    colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                                        containerColor = LuminousGold,
                                        contentColor = RoyalIndigo
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("home_edit_profile_button")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = s.editProfile,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                FilledTonalButton(
                                    onClick = onNavigateAboutMe,
                                    colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color.White.copy(alpha = 0.16f),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("home_about_me_button")
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = s.navAboutMe,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Life Snapshot & Completeness
            item {
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = s.profileCompleteness,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${uiState.profileCompletenessPercent}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { uiState.profileCompletenessPercent / 100f },
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SnapshotStatBadge(
                                count = uiState.memories.size.toString(),
                                label = s.navMemories,
                                modifier = Modifier.weight(1f)
                            )
                            SnapshotStatBadge(
                                count = "${uiState.completedGoalsCount}/${uiState.dreamsGoals.size}",
                                label = s.navDreams,
                                modifier = Modifier.weight(1f)
                            )
                            SnapshotStatBadge(
                                count = uiState.familyMembers.size.toString(),
                                label = s.tabFamily,
                                modifier = Modifier.weight(1f)
                            )
                            SnapshotStatBadge(
                                count = uiState.educationList.size.toString(),
                                label = s.tabEducation,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 3. Quick Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = onToggleLanguage,
                        label = {
                            Text(if (uiState.settings.language == "hi") "English" else "हिन्दी")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_language_chip")
                    )

                    AssistChip(
                        onClick = {
                            if (uiState.settings.lockEnabled) onLockApp() else onNavigateSettings()
                        },
                        label = { Text(s.lockApp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_lock_chip")
                    )

                    AssistChip(
                        onClick = onNavigateSettings,
                        label = { Text(s.settingsBackup) },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("home_settings_chip")
                    )
                }
            }

            // 4. All 7 Chapters of My Life
            item {
                SectionHeader(title = s.exploreSections)
            }

            val chapters = listOf(
                ChapterItem(
                    title = s.sectionPersonalTitle,
                    subtitle = s.sectionPersonalDesc,
                    icon = Icons.Default.AccountCircle,
                    accent = RoyalIndigo,
                    badge = if (profile.fullName.isNotBlank()) profile.fullName else null,
                    testTag = "chapter_personal",
                    onClick = onEditPersonalProfile
                ),
                ChapterItem(
                    title = s.sectionAboutTitle,
                    subtitle = s.sectionAboutDesc,
                    icon = Icons.Default.Favorite,
                    accent = TerracottaRose,
                    badge = if (uiState.achievements.isNotEmpty()) "${uiState.achievements.size} ${s.itemsCountSuffix}" else null,
                    testTag = "chapter_about",
                    onClick = onNavigateAboutMe
                ),
                ChapterItem(
                    title = s.sectionFamilyTitle,
                    subtitle = s.sectionFamilyDesc,
                    icon = Icons.Default.FamilyRestroom,
                    accent = MarigoldGold,
                    badge = if (uiState.familyMembers.isNotEmpty()) "${uiState.familyMembers.size} ${s.itemsCountSuffix}" else null,
                    testTag = "chapter_family",
                    onClick = { onNavigateFamilyTab(0) }
                ),
                ChapterItem(
                    title = s.sectionEducationTitle,
                    subtitle = s.sectionEducationDesc,
                    icon = Icons.Default.School,
                    accent = Color(0xFF0F766E),
                    badge = if (uiState.educationList.isNotEmpty()) "${uiState.educationList.size} ${s.itemsCountSuffix}" else null,
                    testTag = "chapter_education",
                    onClick = { onNavigateFamilyTab(1) }
                ),
                ChapterItem(
                    title = s.sectionMemoriesTitle,
                    subtitle = s.sectionMemoriesDesc,
                    icon = Icons.Default.PhotoLibrary,
                    accent = Color(0xFF7C3AED),
                    badge = if (uiState.memories.isNotEmpty()) "${uiState.memories.size} ${s.itemsCountSuffix}" else null,
                    testTag = "chapter_memories",
                    onClick = onNavigateMemories
                ),
                ChapterItem(
                    title = s.sectionDreamsTitle,
                    subtitle = s.sectionDreamsDesc,
                    icon = Icons.Default.AutoAwesome,
                    accent = EmeraldSage,
                    badge = if (uiState.dreamsGoals.isNotEmpty()) "${uiState.completedGoalsCount}/${uiState.dreamsGoals.size}" else null,
                    testTag = "chapter_dreams",
                    onClick = onNavigateDreams
                ),
                ChapterItem(
                    title = s.sectionContactsTitle,
                    subtitle = s.sectionContactsDesc,
                    icon = Icons.Default.Contacts,
                    accent = Color(0xFF0369A1),
                    badge = if (uiState.contactLinks.isNotEmpty()) "${uiState.contactLinks.size} ${s.itemsCountSuffix}" else null,
                    testTag = "chapter_contacts",
                    onClick = { onNavigateFamilyTab(2) }
                )
            )

            if (isWide) {
                val rows = chapters.chunked(2)
                items(rows.size) { index ->
                    val pair = rows[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { item ->
                            ChapterCard(item = item, modifier = Modifier.weight(1f))
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                items(chapters.size) { index ->
                    ChapterCard(item = chapters[index], modifier = Modifier.fillMaxWidth())
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

private data class ChapterItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val badge: String?,
    val testTag: String,
    val onClick: () -> Unit
)

@Composable
private fun SnapshotStatBadge(
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ChapterCard(
    item: ChapterItem,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = item.onClick)
            .testTag(item.testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = item.accent.copy(alpha = 0.14f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!item.badge.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = item.accent.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = item.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = item.accent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
