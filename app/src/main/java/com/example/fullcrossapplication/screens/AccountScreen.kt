package com.example.fullcrossapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fullcrossapplication.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onLogout: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${currentUser?.firstName} ${currentUser?.lastName}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = currentUser?.email ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Account Settings
        Text(
            text = "Account Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ListItem(
            headlineContent = { Text("Edit Profile") },
            leadingContent = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Profile"
                )
            },
            modifier = Modifier.clickable { /* Handle edit profile click */ }
        )

        ListItem(
            headlineContent = { Text("Change Password") },
            leadingContent = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Change Password"
                )
            },
            modifier = Modifier.clickable { /* Handle change password click */ }
        )

        ListItem(
            headlineContent = { Text("Notifications") },
            leadingContent = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            },
            modifier = Modifier.clickable { /* Handle notifications click */ }
        )

        // App Settings
        Text(
            text = "App Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ListItem(
            headlineContent = { Text("Dark Mode") },
            leadingContent = {
                Icon(
                    Icons.Default.DarkMode,
                    contentDescription = "Dark Mode"
                )
            },
            trailingContent = {
                Switch(
                    checked = false,
                    onCheckedChange = { /* Handle dark mode toggle */ }
                )
            }
        )

        ListItem(
            headlineContent = { Text("Language") },
            leadingContent = {
                Icon(
                    Icons.Default.Language,
                    contentDescription = "Language"
                )
            },
            modifier = Modifier.clickable { /* Handle language click */ }
        )

        // Support Section
        Text(
            text = "Support",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ListItem(
            headlineContent = { Text("Help & FAQ") },
            leadingContent = {
                Icon(
                    Icons.Default.Help,
                    contentDescription = "Help & FAQ"
                )
            },
            modifier = Modifier.clickable { /* Handle help click */ }
        )

        ListItem(
            headlineContent = { Text("Contact Support") },
            leadingContent = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Contact Support"
                )
            },
            modifier = Modifier.clickable { /* Handle contact support click */ }
        )

        // Logout Button
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                Icons.Default.Logout,
                contentDescription = "Logout",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Logout")
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 