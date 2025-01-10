package com.example.fullcrossapplication.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fullcrossapplication.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LiveStream(
    val title: String,
    val thumbnailUrl: String,
    val startTime: LocalDateTime,
    val durationMinutes: Long,
    val viewerCount: Int,
    val isLive: Boolean = false,
    val facebookUrl: String = "https://www.facebook.com/profile.php?id=61555182075913" // Default Facebook URL
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchScreen() {
    val viewerCount = 128 // Example viewer count
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
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
        // Enhanced Header
        LargeTopAppBar(
            title = {
                Column {
                    Text(
                        "Watch Now",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        "View the latest live broadcast here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Featured Live Stream
            item {
                FeaturedStreamCard(
                    stream = LiveStream(
                        title = "Sunday Morning Service",
                        thumbnailUrl = "https://example.com/thumbnail.jpg",
                        startTime = LocalDateTime.now(),
                        durationMinutes = 60,
                        viewerCount = viewerCount,
                        isLive = true,
                        facebookUrl = "https://www.facebook.com/100079371798055/videos/655144869785669"
                    )
                )
            }

            // Upcoming Streams Section
            item {
                Text(
                    "Upcoming Services",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Example upcoming streams
            items(getUpcomingStreams()) { stream ->
                UpcomingStreamCard(stream = stream)
            }
        }
    }
}

@Composable
private fun FeaturedStreamCard(stream: LiveStream) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Placeholder/Loading state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            // Actual image
            AsyncImage(
                model = stream.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_christian_cross) // Fallback image
            )

            // Live indicator
            if (stream.isLive) {
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.error
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "LIVE",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stream.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Viewers",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${stream.viewerCount} watching",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Duration",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${stream.durationMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(stream.facebookUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Unable to open Facebook",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Join Stream")
            }
        }
    }
}

@Composable
private fun UpcomingStreamCard(stream: LiveStream) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(72.dp)
            ) {
                Text(
                    text = stream.startTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stream.startTime.format(DateTimeFormatter.ofPattern("EEE")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .height(40.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Content column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stream.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${stream.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { /* Handle reminder */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Set reminder",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun getUpcomingStreams(): List<LiveStream> {
    val now = LocalDateTime.now()
    
    // Get next Wednesday at 6 PM
    val nextWednesday = now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.WEDNESDAY))
        .withHour(18)  // 6 PM
        .withMinute(0)
        .withSecond(0)

    // Get next Sunday at 9 AM
    val nextSunday = now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
        .withHour(9)   // 9 AM
        .withMinute(0)
        .withSecond(0)

    return listOf(
        LiveStream(
            title = "Wednesday Bible Study",
            thumbnailUrl = "https://example.com/thumbnail1.jpg",
            startTime = nextWednesday,
            durationMinutes = 50,
            viewerCount = 0,
            facebookUrl = "https://www.facebook.com/100079371798055/videos/655144869785669"
        ),
        LiveStream(
            title = "Sunday Morning Service",
            thumbnailUrl = "https://example.com/thumbnail2.jpg",
            startTime = nextSunday,
            durationMinutes = 50,
            viewerCount = 0,
            facebookUrl = "https://www.facebook.com/100079371798055/videos/655144869785669"
        )
    ).sortedBy { it.startTime }
}

private fun formatDuration(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60)
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
} 