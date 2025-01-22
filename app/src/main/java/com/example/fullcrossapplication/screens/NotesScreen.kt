package com.example.fullcrossapplication.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.data.Comment
import com.example.fullcrossapplication.data.Discussion
import com.example.fullcrossapplication.data.Note
import com.example.fullcrossapplication.data.NoteType
import com.example.fullcrossapplication.data.NotesTab
import com.example.fullcrossapplication.viewmodels.NotesViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: NotesViewModel = viewModel()) {
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddDiscussionDialog by remember { mutableStateOf(false) }
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val datesWithNotes by viewModel.datesWithNotes.collectAsStateWithLifecycle()
    var expandedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTab by remember { mutableStateOf(NotesTab.PERSONAL_NOTES) }
    var selectedDiscussionForComment by remember { mutableStateOf<Discussion?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(datesWithNotes) {
        if (datesWithNotes.contains(LocalDate.now()) && expandedDate != LocalDate.now()) {
            expandedDate = LocalDate.now()
            viewModel.setSelectedDate(LocalDate.now())
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            ) { data ->
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { snackbarHostState.currentSnackbarData?.dismiss() }
                        ) {
                            Text("Dismiss", color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.Black
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(data.visuals.message)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Enhanced Header with Tabs
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (selectedTab) {
                                NotesTab.PERSONAL_NOTES -> "Notes"
                                NotesTab.DISCUSSIONS -> "Discussions"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = when (selectedTab) {
                                NotesTab.PERSONAL_NOTES -> "${datesWithNotes.size} days of reflection"
                                NotesTab.DISCUSSIONS -> "Join the conversation"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            if (selectedTab == NotesTab.PERSONAL_NOTES) {
                                showAddNoteDialog = true
                            } else {
                                showAddDiscussionDialog = true
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = if (selectedTab == NotesTab.PERSONAL_NOTES) 
                                "Add Note" 
                            else 
                                "Start Discussion",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        // Use a subtle green color
                        color = Color(0xFF4CAF50).copy(alpha = 0.6f)  // Material Green 500 with 60% opacity
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == NotesTab.PERSONAL_NOTES,
                    onClick = { selectedTab = NotesTab.PERSONAL_NOTES },
                    text = { 
                        Text(
                            "My Notes",
                            color = if (selectedTab == NotesTab.PERSONAL_NOTES)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ) 
                    }
                )
                Tab(
                    selected = selectedTab == NotesTab.DISCUSSIONS,
                    onClick = { selectedTab = NotesTab.DISCUSSIONS },
                    text = { 
                        Text(
                            "Discussions",
                            color = if (selectedTab == NotesTab.DISCUSSIONS)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ) 
                    }
                )
            }

            when (selectedTab) {
                NotesTab.PERSONAL_NOTES -> {
                    // Existing notes content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(datesWithNotes.toList().sortedDescending()) { date ->
                            DateCard(
                                date = date,
                                isExpanded = date == expandedDate,
                                notes = if (date == expandedDate) notes else emptyList(),
                                onExpandClick = {
                                    if (expandedDate == date) {
                                        expandedDate = null
                                    } else {
                                        expandedDate = date
                                        viewModel.setSelectedDate(date)
                                    }
                                },
                                onDeleteNote = { note ->
                                    viewModel.deleteNote(note)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Note deleted",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                NotesTab.DISCUSSIONS -> {
                    DiscussionsContent(
                        viewModel = viewModel,
                        onDiscussionClick = { /* Handle discussion click */ }
                    )
                }
            }
        }
    }

    if (showAddNoteDialog) {
        AddNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onNoteAdded = { title, content, verseRef, type ->
                viewModel.addNote(title, content, verseRef, type)
                expandedDate = LocalDate.now()
                showAddNoteDialog = false
                // Show snackbar using coroutine scope
                kotlinx.coroutines.MainScope().launch {
                    snackbarHostState.showSnackbar(
                        message = "Note successfully added",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }

    if (showAddDiscussionDialog) {
        AddDiscussionDialog(
            onDismiss = { showAddDiscussionDialog = false },
            onDiscussionAdded = { title, content ->
                viewModel.addDiscussion(title, content)
                showAddDiscussionDialog = false
                // Show snackbar for discussion posted
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Discussion posted successfully",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }
}

@Composable
private fun DateCard(
    date: LocalDate,
    isExpanded: Boolean,
    notes: List<Note>,
    onExpandClick: () -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    val elevation by animateFloatAsState(
        targetValue = if (isExpanded) 4f else 1f,
        label = "elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        border = null
    ) {
        Column {
            // Date Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onExpandClick)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Notes List (only shown when expanded)
            if (isExpanded) {
                notes.forEach { note ->
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    NoteItem(
                        note = note,
                        onDelete = { onDeleteNote(note) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteItem(
    note: Note,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (note.verseReference != null) {
                    Text(
                        text = note.verseReference,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(MaterialTheme.shapes.small)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Note",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = note.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        // Note type chip
        Text(
            text = note.type.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    discussionId: String,
    currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    onDeleteComment: (String) -> Unit = {},
    onReply: (Comment) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReplies by remember { mutableStateOf(false) }
    val viewModel: NotesViewModel = viewModel()
    val discussions by viewModel.discussions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize()
    ) {
        // Main comment content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (showReplies && comment.replyCount > 0) 0.dp else 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = comment.authorName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatTimestamp(comment.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                // Reply button only
                if (!comment.isReply) {
                    TextButton(
                        onClick = { onReply(comment) },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            "Reply",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Show who the comment is replying to if it's a reply
            if (comment.replyToAuthorName != null) {
                Text(
                    text = "Replying to ${comment.replyToAuthorName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Bottom row with replies toggle and delete button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    // Show replies count and toggle button if there are replies
                    if (comment.replyCount > 0 && !comment.isReply) {
                        TextButton(
                            onClick = { showReplies = !showReplies },
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (showReplies) "Hide replies" else "Show ${comment.replyCount} replies",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Icon(
                                    imageVector = if (showReplies) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (showReplies) "Hide replies" else "Show replies",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                
                // Delete button for comment author
                if (comment.authorId == currentUserId) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Delete comment",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Show replies if expanded
        androidx.compose.animation.AnimatedVisibility(
            visible = showReplies && comment.replyCount > 0,
            enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp)
            ) {
                // Get replies from the discussion's comments list
                val discussion = discussions.find { it.id == discussionId }
                val replies = discussion?.comments?.filter { it.parentCommentId == comment.id } ?: emptyList()
                
                replies.forEach { reply ->
                    CommentItem(
                        comment = reply,
                        discussionId = discussionId,
                        currentUserId = currentUserId,
                        onDeleteComment = onDeleteComment,
                        onReply = onReply
                    )
                    if (reply != replies.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Comment") },
            text = { Text("Are you sure you want to delete this comment?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteComment(comment.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel",
                              color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddNoteDialog(
    onDismiss: () -> Unit,
    onNoteAdded: (String, String, String?, NoteType) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var verseReference by remember { mutableStateOf("") }
    var noteType by remember { mutableStateOf(NoteType.GENERAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Note") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
                OutlinedTextField(
                    value = verseReference,
                    onValueChange = { verseReference = it },
                    label = { Text("Verse Reference (Optional)", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NoteType.values().forEach { type ->
                        Surface(
                            onClick = { noteType = type },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            shape = MaterialTheme.shapes.small,
                            color = if (noteType == type) 
                                Color(0xFF4CAF50).copy(alpha = 0.5f)  // More visible green background when selected
                            else 
                                Color(0xFF4CAF50).copy(alpha = 0.1f),  // Light green background when not selected
                            border = BorderStroke(
                                1.dp,
                                if (noteType == type)
                                    Color(0xFF2E7D32)  // Darker green border when selected
                                else
                                    Color(0xFF2E7D32).copy(alpha = 0.7f)  // Slightly transparent dark green when not selected
                            )
                        ) {
                            Text(
                                text = type.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (noteType == type)
                                    Color(0xFF1B5E20)  // Even darker green text when selected (Green 900)
                                else
                                    Color(0xFF2E7D32)  // Dark green text when not selected (Green 800)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onNoteAdded(
                            title,
                            content,
                            verseReference.takeIf { it.isNotBlank() },
                            noteType
                        )
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Note") },
        text = { Text("Are you sure you want to delete this note?") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel",
                           color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}

@Composable
private fun DiscussionsContent(
    viewModel: NotesViewModel,
    onDiscussionClick: (Discussion) -> Unit
) {
    val discussions by viewModel.discussions.collectAsStateWithLifecycle()
    var selectedDiscussion by remember { mutableStateOf<Discussion?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(discussions) { discussion ->
            DiscussionCard(
                discussion = discussion,
                onClick = { selectedDiscussion = discussion },
                onLikeClick = { viewModel.likeDiscussion(discussion.id) },
                onCommentClick = { selectedDiscussion = discussion }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    selectedDiscussion?.let { discussion ->
        FullDiscussionSheet(
            discussion = discussion,
            onDismiss = { selectedDiscussion = null },
            onCommentAdded = { content ->
                viewModel.addComment(discussion.id, content)
            }
        )
    }
}

@Composable
private fun DiscussionCard(
    discussion: Discussion,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var showDeleteDialog by remember { mutableStateOf(false) }
    val viewModel: NotesViewModel = viewModel()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = discussion.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                // Show delete button only for the discussion author
                if (discussion.authorId == currentUserId) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Delete discussion",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Text(
                text = discussion.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${discussion.authorName}",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = "Like",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${discussion.likes}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Comment,
                                contentDescription = "Comment",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${discussion.commentCount}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Discussion") },
            text = { Text("Are you sure you want to delete this discussion? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDiscussion(discussion.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel",
                              color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullDiscussionSheet(
    discussion: Discussion,
    onDismiss: () -> Unit,
    onCommentAdded: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var newCommentText by remember { mutableStateOf("") }
    val viewModel: NotesViewModel = viewModel()
    val currentUserName by viewModel.currentUserName.collectAsStateWithLifecycle()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var showDeleteDialog by remember { mutableStateOf(false) }
    var replyingTo by remember { mutableStateOf<Comment?>(null) }
    
    // Get the latest version of the discussion from the discussions list
    val discussions by viewModel.discussions.collectAsStateWithLifecycle()
    val updatedDiscussion = discussions.find { it.id == discussion.id } ?: discussion

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxHeight(0.95f)
            .imePadding(),
        windowInsets = WindowInsets.ime.union(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .navigationBarsPadding()
        ) {
            // Discussion Header with Delete Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = updatedDiscussion.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (updatedDiscussion.authorId == currentUserId) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Delete discussion",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${updatedDiscussion.authorName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = formatTimestamp(updatedDiscussion.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                text = updatedDiscussion.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Like button and comment count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable {
                        viewModel.likeDiscussion(updatedDiscussion.id)
                    }
                ) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        modifier = Modifier.size(16.dp),
                        tint = if (currentUserId in updatedDiscussion.likedByUsers)
                            MaterialTheme.colorScheme.onBackground
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${updatedDiscussion.likes}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Comment,
                        contentDescription = "Comments",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${updatedDiscussion.comments.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Divider()

            Text(
                text = "Comments (${updatedDiscussion.comments.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Comments list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(updatedDiscussion.comments.filter { !it.isReply }) { comment ->
                    CommentItem(
                        comment = comment,
                        discussionId = updatedDiscussion.id,
                        currentUserId = currentUserId,
                        onDeleteComment = { commentId ->
                            viewModel.deleteComment(updatedDiscussion.id, commentId)
                        },
                        onReply = { replyingTo = it }
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            // Comment input with reply indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Show who we're replying to
                if (replyingTo != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Replying to ${replyingTo?.authorName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { replyingTo = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cancel reply",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        label = { 
                            Text(
                                if (replyingTo != null) "Write a reply..." else "Add a comment",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (newCommentText.isNotBlank()) {
                                        viewModel.addComment(
                                            discussionId = updatedDiscussion.id,
                                            content = newCommentText,
                                            parentCommentId = replyingTo?.id,
                                            replyToAuthorName = replyingTo?.authorName
                                        )
                                        newCommentText = ""
                                        replyingTo = null
                                    }
                                },
                                enabled = newCommentText.isNotBlank()
                            ) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Send comment",
                                    tint = if (newCommentText.isNotBlank()) 
                                        MaterialTheme.colorScheme.onBackground 
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Discussion") },
            text = { Text("Are you sure you want to delete this discussion? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDiscussion(discussion.id)
                        showDeleteDialog = false
                        onDismiss()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel",
                              color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDiscussionDialog(
    onDismiss: () -> Unit,
    onDiscussionAdded: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start a Discussion") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Share your thoughts...", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onDiscussionAdded(title, content)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000} minutes ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            .format(Date(timestamp))
    }
} 