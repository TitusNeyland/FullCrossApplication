package com.example.fullcrossapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.data.Bible
import com.example.fullcrossapplication.data.Book
import com.example.fullcrossapplication.viewmodels.BibleViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.fullcrossapplication.utils.BibleTextFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadScreen(viewModel: BibleViewModel = viewModel()) {
    val bibles by viewModel.bibles.collectAsStateWithLifecycle()
    val books by viewModel.books.collectAsStateWithLifecycle()
    val currentChapter by viewModel.currentChapter.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val selectedBible by viewModel.selectedBible.collectAsStateWithLifecycle()
    val selectedBook by viewModel.selectedBook.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showNoteDialog by remember { mutableStateOf(false) }
    val selectedVerse by viewModel.selectedVerse.collectAsStateWithLifecycle()
    val verseOfDay by viewModel.verseOfDay.collectAsStateWithLifecycle()
    val isLoadingVerse by viewModel.isLoadingVerse.collectAsStateWithLifecycle()
    val verseError by viewModel.verseError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Navigation Bar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = when {
                        currentChapter != null -> selectedBook?.name ?: ""
                        selectedBook != null -> "Select Chapter"
                        selectedBible != null -> "Select Book"
                        else -> "Select Bible Version"
                    }
                )
            },
            navigationIcon = {
                if (selectedBible != null) {
                    IconButton(
                        onClick = {
                            when {
                                currentChapter != null -> {
                                    viewModel.clearChapter()
                                }
                                selectedBook != null -> {
                                    viewModel.setSelectedBook(null)
                                }
                                selectedBible != null -> {
                                    viewModel.setSelectedBible(null)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            }
        )

        // Content
        Box(modifier = Modifier.weight(1f)) {
            when {
                // Show chapter content
                currentChapter != null -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        item {
                            Column {
                                // Add a row for the title and next chapter button
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Previous Chapter button
                                    IconButton(
                                        onClick = {
                                            selectedBible?.let { bible ->
                                                viewModel.loadPreviousChapter(bible.id)
                                            }
                                        },
                                        enabled = viewModel.currentChapterNumber.collectAsStateWithLifecycle().value?.let { it > 1 } ?: false
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NavigateBefore,
                                            contentDescription = "Previous Chapter"
                                        )
                                    }

                                    // Chapter title
                                    Text(
                                        text = "${selectedBook?.name} Chapter ${currentChapter!!.number}",
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    // Next Chapter button
                                    IconButton(
                                        onClick = {
                                            selectedBible?.let { bible ->
                                                viewModel.loadNextChapter(bible.id)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NavigateNext,
                                            contentDescription = "Next Chapter"
                                        )
                                    }
                                }

                                // Chapter content
                                BibleTextFormatter.formatBibleText(currentChapter!!.content).forEach { verse ->
                                    VerseItem(
                                        verse = verse,
                                        onVerseClick = {
                                            viewModel.setSelectedVerse("${selectedBook?.name} ${currentChapter?.number}:${verse.verseNumber}")
                                            showNoteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                // Show book selection
                selectedBible != null && selectedBook == null -> {
                    LazyColumn {
                        items(books) { book ->
                            BookItem(book) {
                                viewModel.setSelectedBook(it)
                                viewModel.loadChapter(selectedBible!!.id, "${it.id}.1")
                            }
                        }
                    }
                }
                // Show bible versions
                else -> {
                    Column {
                        // Add search field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            placeholder = { Text("Search Bible versions...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            singleLine = true
                        )

                        // Filter bibles based on search query
                        LazyColumn {
                            items(bibles.filter { bible ->
                                searchQuery.isEmpty() || bible.name.contains(searchQuery, ignoreCase = true) ||
                                        bible.language.name.contains(searchQuery, ignoreCase = true)
                            }) { bible ->
                                BibleItem(bible) {
                                    viewModel.setSelectedBible(it)
                                    viewModel.loadBooks(it.id)
                                }
                            }
                        }
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                )
            }

            // Error message
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }

        // Move Verse of the Day to bottom
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verse of the Day",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        onClick = { viewModel.refreshVerseOfDay() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh verse",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (isLoadingVerse) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(20.dp)
                    )
                } else if (verseError != null) {
                    Text(
                        text = verseError ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(4.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    verseOfDay?.let { verse ->
                        Text(
                            text = verse.text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "- ${verse.reference}",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }

    // Add note dialog
    if (showNoteDialog && selectedVerse != null) {
        AddVerseNoteDialog(
            verseReference = selectedVerse!!,
            onDismiss = { showNoteDialog = false },
            onNoteAdded = { title, content ->
                viewModel.addVerseNote(title, content, selectedVerse!!)
                showNoteDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BibleItem(bible: Bible, onClick: (Bible) -> Unit) {
    Card(
        onClick = { onClick(bible) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = bible.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = bible.language.name,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookItem(book: Book, onClick: (Book) -> Unit) {
    Card(
        onClick = { onClick(book) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = book.name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun VerseItem(
    verse: BibleTextFormatter.FormattedVerse,
    onVerseClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onVerseClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (verse.isVerseStart) {
                Text(
                    text = "[${verse.verseNumber}]",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = verse.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVerseNoteDialog(
    verseReference: String,
    onDismiss: () -> Unit,
    onNoteAdded: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Note for $verseReference") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Your thoughts") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onNoteAdded(title, content)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}