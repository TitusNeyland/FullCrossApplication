package com.example.fullcrossapplication.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fullcrossapplication.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.TextButton
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.viewmodels.ContactsViewModel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.example.fullcrossapplication.viewmodels.NotificationsViewModel
import androidx.compose.material3.Badge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fullcrossapplication.data.NotificationType

data class LiveStream(
    val title: String,
    val thumbnailUrl: String,
    val startTime: LocalDateTime,
    val durationMinutes: Long,
    val viewerCount: Int,
    val isLive: Boolean = false,
    val facebookUrl: String = "https://www.facebook.com/profile.php?id=61555182075913" // Default Facebook URL
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchScreen(
    notificationsViewModel: NotificationsViewModel = viewModel()
) {
    var showSocialDialog by remember { mutableStateOf(false) }
    val viewerCount = 128 // Example viewer count
    val context = LocalContext.current
    val unreadCount by notificationsViewModel.unreadCount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        // Enhanced Header
        LargeTopAppBar(
            title = {
                Column {
                    Text(
                        "Watch Now",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        "View the latest live broadcast here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            actions = {
                Box {
                    IconButton(
                        onClick = { showSocialDialog = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = "Connect with Friends",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (unreadCount > 0) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        ) {
                            Text(unreadCount.toString())
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Featured Live Stream
            item {
                FeaturedStreamCard(
                    stream = LiveStream(
                        title = "Sunday Morning Service",
                        thumbnailUrl = "https://example.com/thumbnail.jpg",
                        startTime = LocalDateTime.now(),
                        durationMinutes = 60,
                        viewerCount = viewerCount,
                        isLive = true,
                        facebookUrl = "https://www.facebook.com/100079371798055/videos/655144869785669"
                    )
                )
            }

            // Upcoming Streams Section
            item {
                Text(
                    "Upcoming Services",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Example upcoming streams
            items(getUpcomingStreams()) { stream ->
                UpcomingStreamCard(stream = stream)
            }
        }
    }

    if (showSocialDialog) {
        SocialConnectionDialog(
            onDismiss = { showSocialDialog = false },
            onSyncContacts = {
                // TODO: Implement contact sync
                Toast.makeText(context, "Syncing contacts...", Toast.LENGTH_SHORT).show()
                showSocialDialog = false
            },
            onReferContacts = {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Join me on our church app! [Your app link here]")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Refer Friends"))
                showSocialDialog = false
            },
            onAddFriends = {
                // TODO: Implement add friends
                Toast.makeText(context, "Add friends feature coming soon!", Toast.LENGTH_SHORT).show()
                showSocialDialog = false
            }
        )
    }
}

@Composable
private fun FeaturedStreamCard(stream: LiveStream) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Placeholder/Loading state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            // Actual image
            AsyncImage(
                model = stream.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_christian_cross) // Fallback image
            )

            // Live indicator
            if (stream.isLive) {
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.error
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "LIVE",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stream.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Viewers",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${stream.viewerCount} watching",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Duration",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${stream.durationMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(stream.facebookUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Unable to open Facebook",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Join Stream")
            }
        }
    }
}

@Composable
private fun UpcomingStreamCard(stream: LiveStream) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(72.dp)
            ) {
                Text(
                    text = stream.startTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stream.startTime.format(DateTimeFormatter.ofPattern("EEE")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .height(40.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Content column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stream.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${stream.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { /* Handle reminder */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Set reminder",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun getUpcomingStreams(): List<LiveStream> {
    val now = LocalDateTime.now()
    
    // Get next Wednesday at 6 PM
    val nextWednesday = now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.WEDNESDAY))
        .withHour(18)  // 6 PM
        .withMinute(0)
        .withSecond(0)

    // Get next Sunday at 9 AM
    val nextSunday = now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
        .withHour(9)   // 9 AM
        .withMinute(0)
        .withSecond(0)

    return listOf(
        LiveStream(
            title = "Wednesday Bible Study",
            thumbnailUrl = "https://example.com/thumbnail1.jpg",
            startTime = nextWednesday,
            durationMinutes = 50,
            viewerCount = 0,
            facebookUrl = "https://www.facebook.com/100079371798055/videos/655144869785669"
        ),
        LiveStream(
            title = "Sunday Morning Service",
            thumbnailUrl = "https://example.com/thumbnail2.jpg",
            startTime = nextSunday,
            durationMinutes = 50,
            viewerCount = 0,
            facebookUrl = "https://www.facebook.com/100079371798055/videos/655144869785669"
        )
    ).sortedBy { it.startTime }
}

private fun formatDuration(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60)
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}

@Composable
private fun SocialConnectionDialog(
    onDismiss: () -> Unit,
    onSyncContacts: () -> Unit,
    onReferContacts: () -> Unit,
    onAddFriends: () -> Unit,
    contactsViewModel: ContactsViewModel = viewModel(),
    notificationsViewModel: NotificationsViewModel = viewModel()
) {
    val context = LocalContext.current
    val contacts by contactsViewModel.contacts.collectAsState()
    val isLoading by contactsViewModel.isLoading.collectAsState()
    val error by contactsViewModel.error.collectAsState()
    var isSyncExpanded by remember { mutableStateOf(false) }
    var isFindFriendsExpanded by remember { mutableStateOf(false) }
    val searchResults by contactsViewModel.searchResults.collectAsState()
    val isSearching by contactsViewModel.isSearching.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val notifications by notificationsViewModel.notifications.collectAsStateWithLifecycle()
    val friendRequests = notifications.filter { 
        it.type == NotificationType.FRIEND_REQUEST && !it.read 
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            contactsViewModel.syncContacts()
            isSyncExpanded = true
        } else {
            Toast.makeText(
                context,
                "Permission needed to sync contacts",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Connect with Friends",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                // Friend Requests Section (if there are any)
                if (friendRequests.isNotEmpty()) {
                    Text(
                        "Friend Requests (${friendRequests.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .fillMaxWidth()
                    ) {
                        items(friendRequests) { request ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                ListItem(
                                    headlineContent = { 
                                        Text(request.fromUserName)
                                    },
                                    supportingContent = {
                                        Text(
                                            "Sent you a friend request",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    trailingContent = {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    // Accept friend request
                                                    contactsViewModel.acceptFriendRequest(request.fromUserId)
                                                    notificationsViewModel.markAsRead(request.id)
                                                }
                                            ) {
                                                Text("Accept")
                                            }
                                            TextButton(
                                                onClick = {
                                                    // Decline friend request
                                                    contactsViewModel.declineFriendRequest(request.fromUserId)
                                                    notificationsViewModel.markAsRead(request.id)
                                                }
                                            ) {
                                                Text("Decline")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // Existing Find Friends Section
                ListItem(
                    headlineContent = { Text("Find Friends") },
                    supportingContent = { Text("Search for other members") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { isFindFriendsExpanded = !isFindFriendsExpanded }) {
                            Icon(
                                if (isFindFriendsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isFindFriendsExpanded) "Collapse" else "Expand"
                            )
                        }
                    },
                    modifier = Modifier.clickable { isFindFriendsExpanded = !isFindFriendsExpanded }
                )
                
                if (isFindFriendsExpanded) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            contactsViewModel.searchUsers(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        placeholder = { 
                            Text(
                                "Search by name or phone number",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { 
                                        searchQuery = ""
                                        contactsViewModel.clearSearch()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (isSearching) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    
                    if (searchResults.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .height(200.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            items(searchResults) { user ->
                                ListItem(
                                    headlineContent = { Text(user.fullName) },
                                    supportingContent = { 
                                        Text(
                                            user.phoneNumber.takeIf { it.isNotEmpty() }
                                                ?.let { formatPhoneNumber(it) } ?: "No phone number"
                                        )
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.PersonAdd,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        contactsViewModel.sendFriendRequest(
                                            toUserId = user.id,
                                            toUserName = user.fullName
                                        )
                                        Toast.makeText(
                                            context,
                                            "Friend request sent to ${user.firstName}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                    } else if (searchQuery.length >= 2 && !isSearching) {
                        Text(
                            "No users found",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Sync Contacts Section (Now Second)
                ListItem(
                    headlineContent = { Text("Sync Contacts") },
                    supportingContent = { Text("Find friends who are already using the app") },
                    leadingContent = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.ContactPhone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    trailingContent = {
                        if (contacts.isNotEmpty()) {
                            IconButton(onClick = { isSyncExpanded = !isSyncExpanded }) {
                                Icon(
                                    if (isSyncExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isSyncExpanded) "Collapse" else "Expand"
                                )
                            }
                        }
                    },
                    modifier = Modifier.clickable {
                        if (contacts.isEmpty()) {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        } else {
                            isSyncExpanded = !isSyncExpanded
                        }
                    }
                )
                
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
                // Contacts List (Expandable)
                if (contacts.isNotEmpty() && isSyncExpanded) {
                    Text(
                        "Contacts (${contacts.size})",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(contacts) { contact ->
                            ListItem(
                                headlineContent = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(contact.name)
                                        if (contact.isAppUser) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                                    .size(8.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }
                                },
                                supportingContent = { 
                                    contact.phoneNumber?.let { Text(it) }
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = if (contact.isAppUser) 
                                            MaterialTheme.colorScheme.primary
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                },
                                modifier = Modifier.clickable(enabled = !contact.isAppUser) {
                                    if (!contact.isAppUser) {
                                        // TODO: Implement invite friend functionality
                                    }
                                }
                            )
                        }
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

private fun formatPhoneNumber(number: String): String {
    return try {
        val cleaned = number.replace("[^0-9]".toRegex(), "")
        when (cleaned.length) {
            10 -> "(${cleaned.substring(0,3)}) ${cleaned.substring(3,6)}-${cleaned.substring(6)}"
            else -> number
        }
    } catch (e: Exception) {
        number
    }
} 