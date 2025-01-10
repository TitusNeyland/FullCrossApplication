package com.example.fullcrossapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.fullcrossapplication.R
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

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
    
    val paymentMethods = listOf(
        PaymentMethod(
            name = "PayPal",
            icon = R.drawable.ic_paypal,
            handle = "church@fullcrossministries.org",
            description = "Send via PayPal to support our ministry",
            deepLink = "https://www.paypal.com/myaccount/transfer/homepage/buy/preview"
        ),
        PaymentMethod(
            name = "Cash App",
            icon = R.drawable.ic_cashapp,
            handle = "@FullCross",
            description = "Quick and easy donations through Cash App",
            deepLink = "https://cash.app/\$FullCross"
        ),
        PaymentMethod(
            name = "Venmo",
            icon = R.drawable.ic_venmo,
            handle = "@Titus-Neyland",
            description = "Send your donation through Venmo",
            deepLink = "venmo://paycharge?txn=pay&recipients=Titus-Neyland"
        ),
        PaymentMethod(
            name = "Zelle",
            icon = R.drawable.ic_zelle,
            handle = "donate@fullcross.org",
            description = "Direct bank transfer through Zelle"
        ),
        PaymentMethod(
            name = "Apple Pay",
            icon = R.drawable.ic_apple_pay,
            handle = "donate@fullcross.org",
            description = "Quick payment using Apple Pay"
        ),
        PaymentMethod(
            name = "Givelify",
            icon = R.drawable.ic_givlify,
            handle = "Full Cross Ministries",
            description = "Support us through Givelify platform",
            deepLink = "https://www.givelify.com/donate/MTUxNDM5OQ==/selection"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Support Our Ministry",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your generous donations help us spread the Gospel and support our community.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )

        paymentMethods.forEach { method ->
            PaymentMethodCard(method)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodCard(paymentMethod: PaymentMethod) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
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
                    }
                }
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = paymentMethod.icon),
                contentDescription = "${paymentMethod.name} icon",
                modifier = Modifier.size(32.dp),
                tint = Color.Unspecified
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paymentMethod.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = paymentMethod.handle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 