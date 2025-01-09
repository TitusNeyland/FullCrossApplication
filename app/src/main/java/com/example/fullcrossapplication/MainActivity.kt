package com.example.fullcrossapplication

import android.os.Bundle
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FullCrossApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.Splash) }
                    
                    when (currentScreen) {
                        Screen.Splash -> {
                            SplashScreen {
                                currentScreen = Screen.Login
                            }
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
                            MainScreen()
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