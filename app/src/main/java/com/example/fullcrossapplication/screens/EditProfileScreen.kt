package com.example.fullcrossapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fullcrossapplication.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    var firstName by remember { mutableStateOf(currentUser?.firstName ?: "") }
    var lastName by remember { mutableStateOf(currentUser?.lastName ?: "") }
    var phoneNumber by remember { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val error by authViewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = firstName,
                onValueChange = { 
                    firstName = it
                    authViewModel.clearError()
                },
                label = { Text("First Name", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { 
                    lastName = it
                    authViewModel.clearError()
                },
                label = { Text("Last Name", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { 
                    phoneNumber = it
                    authViewModel.clearError()
                },
                label = { Text("Phone Number", color = MaterialTheme.colorScheme.onBackground) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    authViewModel.clearError()
                },
                label = { Text("Email", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )

            Button(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank() && phoneNumber.isNotBlank() && email.isNotBlank()) {
                        authViewModel.updateProfile(
                            firstName,
                            lastName,
                            phoneNumber,
                            email
                        ) { success ->
                            if (success) {
                                showSuccessDialog = true
                            }
                        }
                    } else {
                        authViewModel.setError("Please fill in all required fields")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.background
                    )
                } else {
                    Text(
                        "Save Changes",
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    onNavigateBack()
                },
                title = { Text("Success") },
                text = { Text("Your profile has been successfully updated.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            onNavigateBack()
                        }
                    ) {
                        Text("OK",
                                  color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    }
}

private fun validateInputs(
    firstName: String,
    lastName: String
): Boolean {
    return firstName.isNotBlank() && lastName.isNotBlank()
} 