package com.example.fullcrossapplication.screens

import android.app.Application
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.viewmodels.ChatMessage
import com.example.fullcrossapplication.viewmodels.ChatTab
import com.example.fullcrossapplication.viewmodels.WatchViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.lifecycle.ViewModelProvider

@Composable
fun WatchScreen(
    viewModel: WatchViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val focusManager = LocalFocusManager.current
    val verseOfDay by viewModel.verseOfDay.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val viewerCount by viewModel.viewerCount.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    var chatMessage by remember { mutableStateOf("") }
    var isReplyMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Facebook Video Section with Viewer Count
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header row with title and viewer count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Latest Message",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    
                    // Viewer count
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Viewers",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$viewerCount watching",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Facebook Video Player
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = WebViewClient()
                            loadUrl("https://www.facebook.com/plugins/video.php?href=https://fb.watch/x0XNoN63M6/&show_text=false")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }

        // Live Chat Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Tab Row
                val selectedTab by viewModel.selectedTab.collectAsState()
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChatTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { viewModel.setSelectedTab(tab) },
                            text = { Text(tab.name) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    ChatTab.CHAT -> {
                        // Chat Messages
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .heightIn(
                                    min = 100.dp,
                                    max = 300.dp
                                )
                        ) {
                            items(chatMessages) { message ->
                                ChatMessageItem(
                                    message = message,
                                    viewModel = viewModel,
                                    onReplyStart = { isReplyMode = true },
                                    onReplyEnd = { isReplyMode = false }
                                )
                            }
                        }

                        // Only show main chat input when not in reply mode
                        if (!isReplyMode) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = chatMessage,
                                    onValueChange = { chatMessage = it },
                                    placeholder = { Text("Type a message...") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                IconButton(
                                    onClick = {
                                        if (chatMessage.isNotBlank()) {
                                            viewModel.sendChatMessage(chatMessage)
                                            chatMessage = ""
                                            focusManager.clearFocus()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send message"
                                    )
                                }
                            }
                        }
                    }
                    
                    ChatTab.NOTES -> {
                        val notes by viewModel.notes.collectAsState()
                        val showNoteDialog by viewModel.showNoteDialog.collectAsState()

                        // Notes List with Add Note Button
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Add Note Button
                            OutlinedButton(
                                onClick = { viewModel.showNoteDialog() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add note"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add New Note")
                            }

                            // Notes List
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .heightIn(
                                        min = 100.dp,
                                        max = 300.dp
                                    )
                            ) {
                                items(notes) { note ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = note.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = note.date.format(DateTimeFormatter.ofPattern("MM/dd")),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = note.content,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(top = 4.dp)
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
                                    }
                                }
                            }
                        }

                        // Note Dialog
                        if (showNoteDialog) {
                            AddVerseNoteDialog(
                                verseReference = "",  // Empty since this isn't tied to a verse
                                onDismiss = { viewModel.hideNoteDialog() },
                                onNoteAdded = { title, content ->
                                    viewModel.onNoteAdded(title, content)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Placeholder for future video content
        Text(
            text = "Video content coming soon...",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    viewModel: WatchViewModel,
    onReplyStart: () -> Unit,
    onReplyEnd: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    var showReplyInput by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Main message
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = message.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(Date(message.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = message.message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Reactions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Reaction buttons
                val reactions = listOf("👍", "❤️", "😊", "🙏")
                reactions.forEach { emoji ->
                    val count = message.reactions[emoji] ?: 0
                    ElevatedButton(
                        onClick = { viewModel.addReaction(message.id, emoji) },
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "$emoji ${if (count > 0) count else ""}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Reply button
            TextButton(
                onClick = { showReplyInput = !showReplyInput },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = if (showReplyInput) "Cancel" else "Reply",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Reply input
            if (showReplyInput) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Write a reply...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                viewModel.addReply(message.id, replyText)
                                replyText = ""
                                showReplyInput = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send reply"
                        )
                    }
                }
            }

            // Replies
            if (message.replies.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp)
                ) {
                    // Thread line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        // Vertical thread line
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height((message.replies.size * 60).dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(1.dp)
                                )
                                .align(Alignment.TopStart)
                        )

                        // Replies
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        ) {
                            message.replies.forEach { reply ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = reply.userName,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = dateFormat.format(Date(reply.timestamp)),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = reply.message,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Effect to notify parent about reply state changes
    LaunchedEffect(showReplyInput) {
        if (showReplyInput) {
            onReplyStart()
        } else {
            onReplyEnd()
        }
    }
} 