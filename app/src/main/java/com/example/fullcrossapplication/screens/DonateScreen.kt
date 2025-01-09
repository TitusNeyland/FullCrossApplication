package com.example.fullcrossapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fullcrossapplication.R

data class PaymentMethod(
    val name: String,
    val icon: Int,
    val handle: String
)

@Composable
fun DonateScreen() {
    val paymentMethods = listOf(
        PaymentMethod("PayPal", R.drawable.ic_paypal, "@fullcrossministries"),
        PaymentMethod("Cash App", R.drawable.ic_cashapp, "fullcrossmin"),
        // Add more payment methods as needed
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Support Our Ministry",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Your generous donations help us spread the word of God and support our community. " +
                   "Choose your preferred payment method below:",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Payment Methods
        paymentMethods.forEach { method ->
            PaymentMethodCard(method)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Additional Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Other Ways to Give",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "For other donation methods or any questions about giving, " +
                           "please contact us at donations@fullcrossministries.org",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodCard(method: PaymentMethod) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Handle payment method click */ }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = method.icon),
                    contentDescription = "${method.name} logo",
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = method.name,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = method.handle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_christian_cross),
                contentDescription = "Donate",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
} 