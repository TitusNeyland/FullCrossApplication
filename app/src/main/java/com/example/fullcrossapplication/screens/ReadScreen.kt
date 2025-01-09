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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadScreen(viewModel: BibleViewModel = viewModel()) {
    val bibles by viewModel.bibles.collectAsStateWithLifecycle()
    val books by viewModel.books.collectAsStateWithLifecycle()
    val currentChapter by viewModel.currentChapter.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var selectedBible by remember { mutableStateOf<Bible?>(null) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }

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
                                    selectedBook = null
                                }
                                selectedBook != null -> {
                                    selectedBook = null
                                }
                                selectedBible != null -> {
                                    selectedBible = null
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
                            .padding(16.dp)
                    ) {
                        item {
                            Text(
                                text = currentChapter!!.content,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                // Show book selection
                selectedBible != null && selectedBook == null -> {
                    LazyColumn {
                        items(books) { book ->
                            BookItem(book) {
                                selectedBook = it
                                viewModel.loadChapter(selectedBible!!.id, "${it.id}.1")
                            }
                        }
                    }
                }
                // Show bible versions
                else -> {
                    LazyColumn {
                        items(bibles) { bible ->
                            BibleItem(bible) {
                                selectedBible = it
                                viewModel.loadBooks(it.id)
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