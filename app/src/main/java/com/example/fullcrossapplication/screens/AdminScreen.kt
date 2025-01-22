package com.example.fullcrossapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.viewmodels.AdminViewModel
import com.example.fullcrossapplication.viewmodels.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    authViewModel: AuthViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel()
) {
    val streamSettings by adminViewModel.streamSettings.collectAsState()
    var streamUrl by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        adminViewModel.loadStreamSettings()
    }
    
    LaunchedEffect(streamSettings) {
        streamUrl = streamSettings?.streamUrl ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Admin Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stream Settings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Stream Settings",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = streamUrl,
                            onValueChange = { streamUrl = it },
                            label = { Text("Stream URL") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                cursorColor = Color.Black
                            ),
                            supportingText = {
                                streamSettings?.let { settings ->
                                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                    Text(
                                        "Last updated: ${dateFormat.format(Date(settings.lastUpdated))}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                adminViewModel.updateStreamUrl(streamUrl)
                                showSuccessMessage = true
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }

    if (showSuccessMessage) {
        AlertDialog(
            onDismissRequest = { showSuccessMessage = false },
            title = { Text("Success") },
            text = { Text("Stream URL has been updated successfully.") },
            confirmButton = {
                TextButton(
                    onClick = { showSuccessMessage = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
} 