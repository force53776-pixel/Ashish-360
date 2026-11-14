package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.ContactLinkEntity
import com.example.data.local.ContactPlatform
import com.example.data.local.EducationEntity
import com.example.data.local.FamilyInfoEntity
import com.example.data.local.FamilyMemberEntity
import com.example.localization.LocalAppStrings
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SectionHeader
import com.example.ui.viewmodel.MyLifeUiState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyEducationScreen(
    uiState: MyLifeUiState,
    selectedSubTab: Int,
    onSelectSubTab: (Int) -> Unit,
    onPickFamilyPhoto: (Uri) -> Unit,
    onSaveFamilyInfo: (FamilyInfoEntity) -> Unit,
    onSaveFamilyMember: (FamilyMemberEntity) -> Unit,
    onDeleteFamilyMember: (FamilyMemberEntity) -> Unit,
    onSaveEducation: (EducationEntity) -> Unit,
    onDeleteEducation: (Long) -> Unit,
    onSaveContactLink: (ContactLinkEntity) -> Unit,
    onDeleteContactLink: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAppStrings.current
    val context = LocalContext.current

    var showEditFamilyInfoDialog by remember { mutableStateOf(false) }
    var editingMember by remember { mutableStateOf<FamilyMemberEntity?>(null) }
    var showMemberDialog by remember { mutableStateOf(false) }
    var pendingDeleteMember by remember { mutableStateOf<FamilyMemberEntity?>(null) }

    var editingEducation by remember { mutableStateOf<EducationEntity?>(null) }
    var showEducationDialog by remember { mutableStateOf(false) }
    var pendingDeleteEduId by remember { mutableStateOf<Long?>(null) }

    var editingContact by remember { mutableStateOf<ContactLinkEntity?>(null) }
    var showContactDialog by remember { mutableStateOf(false) }
    var pendingDeleteContactId by remember { mutableStateOf<Long?>(null) }

    val familyPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let(onPickFamilyPhoto) }
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .align(Alignment.TopCenter)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { onSelectSubTab(0) },
                    text = { Text(s.tabFamily, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    modifier = Modifier.testTag("subtab_family")
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { onSelectSubTab(1) },
                    text = { Text(s.tabEducation, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    modifier = Modifier.testTag("subtab_education")
                )
                Tab(
                    selected = selectedSubTab == 2,
                    onClick = { onSelectSubTab(2) },
                    text = { Text(s.tabContacts, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    modifier = Modifier.testTag("subtab_contacts")
                )
            }

            when (selectedSubTab) {
                0 -> {
                    // FAMILY TAB
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            SectionHeader(
                                title = s.parentsInfo,
                                actionLabel = s.edit,
                                actionTestTag = "edit_family_info_button",
                                onActionClick = { showEditFamilyInfoDialog = true }
                            )
                        }

                        item {
                            val family = uiState.familyInfo
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
                                    // Family Photo
                                    val hasPhoto = family.familyPhotoPath.isNotBlank() && File(family.familyPhotoPath).exists()
                                    if (hasPhoto) {
                                        AsyncImage(
                                            model = File(family.familyPhotoPath),
                                            contentDescription = s.familyPhoto,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(190.dp)
                                                .clip(MaterialTheme.shapes.medium)
                                                .clickable {
                                                    familyPhotoPicker.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                }
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        FilledTonalButton(
                                            onClick = {
                                                familyPhotoPicker.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("pick_family_photo_button")
                                        ) {
                                            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(if (hasPhoto) s.changePhoto else s.familyPhoto)
                                        }
                                        OutlinedButton(
                                            onClick = { showEditFamilyInfoDialog = true },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(s.edit)
                                        }
                                    }

                                    if (family.fatherName.isNotBlank()) {
                                        ParentRow(label = s.fatherName, name = family.fatherName)
                                    }
                                    if (family.motherName.isNotBlank()) {
                                        ParentRow(label = s.motherName, name = family.motherName)
                                    }
                                    if (family.familyQuote.isNotBlank()) {
                                        Surface(
                                            shape = MaterialTheme.shapes.small,
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "\"${family.familyQuote}\"",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = s.siblingsAndMembers,
                                actionLabel = "+ ${s.add}",
                                actionTestTag = "add_family_member_button",
                                onActionClick = {
                                    editingMember = null
                                    showMemberDialog = true
                                }
                            )
                        }

                        if (uiState.familyMembers.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    icon = Icons.Default.FamilyRestroom,
                                    title = s.siblingsAndMembers,
                                    subtitle = s.noFamilyMembersYet,
                                    actionLabel = s.addFamilyMember,
                                    actionTestTag = "empty_add_family_member_button",
                                    onActionClick = {
                                        editingMember = null
                                        showMemberDialog = true
                                    }
                                )
                            }
                        } else {
                            items(uiState.familyMembers, key = { it.id }) { member ->
                                FamilyMemberCard(
                                    member = member,
                                    onEdit = {
                                        editingMember = member
                                        showMemberDialog = true
                                    },
                                    onDelete = { pendingDeleteMember = member }
                                )
                            }
                        }
                        item { Spacer(Modifier.height(72.dp)) }
                    }
                }

                1 -> {
                    // EDUCATION TAB
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            SectionHeader(
                                title = s.educationJourney,
                                actionLabel = "+ ${s.add}",
                                actionTestTag = "add_education_button",
                                onActionClick = {
                                    editingEducation = null
                                    showEducationDialog = true
                                }
                            )
                        }

                        if (uiState.educationList.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    icon = Icons.Default.School,
                                    title = s.sectionEducationTitle,
                                    subtitle = s.noEducationYet,
                                    actionLabel = s.addEducation,
                                    actionTestTag = "empty_add_education_button",
                                    onActionClick = {
                                        editingEducation = null
                                        showEducationDialog = true
                                    }
                                )
                            }
                        } else {
                            items(uiState.educationList, key = { it.id }) { edu ->
                                EducationCard(
                                    education = edu,
                                    onEdit = {
                                        editingEducation = edu
                                        showEducationDialog = true
                                    },
                                    onDelete = { pendingDeleteEduId = edu.id }
                                )
                            }
                        }
                        item { Spacer(Modifier.height(72.dp)) }
                    }
                }

                else -> {
                    // CONTACTS TAB
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            SectionHeader(
                                title = s.contactsAndSocial,
                                actionLabel = "+ ${s.add}",
                                actionTestTag = "add_contact_button",
                                onActionClick = {
                                    editingContact = null
                                    showContactDialog = true
                                }
                            )
                        }

                        // Quick mirror of personal phone & email if set
                        val profile = uiState.personalProfile
                        if (profile.mobile.isNotBlank() || profile.email.isNotBlank()) {
                            item {
                                Card(
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (profile.mobile.isNotBlank()) {
                                            ContactActionRow(
                                                icon = Icons.Default.Phone,
                                                label = s.mobileNumber,
                                                value = profile.mobile,
                                                onOpen = { openContactAction(context, ContactPlatform.PHONE, profile.mobile) },
                                                onCopy = { copyText(context, profile.mobile, s.copiedToClipboard) }
                                            )
                                        }
                                        if (profile.email.isNotBlank()) {
                                            ContactActionRow(
                                                icon = Icons.Default.Email,
                                                label = s.emailAddress,
                                                value = profile.email,
                                                onOpen = { openContactAction(context, ContactPlatform.EMAIL, profile.email) },
                                                onCopy = { copyText(context, profile.email, s.copiedToClipboard) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (uiState.contactLinks.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    icon = Icons.Default.Contacts,
                                    title = s.sectionContactsTitle,
                                    subtitle = s.noContactsYet,
                                    actionLabel = s.addContact,
                                    actionTestTag = "empty_add_contact_button",
                                    onActionClick = {
                                        editingContact = null
                                        showContactDialog = true
                                    }
                                )
                            }
                        } else {
                            items(uiState.contactLinks, key = { it.id }) { link ->
                                ContactLinkCard(
                                    link = link,
                                    onOpen = { openContactAction(context, link.platform, link.value) },
                                    onCopy = { copyText(context, link.value, s.copiedToClipboard) },
                                    onEdit = {
                                        editingContact = link
                                        showContactDialog = true
                                    },
                                    onDelete = { pendingDeleteContactId = link.id }
                                )
                            }
                        }
                        item { Spacer(Modifier.height(72.dp)) }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                when (selectedSubTab) {
                    0 -> {
                        editingMember = null
                        showMemberDialog = true
                    }
                    1 -> {
                        editingEducation = null
                        showEducationDialog = true
                    }
                    else -> {
                        editingContact = null
                        showContactDialog = true
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("family_edu_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = s.add)
        }
    }

    if (showEditFamilyInfoDialog) {
        EditFamilyInfoDialog(
            initial = uiState.familyInfo,
            onSave = onSaveFamilyInfo,
            onDismiss = { showEditFamilyInfoDialog = false }
        )
    }

    if (showMemberDialog) {
        EditFamilyMemberDialog(
            initial = editingMember,
            onSave = onSaveFamilyMember,
            onDismiss = { showMemberDialog = false }
        )
    }

    if (showEducationDialog) {
        EditEducationDialog(
            initial = editingEducation,
            onSave = onSaveEducation,
            onDismiss = { showEducationDialog = false }
        )
    }

    if (showContactDialog) {
        EditContactLinkDialog(
            initial = editingContact,
            onSave = onSaveContactLink,
            onDismiss = { showContactDialog = false }
        )
    }

    pendingDeleteMember?.let { m ->
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteFamilyMember(m)
                pendingDeleteMember = null
            },
            onDismiss = { pendingDeleteMember = null }
        )
    }

    pendingDeleteEduId?.let { id ->
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteEducation(id)
                pendingDeleteEduId = null
            },
            onDismiss = { pendingDeleteEduId = null }
        )
    }

    pendingDeleteContactId?.let { id ->
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteContactLink(id)
                pendingDeleteContactId = null
            },
            onDismiss = { pendingDeleteContactId = null }
        )
    }
}

@Composable
private fun ParentRow(label: String, name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FamilyMemberCard(
    member: FamilyMemberEntity,
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = member.name, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(member.relation, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                if (member.phone.isNotBlank()) {
                    Text(member.phone, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (member.notes.isNotBlank()) {
                    Text(member.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onEdit, modifier = Modifier.minimumInteractiveComponentSize()) {
                Icon(Icons.Default.Edit, contentDescription = s.edit, tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                Icon(Icons.Default.DeleteOutline, contentDescription = s.delete, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun EducationCard(
    education: EducationEntity,
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
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.School, contentDescription = education.institutionName, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(education.institutionName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                val degreeLine = listOfNotNull(
                    education.qualification.takeIf { it.isNotBlank() },
                    education.fieldOfStudy.takeIf { it.isNotBlank() },
                    education.level.takeIf { it.isNotBlank() }
                ).joinToString(" • ")
                if (degreeLine.isNotBlank()) {
                    Text(degreeLine, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
                val years = listOfNotNull(
                    education.startYear.takeIf { it.isNotBlank() },
                    if (education.isCurrent) s.currentlyStudying else education.endYear.takeIf { it.isNotBlank() }
                ).joinToString(" – ")
                if (years.isNotBlank()) {
                    Text(years, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (education.gradeOrScore.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = education.gradeOrScore,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
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
private fun ContactActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onOpen: () -> Unit,
    onCopy: () -> Unit
) {
    val s = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        IconButton(onClick = onCopy, modifier = Modifier.minimumInteractiveComponentSize()) {
            Icon(Icons.Default.ContentCopy, contentDescription = s.copiedToClipboard)
        }
    }
}

@Composable
private fun ContactLinkCard(
    link: ContactLinkEntity,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val s = LocalAppStrings.current
    val icon = when (link.platform) {
        ContactPlatform.PHONE, ContactPlatform.WHATSAPP -> Icons.Default.Phone
        ContactPlatform.EMAIL -> Icons.Default.Email
        ContactPlatform.WEBSITE -> Icons.Default.Language
        ContactPlatform.INSTAGRAM, ContactPlatform.YOUTUBE, ContactPlatform.FACEBOOK,
        ContactPlatform.TWITTER, ContactPlatform.LINKEDIN -> Icons.Default.Public
        else -> Icons.Default.Share
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onOpen)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = link.platform, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(link.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = link.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onCopy, modifier = Modifier.minimumInteractiveComponentSize()) {
                Icon(Icons.Default.ContentCopy, contentDescription = s.copiedToClipboard)
            }
            IconButton(onClick = onEdit, modifier = Modifier.minimumInteractiveComponentSize()) {
                Icon(Icons.Default.Edit, contentDescription = s.edit, tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                Icon(Icons.Default.DeleteOutline, contentDescription = s.delete, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun openContactAction(context: Context, platform: String, value: String) {
    val trimmed = value.trim()
    val intent = when (platform) {
        ContactPlatform.PHONE -> Intent(Intent.ACTION_DIAL, Uri.parse("tel:$trimmed"))
        ContactPlatform.EMAIL -> Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$trimmed"))
        else -> {
            val url = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                trimmed
            } else {
                "https://$trimmed"
            }
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

private fun copyText(context: Context, text: String, toastMsg: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("MyLife", text))
    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
}

@Composable
private fun EditFamilyInfoDialog(
    initial: FamilyInfoEntity,
    onSave: (FamilyInfoEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    var father by remember(initial) { mutableStateOf(initial.fatherName) }
    var mother by remember(initial) { mutableStateOf(initial.motherName) }
    var quote by remember(initial) { mutableStateOf(initial.familyQuote) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(s.editFamilyInfo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = father,
                    onValueChange = { father = it },
                    label = { Text(s.fatherName) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mother,
                    onValueChange = { mother = it },
                    label = { Text(s.motherName) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quote,
                    onValueChange = { quote = it },
                    label = { Text(s.familyQuote) },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                initial.copy(
                                    fatherName = father.trim(),
                                    motherName = mother.trim(),
                                    familyQuote = quote.trim()
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_family_info_button")
                    ) { Text(s.save) }
                }
            }
        }
    }
}

@Composable
private fun EditFamilyMemberDialog(
    initial: FamilyMemberEntity?,
    onSave: (FamilyMemberEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    var name by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var relation by remember(initial) { mutableStateOf(initial?.relation ?: "") }
    var phone by remember(initial) { mutableStateOf(initial?.phone ?: "") }
    var notes by remember(initial) { mutableStateOf(initial?.notes ?: "") }
    var nameError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (initial == null) s.addFamilyMember else s.editFamilyMember,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text(s.memberName) },
                    isError = nameError,
                    supportingText = if (nameError) { { Text(s.requiredField) } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text(s.relation) },
                    placeholder = { Text(s.relationExamples) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(s.mobileNumber) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(s.notes) },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) { nameError = true; return@Button }
                            onSave(
                                (initial ?: FamilyMemberEntity(name = "", relation = "")).copy(
                                    name = name.trim(),
                                    relation = relation.trim(),
                                    phone = phone.trim(),
                                    notes = notes.trim()
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_family_member_button")
                    ) { Text(s.save) }
                }
            }
        }
    }
}

@Composable
private fun EditEducationDialog(
    initial: EducationEntity?,
    onSave: (EducationEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    var institution by remember(initial) { mutableStateOf(initial?.institutionName ?: "") }
    var level by remember(initial) { mutableStateOf(initial?.level ?: "") }
    var field by remember(initial) { mutableStateOf(initial?.fieldOfStudy ?: "") }
    var qualification by remember(initial) { mutableStateOf(initial?.qualification ?: "") }
    var startYear by remember(initial) { mutableStateOf(initial?.startYear ?: "") }
    var endYear by remember(initial) { mutableStateOf(initial?.endYear ?: "") }
    var grade by remember(initial) { mutableStateOf(initial?.gradeOrScore ?: "") }
    var isCurrent by remember(initial) { mutableStateOf(initial?.isCurrent ?: false) }
    var instError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                    Text(
                        text = if (initial == null) s.addEducation else s.editEducation,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = s.close) }
                }

                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it; instError = false },
                    label = { Text(s.institutionName) },
                    isError = instError,
                    supportingText = if (instError) { { Text(s.requiredField) } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = level,
                    onValueChange = { level = it },
                    label = { Text(s.educationLevel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = field,
                    onValueChange = { field = it },
                    label = { Text(s.fieldOfStudy) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = qualification,
                    onValueChange = { qualification = it },
                    label = { Text(s.qualification) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = startYear,
                        onValueChange = { startYear = it },
                        label = { Text(s.startYear) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endYear,
                        onValueChange = { endYear = it },
                        label = { Text(s.endYear) },
                        enabled = !isCurrent,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = grade,
                    onValueChange = { grade = it },
                    label = { Text(s.gradeOrScore) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isCurrent = !isCurrent }
                ) {
                    Checkbox(checked = isCurrent, onCheckedChange = { isCurrent = it })
                    Text(s.currentlyStudying, style = MaterialTheme.typography.bodyMedium)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (institution.isBlank()) { instError = true; return@Button }
                            onSave(
                                (initial ?: EducationEntity(institutionName = "")).copy(
                                    institutionName = institution.trim(),
                                    level = level.trim(),
                                    fieldOfStudy = field.trim(),
                                    qualification = qualification.trim(),
                                    startYear = startYear.trim(),
                                    endYear = if (isCurrent) "" else endYear.trim(),
                                    gradeOrScore = grade.trim(),
                                    isCurrent = isCurrent
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_education_button")
                    ) { Text(s.save) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditContactLinkDialog(
    initial: ContactLinkEntity?,
    onSave: (ContactLinkEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val platforms = listOf(
        ContactPlatform.PHONE,
        ContactPlatform.EMAIL,
        ContactPlatform.WHATSAPP,
        ContactPlatform.INSTAGRAM,
        ContactPlatform.YOUTUBE,
        ContactPlatform.FACEBOOK,
        ContactPlatform.TWITTER,
        ContactPlatform.LINKEDIN,
        ContactPlatform.WEBSITE,
        ContactPlatform.OTHER
    )
    var label by remember(initial) { mutableStateOf(initial?.label ?: "") }
    var platform by remember(initial) { mutableStateOf(initial?.platform ?: ContactPlatform.INSTAGRAM) }
    var value by remember(initial) { mutableStateOf(initial?.value ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var valueError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (initial == null) s.addContact else s.editContact,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = platform,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(s.platform) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        platforms.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    platform = p
                                    if (label.isBlank()) label = p.lowercase().replaceFirstChar { it.uppercase() }
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(s.contactLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it; valueError = false },
                    label = { Text(s.contactValueHint) },
                    isError = valueError,
                    supportingText = if (valueError) { { Text(s.requiredField) } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (value.isBlank()) { valueError = true; return@Button }
                            onSave(
                                (initial ?: ContactLinkEntity(label = "", value = "")).copy(
                                    label = label.ifBlank { platform }.trim(),
                                    platform = platform,
                                    value = value.trim()
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_contact_button")
                    ) { Text(s.save) }
                }
            }
        }
    }
}
