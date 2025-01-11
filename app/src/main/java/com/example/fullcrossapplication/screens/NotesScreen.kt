package com.example.fullcrossapplication.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.data.Note
import com.example.fullcrossapplication.data.NoteType
import com.example.fullcrossapplication.viewmodels.NotesViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: NotesViewModel = viewModel()) {
    var showAddNoteDialog by remember { mutableStateOf(false) }
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val datesWithNotes by viewModel.datesWithNotes.collectAsStateWithLifecycle()
    var expandedDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(datesWithNotes) {
        if (datesWithNotes.contains(LocalDate.now()) && expandedDate != LocalDate.now()) {
            expandedDate = LocalDate.now()
            viewModel.setSelectedDate(LocalDate.now())
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Enhanced Header
        TopAppBar(
            title = {
                Column {
                    Text(
                        "Your Journey Notes",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "${datesWithNotes.size} days of reflection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { showAddNoteDialog = true },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Note",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dates List with enhanced styling
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
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
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
        targetValue = if (isExpanded) 8f else 2f,
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
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
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
                        tint = MaterialTheme.colorScheme.primary
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
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
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
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    minLines = 3
                )
                OutlinedTextField(
                    value = verseReference,
                    onValueChange = { verseReference = it },
                    label = { Text("Verse Reference (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
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
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (noteType == type)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(
                                text = type.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (noteType == type)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface
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
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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
                Text("Cancel")
            }
        }
    )
} 