package com.example.fullcrossapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fullcrossapplication.ui.theme.FullCrossApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fullcrossapplication.viewmodels.AuthViewModel
import com.example.fullcrossapplication.viewmodels.ThemeViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verify Firebase initialization
        try {
            FirebaseApp.initializeApp(this)
            Log.d("Firebase", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("Firebase", "Firebase initialization failed", e)
        }
        
        setContent {
            val authViewModel = viewModel<AuthViewModel>()
            val themeViewModel = viewModel<ThemeViewModel>()
            val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
            val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
            
            FullCrossApplicationTheme(
                darkTheme = isDarkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.Splash) }
                    
                    when (currentScreen) {
                        Screen.Splash -> {
                            SplashScreen(
                                onSplashScreenFinish = {
                                    currentScreen = if (currentUser != null) {
                                        Screen.Main
                                    } else {
                                        Screen.Login
                                    }
                                }
                            )
                        }
                        Screen.Login -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    currentScreen = Screen.Main
                                },
                                onSignUpClick = {
                                    currentScreen = Screen.SignUp
                                }
                            )
                        }
                        Screen.SignUp -> {
                            SignUpScreen(
                                onSignUpSuccess = {
                                    currentScreen = Screen.Main
                                },
                                onBackToLogin = {
                                    currentScreen = Screen.Login
                                }
                            )
                        }
                        Screen.Main -> {
                            MainScreen(
                                onSignOut = {
                                    authViewModel.signOut()
                                    currentScreen = Screen.Login
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Screen {
    Splash,
    Login,
    SignUp,
    Main
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FullCrossApplicationTheme {
        Greeting("Android")
    }
}