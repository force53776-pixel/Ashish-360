package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.DreamCategory
import com.example.data.local.DreamGoalEntity
import com.example.data.local.GoalStatus
import com.example.localization.LocalAppStrings
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.DatePickerTextField
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.EmeraldSage
import com.example.ui.theme.MarigoldGold
import com.example.ui.viewmodel.MyLifeUiState
import kotlin.math.roundToInt

@Composable
fun DreamsGoalsScreen(
    uiState: MyLifeUiState,
    onSaveDreamGoal: (DreamGoalEntity) -> Unit,
    onCycleStatus: (DreamGoalEntity) -> Unit,
    onDeleteDreamGoal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAppStrings.current
    var filterKey by remember { mutableStateOf("ALL") }
    var editingItem by remember { mutableStateOf<DreamGoalEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    val filtered = remember(uiState.dreamsGoals, filterKey) {
        when (filterKey) {
            DreamCategory.DREAM -> uiState.dreamsGoals.filter { it.category == DreamCategory.DREAM }
            DreamCategory.GOAL -> uiState.dreamsGoals.filter { it.category == DreamCategory.GOAL }
            GoalStatus.COMPLETED -> uiState.dreamsGoals.filter { it.status == GoalStatus.COMPLETED }
            else -> uiState.dreamsGoals
        }
    }

    val totalCount = uiState.dreamsGoals.size
    val completedCount = uiState.completedGoalsCount
    val completionRate = if (totalCount == 0) 0 else (completedCount * 100) / totalCount

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Overall Progress Card
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = s.dreamsGoalsTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${s.overallProgress}: $completedCount / $totalCount",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            Text(
                                text = "$completionRate%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { completionRate / 100f },
                            color = EmeraldSage,
                            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        )
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val options = listOf(
                        "ALL" to s.filterAll,
                        DreamCategory.DREAM to s.filterDreams,
                        DreamCategory.GOAL to s.filterGoals,
                        GoalStatus.COMPLETED to s.filterCompleted
                    )
                    items(options) { (key, label) ->
                        FilterChip(
                            selected = filterKey == key,
                            onClick = { filterKey = key },
                            label = { Text(label) },
                            modifier = Modifier.testTag("dreams_filter_$key")
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.AutoAwesome,
                        title = s.dreamsGoalsTitle,
                        subtitle = s.noDreamsYet,
                        actionLabel = s.addDreamGoal,
                        actionTestTag = "empty_add_dream_button",
                        onActionClick = {
                            editingItem = null
                            showEditDialog = true
                        }
                    )
                }
            } else {
                items(filtered, key = { it.id }) { item ->
                    DreamGoalCard(
                        item = item,
                        onCycleStatus = { onCycleStatus(item) },
                        onEdit = {
                            editingItem = item
                            showEditDialog = true
                        },
                        onDelete = { pendingDeleteId = item.id }
                    )
                }
            }

            item { Spacer(Modifier.height(72.dp)) }
        }

        FloatingActionButton(
            onClick = {
                editingItem = null
                showEditDialog = true
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_dream_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = s.addDreamGoal)
        }
    }

    if (showEditDialog) {
        EditDreamGoalDialog(
            initial = editingItem,
            onSave = onSaveDreamGoal,
            onDismiss = { showEditDialog = false }
        )
    }

    pendingDeleteId?.let { id ->
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteDreamGoal(id)
                pendingDeleteId = null
            },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
private fun DreamGoalCard(
    item: DreamGoalEntity,
    onCycleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val s = LocalAppStrings.current
    val (statusLabel, statusColor, statusIcon) = when (item.status) {
        GoalStatus.COMPLETED -> Triple(s.statusCompleted, EmeraldSage, Icons.Default.CheckCircle)
        GoalStatus.IN_PROGRESS -> Triple(s.statusInProgress, MarigoldGold, Icons.Default.Timelapse)
        else -> Triple(s.statusNotStarted, MaterialTheme.colorScheme.onSurfaceVariant, Icons.Default.RadioButtonUnchecked)
    }
    val typeLabel = if (item.category == DreamCategory.DREAM) s.typeDream else s.typeGoal

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.dp,
            color = if (item.status == GoalStatus.COMPLETED) EmeraldSage.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = typeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (item.targetDate.isNotBlank()) {
                        Text(
                            text = "${s.targetDate}: ${item.targetDate}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.Edit, contentDescription = s.edit, tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = s.delete, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LinearProgressIndicator(
                    progress = { item.progressPercent / 100f },
                    color = statusColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                )
                Text(
                    text = "${item.progressPercent}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = statusColor.copy(alpha = 0.14f),
                modifier = Modifier
                    .clickable(onClick = onCycleStatus)
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(statusIcon, contentDescription = statusLabel, tint = statusColor, modifier = Modifier.size(18.dp))
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
private fun EditDreamGoalDialog(
    initial: DreamGoalEntity?,
    onSave: (DreamGoalEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    var title by remember(initial) { mutableStateOf(initial?.title ?: "") }
    var description by remember(initial) { mutableStateOf(initial?.description ?: "") }
    var category by remember(initial) { mutableStateOf(initial?.category ?: DreamCategory.GOAL) }
    var targetDate by remember(initial) { mutableStateOf(initial?.targetDate ?: "") }
    var status by remember(initial) { mutableStateOf(initial?.status ?: GoalStatus.IN_PROGRESS) }
    var progress by remember(initial) { mutableFloatStateOf((initial?.progressPercent ?: 25).toFloat()) }
    var titleError by remember { mutableStateOf(false) }

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
                    Text(
                        text = if (initial == null) s.addDreamGoal else s.editDreamGoal,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = s.close) }
                }

                Text(s.dreamOrGoal, style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = category == DreamCategory.DREAM,
                        onClick = { category = DreamCategory.DREAM },
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) { Text(s.typeDream) }
                    SegmentedButton(
                        selected = category == DreamCategory.GOAL,
                        onClick = { category = DreamCategory.GOAL },
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) { Text(s.typeGoal) }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text(s.memoryTitle) },
                    isError = titleError,
                    supportingText = if (titleError) { { Text(s.requiredField) } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                DatePickerTextField(
                    label = s.targetDate,
                    value = targetDate,
                    onValueChange = { targetDate = it }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(s.achievementDesc) },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(s.completionStatus, style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val statuses = listOf(
                        GoalStatus.NOT_STARTED to s.statusNotStarted,
                        GoalStatus.IN_PROGRESS to s.statusInProgress,
                        GoalStatus.COMPLETED to s.statusCompleted
                    )
                    statuses.forEachIndexed { idx, (key, label) ->
                        SegmentedButton(
                            selected = status == key,
                            onClick = {
                                status = key
                                if (key == GoalStatus.COMPLETED) progress = 100f
                                if (key == GoalStatus.NOT_STARTED) progress = 0f
                            },
                            shape = SegmentedButtonDefaults.itemShape(idx, statuses.size)
                        ) {
                            Text(label, maxLines = 1)
                        }
                    }
                }

                Text("${s.progressLabel}: ${progress.roundToInt()}%", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = progress,
                    onValueChange = {
                        progress = it
                        status = when {
                            it.roundToInt() >= 100 -> GoalStatus.COMPLETED
                            it.roundToInt() == 0 -> GoalStatus.NOT_STARTED
                            else -> GoalStatus.IN_PROGRESS
                        }
                    },
                    valueRange = 0f..100f
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) { titleError = true; return@Button }
                            onSave(
                                (initial ?: DreamGoalEntity(title = "")).copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    category = category,
                                    targetDate = targetDate.trim(),
                                    status = status,
                                    progressPercent = progress.roundToInt()
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_dream_button")
                    ) { Text(s.save) }
                }
            }
        }
    }
}
