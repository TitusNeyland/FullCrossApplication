package com.example.fullcrossapplication.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.data.FriendshipStatus
import com.example.fullcrossapplication.data.UserProfile
import com.example.fullcrossapplication.viewmodels.FriendsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onNavigateBack: () -> Unit,
    viewModel: FriendsViewModel = viewModel()
) {
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFriends()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Friends") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                friends.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No friends yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Add friends to connect with them",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(friends) { friend ->
                            FriendItem(friend)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendItem(
    friend: UserProfile,
    viewModel: FriendsViewModel = viewModel()
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ListItem(
            headlineContent = { 
                Text(friend.fullName)
            },
            supportingContent = { 
                Text(friend.phoneNumber)
            },
            leadingContent = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            trailingContent = {
                when (friend.friendshipStatus) {
                    FriendshipStatus.NONE -> {
                        TextButton(onClick = { viewModel.sendFriendRequest(friend.id) }) {
                            Text("Add Friend")
                        }
                    }
                    FriendshipStatus.PENDING -> {
                        Row {
                            TextButton(onClick = { viewModel.acceptFriendRequest(friend.id) }) {
                                Text("Accept")
                            }
                            TextButton(onClick = { viewModel.declineFriendRequest(friend.id) }) {
                                Text("Decline")
                            }
                        }
                    }
                    FriendshipStatus.ACCEPTED -> {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Friends",
                            tint = Color(0xFF4CAF52)
                        )
                    }
                    FriendshipStatus.DECLINED -> {
                        TextButton(onClick = { viewModel.sendFriendRequest(friend.id) }) {
                            Text("Try Again")
                        }
                    }
                }
            }
        )
    }
} 