package works.jayesh.webby.ui.features.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.domain.model.Order
import works.jayesh.webby.domain.model.OrderItem
import works.jayesh.webby.domain.model.OrderStatus
import works.jayesh.webby.ui.components.EmptyView
import works.jayesh.webby.ui.theme.*
import works.jayesh.webby.utils.Constants
import works.jayesh.webby.utils.toFormattedDate
import works.jayesh.webby.utils.toRupees

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(onNavigateToOrderDetails: (Long) -> Unit, modifier: Modifier = Modifier) {
    // Sample orders - TODO: Connect to ViewModel
    val orders = remember {
        listOf(
                Order(
                        id = 1,
                        orderNumber = "ORD-2024-001",
                        userId = 1,
                        userName = "John Doe",
                        items =
                                listOf(
                                        OrderItem(
                                                1,
                                                1,
                                                "iPhone 15 Pro",
                                                "https://via.placeholder.com/100",
                                                899.99,
                                                1,
                                                899.99
                                        )
                                ),
                        totalAmount = 899.99,
                        discountAmount = 0.0,
                        shippingAmount = 0.0,
                        taxAmount = 161.99,
                        finalAmount = 1061.98,
                        status = OrderStatus.DELIVERED,
                        paymentStatus = "PAID",
                        shippingAddressId = 1,
                        shippingAddress = null,
                        createdAt = "2024-01-15T10:30:00",
                        updatedAt = "2024-01-20T14:45:00"
                ),
                Order(
                        id = 2,
                        orderNumber = "ORD-2024-002",
                        userId = 1,
                        userName = "John Doe",
                        items =
                                listOf(
                                        OrderItem(
                                                2,
                                                2,
                                                "AirPods Pro",
                                                "https://via.placeholder.com/100",
                                                199.99,
                                                2,
                                                399.98
                                        )
                                ),
                        totalAmount = 399.98,
                        discountAmount = 20.0,
                        shippingAmount = 0.0,
                        taxAmount = 71.99,
                        finalAmount = 451.97,
                        status = OrderStatus.SHIPPED,
                        paymentStatus = "PAID",
                        shippingAddressId = 1,
                        shippingAddress = null,
                        createdAt = "2024-01-25T15:20:00",
                        updatedAt = "2024-01-28T09:10:00"
                ),
                Order(
                        id = 3,
                        orderNumber = "ORD-2024-003",
                        userId = 1,
                        userName = "John Doe",
                        items =
                                listOf(
                                        OrderItem(
                                                3,
                                                3,
                                                "Apple Watch Series 9",
                                                "https://via.placeholder.com/100",
                                                379.99,
                                                1,
                                                379.99
                                        )
                                ),
                        totalAmount = 379.99,
                        discountAmount = 0.0,
                        shippingAmount = 50.0,
                        taxAmount = 68.39,
                        finalAmount = 498.38,
                        status = OrderStatus.PROCESSING,
                        paymentStatus = "PAID",
                        shippingAddressId = 1,
                        shippingAddress = null,
                        createdAt = "2024-02-01T11:45:00",
                        updatedAt = "2024-02-01T11:45:00"
                )
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("My Orders") }) }) { padding ->
        if (orders.isEmpty()) {
            EmptyView(
                    message = "No orders yet",
                    icon = Icons.Default.Receipt,
                    actionText = "Start Shopping",
                    onAction = { /* TODO: Navigate to home */},
                    modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                    modifier = modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order = order, onClick = { onNavigateToOrderDetails(order.id) })
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Order Header
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = order.orderNumber, style = MaterialTheme.typography.titleMedium)
                    Text(
                            text = order.createdAt.toFormattedDate(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OrderStatusChip(status = order.status)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Order Items Preview
            order.items.take(2).forEach { item ->
                Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                            model = item.productImage ?: Constants.PLACEHOLDER_PRODUCT,
                            contentDescription = item.productName,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = item.productName,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                        )
                        Text(
                                text = "Qty: ${item.quantity}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(text = item.price.toRupees(), style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (order.items.size > 2) {
                Text(
                        text = "+${order.items.size - 2} more items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Order Total
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                            text = "Total Amount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                            text = order.finalAmount.toRupees(),
                            style = MaterialTheme.typography.titleLarge,
                            color = PriceGreen
                    )
                }

                OutlinedButton(onClick = onClick, modifier = Modifier.height(40.dp)) {
                    Text("View Details")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderStatusChip(status: OrderStatus) {
    val (color, text) =
            when (status) {
                OrderStatus.PENDING -> StatusPending to "Pending"
                OrderStatus.CONFIRMED -> StatusConfirmed to "Confirmed"
                OrderStatus.PROCESSING -> StatusProcessing to "Processing"
                OrderStatus.SHIPPED -> StatusShipped to "Shipped"
                OrderStatus.DELIVERED -> StatusDelivered to "Delivered"
                OrderStatus.CANCELLED -> StatusCancelled to "Cancelled"
            }

    Surface(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
        Text(
                text = text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = color
        )
    }
}
