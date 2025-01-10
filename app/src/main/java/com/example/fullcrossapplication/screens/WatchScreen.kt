package com.example.fullcrossapplication.screens

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.viewmodels.ChatMessage
import com.example.fullcrossapplication.viewmodels.WatchViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WatchScreen(
    viewModel: WatchViewModel = viewModel()
) {
    val verseOfDay by viewModel.verseOfDay.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val viewerCount by viewModel.viewerCount.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    var chatMessage by remember { mutableStateOf("") }

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
                Text(
                    text = "Latest Message",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
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

                // Viewer Count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
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
                Text(
                    text = "Live Chat",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Chat Messages
                LazyColumn(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                ) {
                    items(chatMessages) { message ->
                        ChatMessageItem(message)
                    }
                }

                // Chat Input
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

        // Verse of the Day Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verse of the Day",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = { viewModel.refreshVerse() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh verse"
                        )
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (error != null) {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    verseOfDay?.let { verse ->
                        Text(
                            text = verse.text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 16.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "- ${verse.reference}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
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
private fun ChatMessageItem(message: ChatMessage) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 