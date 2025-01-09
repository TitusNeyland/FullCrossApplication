package com.example.fullcrossapplication.screens

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.sp
import com.example.fullcrossapplication.utils.BibleTextFormatter
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.filled.NavigateBefore

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

    Column(modifier = Modifier.fillMaxSize()) {
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
                                Text(
                                    text = BibleTextFormatter.formatBibleText(currentChapter!!.content),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        lineHeight = 28.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
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