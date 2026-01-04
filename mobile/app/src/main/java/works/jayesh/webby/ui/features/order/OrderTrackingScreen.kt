package works.jayesh.webby.ui.features.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class TrackingStatus(
        val status: String,
        val description: String,
        val timestamp: String,
        val location: String?,
        val isCompleted: Boolean,
        val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
        orderId: String = "ORD-2024-001",
        onNavigateBack: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    val trackingStatuses = remember {
        listOf(
                TrackingStatus(
                        "Order Placed",
                        "Your order has been placed successfully",
                        "Jan 15, 2024 - 10:30 AM",
                        null,
                        true,
                        Icons.Default.CheckCircle
                ),
                TrackingStatus(
                        "Payment Confirmed",
                        "Payment received and verified",
                        "Jan 15, 2024 - 10:32 AM",
                        null,
                        true,
                        Icons.Default.Payment
                ),
                TrackingStatus(
                        "Order Processed",
                        "Your order is being prepared for shipment",
                        "Jan 15, 2024 - 2:15 PM",
                        "Warehouse - New York",
                        true,
                        Icons.Default.Inventory
                ),
                TrackingStatus(
                        "Shipped",
                        "Package has been shipped",
                        "Jan 16, 2024 - 9:00 AM",
                        "Distribution Center - Boston",
                        true,
                        Icons.Default.LocalShipping
                ),
                TrackingStatus(
                        "Out for Delivery",
                        "Your package is out for delivery",
                        "Jan 17, 2024 - 8:30 AM",
                        "Local Hub - Cambridge",
                        true,
                        Icons.Default.DeliveryDining
                ),
                TrackingStatus(
                        "Delivered",
                        "Package delivered successfully",
                        "Expected by Jan 17, 2024 - 6:00 PM",
                        null,
                        false,
                        Icons.Default.Home
                )
        )
    }

    val estimatedDelivery = "Today, 6:00 PM"
    val trackingNumber = "TRK1234567890"
    val courier = "FedEx Express"

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Track Order") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Share tracking */}) {
                                Icon(Icons.Default.Share, "Share")
                            }
                        }
                )
            }
    ) { padding ->
        LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order Info Card
            item {
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                        text = "Order ID",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                        text = orderId,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            AssistChip(
                                    onClick = {},
                                    label = { Text("Out for Delivery") },
                                    leadingIcon = {
                                        Icon(
                                                Icons.Default.LocalShipping,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                        )
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                        text = "Estimated Delivery",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                        text = estimatedDelivery,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                        text = "Tracking Number",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                        text = trackingNumber,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            IconButton(onClick = { /* Copy tracking number */}) {
                                Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                    text = "Courier: ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                    text = courier,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Timeline Title
            item {
                Text(
                        text = "Tracking Timeline",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                )
            }

            // Tracking Timeline
            items(trackingStatuses) { status ->
                TrackingStatusItem(status = status, isLast = status == trackingStatuses.last())
            }

            // Help Card
            item {
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                MaterialTheme.colorScheme.secondaryContainer
                                )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                    Icons.Default.Help,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                    text = "Need Help?",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text =
                                        "If you have any questions about your order or delivery, please contact our customer support.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                    onClick = { /* Contact support */},
                                    modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call")
                            }
                            OutlinedButton(
                                    onClick = { /* Open chat */},
                                    modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackingStatusItem(status: TrackingStatus, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(40.dp)
        ) {
            Box(
                    modifier =
                            Modifier.size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                            if (status.isCompleted)
                                                    MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        status.icon,
                        contentDescription = null,
                        tint =
                                if (status.isCompleted) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                )
            }

            if (!isLast) {
                Box(
                        modifier =
                                Modifier.width(2.dp)
                                        .height(60.dp)
                                        .background(
                                                if (status.isCompleted)
                                                        MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surfaceVariant
                                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Status details
        Column(modifier = Modifier.weight(1f).padding(bottom = if (!isLast) 16.dp else 0.dp)) {
            Text(
                    text = status.status,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color =
                            if (status.isCompleted) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                    text = status.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                    text = status.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            status.location?.let { location ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
