package com.example.fullcrossapplication.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FaqItem(
    val question: String,
    val answer: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndFaqScreen(
    onNavigateBack: () -> Unit
) {
    val faqItems = remember {
        listOf(
            FaqItem(
                "How do I add a new note?",
                "To add a new note, go to the Notes screen and tap the '+' icon in the top right corner. Fill in the title, content, and optionally add a verse reference and select a note type.",
                "Notes"
            ),
            FaqItem(
                "Can I delete my notes?",
                "Yes, you can delete any note by opening it and tapping the delete (trash) icon. You'll be asked to confirm before the note is permanently deleted.",
                "Notes"
            ),
            FaqItem(
                "How do I read the Bible?",
                "Navigate to the Read screen, select your preferred Bible version, choose a book, and then select a chapter to start reading. You can navigate between chapters using the arrow buttons.",
                "Bible Reading"
            ),
            FaqItem(
                "How do I make a donation?",
                "Go to the Donate screen where you'll find multiple payment options including PayPal, Cash App, Venmo, and more. Choose your preferred method and follow the instructions.",
                "Donations"
            ),
            FaqItem(
                "How do I change my password?",
                "Go to the Account screen, tap on 'Change Password', enter your current password and your new password twice to confirm the change.",
                "Account"
            ),
            FaqItem(
                "How do I enable notifications?",
                "In the Account screen, find the Notifications toggle. If it's your first time, you'll be asked to grant permission. You can always adjust these settings later.",
                "Account"
            ),
            FaqItem(
                "What are note types used for?",
                "Note types help organize your spiritual journey. You can categorize notes as General, Prayer, Sermon, or Study to better track different aspects of your faith journey.",
                "Notes"
            ),
            FaqItem(
                "How do I contact support?",
                "Go to the Account screen and tap 'Contact Support'. You can reach us through email, phone, or send a direct message through the app.",
                "Support"
            )
        )
    }

    val groupedFaqs = remember(faqItems) {
        faqItems.groupBy { it.category }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & FAQ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            groupedFaqs.forEach { (category, items) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(items) { faqItem ->
                    FaqCard(faqItem)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FaqCard(faqItem: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = faqItem.question,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faqItem.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
} 