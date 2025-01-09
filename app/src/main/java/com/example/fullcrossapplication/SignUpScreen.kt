package com.example.fullcrossapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_christian_cross),
            contentDescription = "Christian Cross",
            modifier = Modifier.size(80.dp)
        )

        Text(
            text = "Create Account",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // First Name
        OutlinedTextField(
            value = firstName,
            onValueChange = { 
                firstName = it
                errorMessage = null
            },
            label = { Text("First Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        // Last Name
        OutlinedTextField(
            value = lastName,
            onValueChange = { 
                lastName = it
                errorMessage = null
            },
            label = { Text("Last Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = null
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errorMessage != null
        )

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                errorMessage = null
            },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errorMessage != null
        )

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                errorMessage = null
            },
            label = { Text("Confirm Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errorMessage != null
        )

        // Error message
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up button
        Button(
            onClick = {
                val validationError = validateSignUpFields(
                    firstName, lastName, email, password, confirmPassword
                )
                if (validationError == null) {
                    onSignUpSuccess()
                } else {
                    errorMessage = validationError
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign Up")
        }

        // Back to login
        TextButton(onClick = onBackToLogin) {
            Text("Already have an account? Login")
        }
    }
}

private fun validateSignUpFields(
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    confirmPassword: String
): String? {
    return when {
        firstName.isBlank() -> "First name is required"
        lastName.isBlank() -> "Last name is required"
        email.isBlank() -> "Email is required"
        !email.contains("@") -> "Invalid email format"
        password.length < 6 -> "Password must be at least 6 characters"
        password != confirmPassword -> "Passwords do not match"
        else -> null
    }
} 