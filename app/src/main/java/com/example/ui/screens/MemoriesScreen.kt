package com.example.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.MemoryEntity
import com.example.data.local.MemoryMediaType
import com.example.localization.LocalAppStrings
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.DatePickerTextField
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.RoyalIndigo
import com.example.ui.viewmodel.MyLifeUiState
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MemoriesScreen(
    uiState: MyLifeUiState,
    onCopyMedia: suspend (Uri, String) -> String,
    onGetVideoThumb: suspend (String) -> String?,
    onSaveMemory: (MemoryEntity) -> Unit,
    onDeleteMemory: (MemoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAppStrings.current
    var filterType by remember { mutableStateOf("ALL") }
    var editingMemory by remember { mutableStateOf<MemoryEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var viewingMemory by remember { mutableStateOf<MemoryEntity?>(null) }
    var pendingDelete by remember { mutableStateOf<MemoryEntity?>(null) }

    val filtered = remember(uiState.memories, filterType) {
        when (filterType) {
            MemoryMediaType.PHOTO -> uiState.memories.filter { it.mediaType == MemoryMediaType.PHOTO }
            MemoryMediaType.VIDEO -> uiState.memories.filter { it.mediaType == MemoryMediaType.VIDEO }
            MemoryMediaType.NOTE -> uiState.memories.filter { it.mediaType == MemoryMediaType.NOTE }
            else -> uiState.memories
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Memories Hero Banner
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_memories_banner),
                            contentDescription = s.memoriesTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, RoyalIndigo.copy(alpha = 0.88f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(18.dp)
                        ) {
                            Text(
                                text = s.memoriesTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = s.sectionMemoriesDesc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Filter Pills
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val options = listOf(
                        "ALL" to s.filterAll,
                        MemoryMediaType.PHOTO to s.filterPhotos,
                        MemoryMediaType.VIDEO to s.filterVideos,
                        MemoryMediaType.NOTE to s.filterNotes
                    )
                    items(options) { (key, label) ->
                        FilterChip(
                            selected = filterType == key,
                            onClick = { filterType = key },
                            label = { Text(label) },
                            modifier = Modifier.testTag("memory_filter_$key")
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.PhotoLibrary,
                        title = s.memoriesTitle,
                        subtitle = s.noMemoriesYet,
                        actionLabel = s.addMemory,
                        actionTestTag = "empty_add_memory_button",
                        onActionClick = {
                            editingMemory = null
                            showEditDialog = true
                        }
                    )
                }
            } else {
                items(filtered, key = { it.id }) { memory ->
                    MemoryCard(
                        memory = memory,
                        onGetVideoThumb = onGetVideoThumb,
                        onView = { viewingMemory = memory },
                        onEdit = {
                            editingMemory = memory
                            showEditDialog = true
                        },
                        onDelete = { pendingDelete = memory }
                    )
                }
            }

            item { Spacer(Modifier.height(72.dp)) }
        }

        FloatingActionButton(
            onClick = {
                editingMemory = null
                showEditDialog = true
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_memory_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = s.addMemory)
        }
    }

    if (showEditDialog) {
        EditMemoryDialog(
            initial = editingMemory,
            onCopyMedia = onCopyMedia,
            onSave = onSaveMemory,
            onDismiss = { showEditDialog = false }
        )
    }

    viewingMemory?.let { mem ->
        MemoryMediaViewerDialog(
            memory = mem,
            onDismiss = { viewingMemory = null }
        )
    }

    pendingDelete?.let { mem ->
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteMemory(mem)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun MemoryCard(
    memory: MemoryEntity,
    onGetVideoThumb: suspend (String) -> String?,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val s = LocalAppStrings.current
    var videoThumbPath by remember(memory.mediaPath) { mutableStateOf<String?>(null) }

    LaunchedEffect(memory.mediaPath, memory.mediaType) {
        if (memory.mediaType == MemoryMediaType.VIDEO && memory.mediaPath.isNotBlank()) {
            videoThumbPath = onGetVideoThumb(memory.mediaPath)
        }
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onView)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (memory.mediaPath.isNotBlank() && File(memory.mediaPath).exists()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val displayModel = if (memory.mediaType == MemoryMediaType.VIDEO) {
                        videoThumbPath?.let { File(it) }
                    } else {
                        File(memory.mediaPath)
                    }
                    if (displayModel != null) {
                        AsyncImage(
                            model = displayModel,
                            contentDescription = memory.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    if (memory.mediaType == MemoryMediaType.VIDEO) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = s.playVideo,
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = memory.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onEdit, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.Edit, contentDescription = s.edit, tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = s.delete, tint = MaterialTheme.colorScheme.error)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (memory.memoryDate.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = memory.memoryDate,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    if (memory.location.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = memory.location,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (memory.description.isNotBlank()) {
                    Text(
                        text = memory.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun EditMemoryDialog(
    initial: MemoryEntity?,
    onCopyMedia: suspend (Uri, String) -> String,
    onSave: (MemoryEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var title by remember(initial) { mutableStateOf(initial?.title ?: "") }
    var date by remember(initial) { mutableStateOf(initial?.memoryDate ?: "") }
    var location by remember(initial) { mutableStateOf(initial?.location ?: "") }
    var description by remember(initial) { mutableStateOf(initial?.description ?: "") }
    var mediaPath by remember(initial) { mutableStateOf(initial?.mediaPath ?: "") }
    var mediaType by remember(initial) { mutableStateOf(initial?.mediaType ?: MemoryMediaType.NOTE) }
    var titleError by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    mediaPath = onCopyMedia(it, "jpg")
                    mediaType = MemoryMediaType.PHOTO
                }
            }
        }
    )

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    mediaPath = onCopyMedia(it, "mp4")
                    mediaType = MemoryMediaType.VIDEO
                }
            }
        }
    )

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
                        text = if (initial == null) s.addMemory else s.editMemory,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = s.close) }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pick_memory_photo_button")
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(s.pickPhoto)
                    }
                    FilledTonalButton(
                        onClick = {
                            videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pick_memory_video_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(s.pickVideo)
                    }
                }

                if (mediaPath.isNotBlank() && File(mediaPath).exists()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (mediaType == MemoryMediaType.VIDEO) "✓ ${s.pickVideo}" else "✓ ${s.pickPhoto}",
                                style = MaterialTheme.typography.labelLarge
                            )
                            TextButton(onClick = {
                                mediaPath = ""
                                mediaType = MemoryMediaType.NOTE
                            }) {
                                Text(s.removePhoto)
                            }
                        }
                    }
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
                    label = s.memoryDate,
                    value = date,
                    onValueChange = { date = it }
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(s.memoryLocation) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(s.memoryDescription) },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) { titleError = true; return@Button }
                            onSave(
                                (initial ?: MemoryEntity(title = "")).copy(
                                    title = title.trim(),
                                    memoryDate = date.trim(),
                                    location = location.trim(),
                                    description = description.trim(),
                                    mediaPath = mediaPath,
                                    mediaType = if (mediaPath.isBlank()) MemoryMediaType.NOTE else mediaType
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_memory_button")
                    ) { Text(s.save) }
                }
            }
        }
    }
}

@Composable
private fun MemoryMediaViewerDialog(
    memory: MemoryEntity,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (memory.mediaType == MemoryMediaType.VIDEO && memory.mediaPath.isNotBlank() && File(memory.mediaPath).exists()) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val controller = MediaController(ctx)
                                controller.setAnchorView(this)
                                setMediaController(controller)
                                setVideoURI(Uri.fromFile(File(memory.mediaPath)))
                                setOnPreparedListener { mp ->
                                    mp.isLooping = false
                                    start()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                } else if (memory.mediaPath.isNotBlank() && File(memory.mediaPath).exists()) {
                    AsyncImage(
                        model = File(memory.mediaPath),
                        contentDescription = memory.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(memory.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                        if (memory.memoryDate.isNotBlank()) {
                            Text(memory.memoryDate, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.78f))
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = s.close, tint = Color.White)
                    }
                }

                if (memory.description.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = memory.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
