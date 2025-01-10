package com.example.fullcrossapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import com.example.fullcrossapplication.screens.DonateScreen
import com.example.fullcrossapplication.screens.ReadScreen
import com.example.fullcrossapplication.screens.NotesScreen
import com.example.fullcrossapplication.screens.AccountScreen
import com.example.fullcrossapplication.screens.LoginScreen
import com.example.fullcrossapplication.screens.WatchScreen
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp

enum class BottomNavItem(val title: String, val icon: ImageVector) {
    Read("Read", Icons.Default.Book),
    Notes("Notes", Icons.Default.Edit),
    Watch("Watch", Icons.Default.PlayCircle),
    Donate("Donate", Icons.Default.MonetizationOn),
    Account("Account", Icons.Default.AccountCircle)
}

@Composable
fun MainScreen(
    onSignOut: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(BottomNavItem.Watch) }
    var showLoginScreen by remember { mutableStateOf(false) }

    if (showLoginScreen) {
        LoginScreen(
            onLoginSuccess = { showLoginScreen = false },
            onSignUpClick = { /* Handle sign up */ }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.values().forEach { item ->
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
                    onLogout = onSignOut
                )
            }
        }
    }
} 