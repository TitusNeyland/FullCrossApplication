package com.example.fullcrossapplication.screens

import android.Manifest
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.viewmodels.AuthViewModel
import com.example.fullcrossapplication.viewmodels.ContactsViewModel
import com.example.fullcrossapplication.viewmodels.NotificationsViewModel
import com.example.fullcrossapplication.viewmodels.ThemeViewModel
import coil.compose.AsyncImage
import coil.transform.CircleCropTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onLogout: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    onNavigateToSupport: () -> Unit,
    onNavigateToHelpAndFaq: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToFriends: () -> Unit = {},
    notificationsViewModel: NotificationsViewModel = viewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    var notificationsEnabled by remember { 
        mutableStateOf(notificationManager.areNotificationsEnabled())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
        if (!isGranted && !notificationManager.areNotificationsEnabled()) {
            showNotificationDialog = true
        }
    }

    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val friendsCount by authViewModel.friendsCount.collectAsStateWithLifecycle()
    val unreadCount by notificationsViewModel.unreadCount.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        authViewModel.fetchFriendsCount()
    }

    var showFriendsList by remember { mutableStateOf(false) }

    val contactsViewModel: ContactsViewModel = viewModel(
        factory = ContactsViewModel.provideFactory(
            application = context.applicationContext as Application,
            authViewModel = authViewModel
        )
    )

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
                val profileImageUri by authViewModel.profileImageUri.collectAsStateWithLifecycle()
                
                ProfileImage(
                    imageUri = profileImageUri,
                    onImageSelected = { uri ->
                        authViewModel.updateProfileImage(uri)
                    }
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToFriends() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Friends",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Friends",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$friendsCount friends",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "View Friends",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Add a divider for visual separation
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

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
            modifier = Modifier.clickable { onNavigateToEditProfile() }
        )

        ListItem(
            headlineContent = { Text("Change Password") },
            leadingContent = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Change Password"
                )
            },
            modifier = Modifier.clickable { onNavigateToChangePassword() }
        )

        ListItem(
            headlineContent = { Text("Notifications") },
            leadingContent = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            },
            trailingContent = {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { checked ->
                        if (checked && !notificationManager.areNotificationsEnabled()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                showNotificationDialog = true
                            }
                        } else if (!checked && notificationManager.areNotificationsEnabled()) {
                            showNotificationDialog = true
                        }
                        notificationsEnabled = checked
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF4CAF50),  // Material Green 500
                        checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f),
                        checkedBorderColor = Color(0xFF4CAF50),
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            },
            modifier = Modifier.clickable {
                if (!notificationManager.areNotificationsEnabled()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        showNotificationDialog = true
                    }
                } else {
                    showNotificationDialog = true
                }
            }
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
                    checked = isDarkMode,
                    onCheckedChange = { themeViewModel.toggleDarkMode() }
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
            modifier = Modifier.clickable { onNavigateToHelpAndFaq() }
        )

        ListItem(
            headlineContent = { Text("Contact Support") },
            leadingContent = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Contact Support"
                )
            },
            modifier = Modifier.clickable { onNavigateToSupport() }
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

        Button(
            onClick = { showFriendsList = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Manage Friends")
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

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("Notification Settings") },
            text = { 
                Text(
                    if (notificationManager.areNotificationsEnabled())
                        "Would you like to manage notification settings for this app?"
                    else
                        "To receive notifications, please enable them in your device settings."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNotificationDialog = false
                        val intent = Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                        notificationsEnabled = notificationManager.areNotificationsEnabled()
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showNotificationDialog = false
                        notificationsEnabled = notificationManager.areNotificationsEnabled()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showFriendsList) {
        FriendsList(
            contactsViewModel = contactsViewModel,
            onDismiss = { showFriendsList = false }
        )
    }
}

@Composable
fun FriendsList(
    contactsViewModel: ContactsViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val friends by contactsViewModel.friends.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        contactsViewModel.fetchFriends()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Friends List",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            if (friends.isEmpty()) {
                Text(
                    "No friends yet",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(
                        items = friends,
                        key = { it.id }
                    ) { friend ->
                        ListItem(
                            headlineContent = { 
                                Text("${friend.firstName} ${friend.lastName}")
                            },
                            supportingContent = {
                                Text(
                                    friend.phoneNumber,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = {
                                        contactsViewModel.removeFriend(friend.id)
                                        Toast.makeText(
                                            context,
                                            "Removed ${friend.firstName} from friends",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Remove friend",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                        Divider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun ProfileImage(
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = Modifier.padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .clickable { galleryLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
            
        // Camera Icon Overlay (moved outside the profile image Box)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-4).dp)
                .size(28.dp)
                .background(
                    MaterialTheme.colorScheme.onBackground,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Change Profile Picture",
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier.size(16.dp)
            )
        }
    }
} 