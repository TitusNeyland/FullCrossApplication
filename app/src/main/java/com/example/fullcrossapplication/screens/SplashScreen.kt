package com.example.fullcrossapplication.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fullcrossapplication.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashScreenFinish: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "Alpha Animation"
    )
    
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseOutBack
        ),
        label = "Scale Animation"
    )

    // Floating effect animation
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Transition")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Float Animation"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        onSplashScreenFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 64.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .offset(y = offsetY.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_christian_cross),
                contentDescription = "Christian Cross",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )
            
            Text(
                text = stringResource(id = R.string.ministry_name),
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp,
                lineHeight = 38.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
            )
            
            Text(
                text = "Serving God by Serving Others",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}