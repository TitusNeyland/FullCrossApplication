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

data class PaymentMethod(
    val name: String,
    val icon: Int,
    val handle: String,
    val description: String = ""
)

@Composable
fun DonateScreen() {
    val paymentMethods = listOf(
        PaymentMethod(
            name = "PayPal",
            icon = R.drawable.ic_paypal,
            handle = "@fullcrossministries",
            description = "Send via PayPal to support our ministry"
        ),
        PaymentMethod(
            name = "Cash App",
            icon = R.drawable.ic_cashapp,
            handle = "@fullcrossmin",
            description = "Quick and easy donations through Cash App"
        ),
        PaymentMethod(
            name = "Venmo",
            icon = R.drawable.ic_venmo,
            handle = "@fullcross-ministries",
            description = "Send your donation through Venmo"
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
            name = "Givlify",
            icon = R.drawable.ic_givlify,
            handle = "fullcrossministries",
            description = "Support us through Givlify platform"
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Handle payment method click */ }
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