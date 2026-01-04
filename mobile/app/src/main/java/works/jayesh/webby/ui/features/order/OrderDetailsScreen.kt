package works.jayesh.webby.ui.features.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
        orderId: Long = 1,
        onNavigateBack: () -> Unit = {},
        onTrackOrder: () -> Unit = {},
        onDownloadInvoice: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    val orderItems = remember {
        listOf(Triple("iPhone 15 Pro", 2, 1799.98), Triple("AirPods Pro", 1, 199.99))
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Order Details") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = onDownloadInvoice) {
                                Icon(Icons.Default.Download, "Download Invoice")
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
            // Order Status
            item {
                Card(
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
                                        text = "Order #ORD-20240104-001",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                        text = "Placed on Jan 4, 2026",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                        text = "Shipped",
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 6.dp
                                                ),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AppButton(
                                text = "Track Order",
                                onClick = onTrackOrder,
                                icon = Icons.Default.LocationOn
                        )
                    }
                }
            }

            // Delivery Address
            item {
                Text(
                        text = "Delivery Address",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                        text = "John Doe",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                        text = "+1234567890",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text =
                                                "123 Main St, Apartment 4B, New York, NY, USA - 10001",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Order Items
            item {
                Text(
                        text = "Items (${orderItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(orderItems) { (name, quantity, price) -> OrderItemCard(name, quantity, price) }

            // Price Details
            item {
                Text(
                        text = "Price Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PriceDetailRow("Subtotal", 1999.97)
                        PriceDetailRow("Shipping", 0.0, isFree = true)
                        PriceDetailRow("Tax", 359.99)
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                    text = "Total",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                    text = "$2,359.96",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Payment Info
            item {
                Text(
                        text = "Payment Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card {
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                                Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                    text = "Credit Card",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                    text = "•••• •••• •••• 1234",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Help Section
            item {
                Card(
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                ) {
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                                Icons.Default.Help,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                    text = "Need Help?",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                    text = "Contact customer support",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(name: String, quantity: Int, price: Double) {
    Card {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                    model = Constants.PLACEHOLDER_PRODUCT,
                    contentDescription = name,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = "Quantity: $quantity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = "$${"%.2f".format(price)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PriceDetailRow(label: String, amount: Double, isFree: Boolean = false) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isFree) {
            Text(
                    text = "FREE",
                    style = MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.ui.graphics.Color(0xFF4CAF50)
            )
        } else {
            Text(
                    text = "$${"%.2f".format(amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
