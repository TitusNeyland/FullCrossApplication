package com.example.fullcrossapplication.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fullcrossapplication.viewmodels.AuthViewModel

enum class BottomNavItem(val title: String, val icon: ImageVector) {
    Read("Read", Icons.Default.Book),
    Notes("Notes", Icons.Default.Edit),
    Watch("Watch", Icons.Default.PlayCircle),
    Donate("Donate", Icons.Default.MonetizationOn),
    Account("Account", Icons.Default.AccountCircle),
    Admin("Admin", Icons.Default.AdminPanelSettings)
}

@Composable
fun MainScreen(
    onSignOut: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf(BottomNavItem.Watch) }
    var showLoginScreen by remember { mutableStateOf(false) }
    var showContactSupport by remember { mutableStateOf(false) }
    var showHelpAndFaq by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showEditProfile by remember { mutableStateOf(false) }
    var showFriendsList by remember { mutableStateOf(false) }
    
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.roles?.contains("admin") ?: false

    if (showLoginScreen) {
        LoginScreen(
            onLoginSuccess = { showLoginScreen = false },
            onSignUpClick = { /* Handle sign up */ }
        )
        return
    }

    if (showContactSupport) {
        ContactSupportScreen(
            onNavigateBack = { showContactSupport = false }
        )
        return
    }

    if (showHelpAndFaq) {
        HelpAndFaqScreen(
            onNavigateBack = { showHelpAndFaq = false }
        )
        return
    }

    if (showChangePassword) {
        ChangePasswordScreen(
            onNavigateBack = { showChangePassword = false }
        )
        return
    }

    if (showEditProfile) {
        EditProfileScreen(
            onNavigateBack = { showEditProfile = false }
        )
        return
    }

    if (showFriendsList) {
        FriendsScreen(
            onNavigateBack = { showFriendsList = false }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = if (isAdmin) {
                    BottomNavItem.values().toList()
                } else {
                    BottomNavItem.values().filter { it != BottomNavItem.Admin }
                }
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedItem) {
                BottomNavItem.Read -> ReadScreen()
                BottomNavItem.Notes -> NotesScreen()
                BottomNavItem.Watch -> WatchScreen()
                BottomNavItem.Donate -> DonateScreen()
                BottomNavItem.Account -> AccountScreen(
                    onLogout = onSignOut,
                    onNavigateToSupport = { showContactSupport = true },
                    onNavigateToHelpAndFaq = { showHelpAndFaq = true },
                    onNavigateToChangePassword = { showChangePassword = true },
                    onNavigateToEditProfile = { showEditProfile = true },
                    onNavigateToFriends = { showFriendsList = true }
                )
                BottomNavItem.Admin -> AdminScreen()
            }
        }
    }
} 