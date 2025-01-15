package com.example.fullcrossapplication.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fullcrossapplication.R

data class PaymentMethod(
    val name: String,
    val icon: Int,
    val handle: String,
    val description: String = "",
    val deepLink: String? = null
)

@Composable
fun DonateScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    val paymentMethods = listOf(
        PaymentMethod(
            name = "PayPal",
            icon = R.drawable.ic_paypal,
            handle = "church@fullcrossministries.org",
            description = "Support our ministry securely through PayPal",
            deepLink = "https://www.paypal.com/myaccount/transfer/homepage/buy/preview"
        ),
        PaymentMethod(
            name = "Cash App",
            icon = R.drawable.ic_cashapp,
            handle = "@FullCross",
            description = "Quick and easy donations with Cash App",
            deepLink = "https://cash.app/\$FullCross"
        ),
        PaymentMethod(
            name = "Venmo",
            icon = R.drawable.ic_venmo,
            handle = "@Titus-Neyland",
            description = "Send your gift through Venmo",
            deepLink = "venmo://paycharge?txn=pay&recipients=Titus-Neyland"
        ),
        PaymentMethod(
            name = "Zelle",
            icon = R.drawable.ic_zelle,
            handle = "donate@fullcross.org",
            description = "Direct bank transfer using Zelle"
        ),
        PaymentMethod(
            name = "Apple Pay",
            icon = R.drawable.ic_apple_pay,
            handle = "donate@fullcross.org",
            description = "Quick and secure Apple Pay donation"
        ),
        PaymentMethod(
            name = "Givelify",
            icon = R.drawable.ic_givlify,
            handle = "Full Cross Ministries",
            description = "Support us through the Givelify platform",
            deepLink = "https://www.givelify.com/donate/MTUxNDM5OQ==/selection"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Support Our Ministry",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Your generous donations help us spread the Gospel and support our community. Every gift makes a difference.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        // Payment Methods Section - Moved up
        Text(
            text = "Ways to Give",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        paymentMethods.forEach { method ->
            PaymentMethodCard(
                paymentMethod = method,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .animateContentSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scripture Quote Card - Moved down
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"For God loves a cheerful giver.\"",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "2 Corinthians 9:7",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Bottom spacing
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 4f else 1f,
        label = "elevation"
    )

    Card(
        modifier = modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> isPressed = true
                            PointerEventType.Release -> isPressed = false
                        }
                    }
                }
            },
        onClick = {
            if (!isLoading) {
                isLoading = true
                when (paymentMethod.name) {
                    "Cash App" -> if (paymentMethod.deepLink != null) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentMethod.deepLink))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Please install Cash App to proceed with payment",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                    "PayPal" -> {
                        try {
                            val uri = Uri.parse("paypal://").buildUpon()
                                .appendPath("send")
                                .appendPath("church@fullcrossministries.org")
                                .build()
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            
                            // Try to start PayPal app first
                            try {
                                intent.setPackage("com.paypal.android.p2pmobile")
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // If PayPal app is not installed, open in browser
                                val webIntent = Intent(Intent.ACTION_VIEW, 
                                    Uri.parse("https://paypal.me/fullcrossministries"))
                                context.startActivity(webIntent)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Unable to open PayPal",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                    "Venmo" -> {
                        try {
                            // Try to open Venmo app first
                            val uri = Uri.parse("venmo://paycharge?txn=pay&recipients=Titus-Neyland")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.venmo")
                            
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // If Venmo app is not installed, open in browser
                                val webIntent = Intent(Intent.ACTION_VIEW, 
                                    Uri.parse("https://venmo.com/Titus-Neyland"))
                                context.startActivity(webIntent)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Unable to open Venmo",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                    "Givelify" -> {
                        try {
                            // Try to open Givelify app first
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentMethod.deepLink))
                            intent.setPackage("com.givelify.android")
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // If Givelify app is not installed, open in browser
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentMethod.deepLink))
                                context.startActivity(webIntent)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Unable to open Givelify",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            }
        },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Payment method icon with background
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = paymentMethod.icon),
                    contentDescription = "${paymentMethod.name} icon",
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize(),
                    tint = Color.Unspecified
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paymentMethod.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = paymentMethod.handle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (paymentMethod.description.isNotEmpty()) {
                    Text(
                        text = paymentMethod.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 8.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Select payment method",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
} 