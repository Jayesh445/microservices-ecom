package works.jayesh.webby.ui.features.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
        onNavigateBack: () -> Unit = {},
        onNavigateToAddressSelection: () -> Unit = {},
        onNavigateToPaymentMethod: () -> Unit = {},
        onPlaceOrder: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    var selectedAddress by remember { mutableStateOf<String?>("123 Main St, City, Country") }
    var selectedPayment by remember { mutableStateOf<String?>("Credit Card ending in 1234") }

    val cartItems = remember {
        listOf(Triple("iPhone 15 Pro", 2, 1799.98), Triple("AirPods Pro", 1, 199.99))
    }

    val subtotal = cartItems.sumOf { it.third }
    val shipping = if (subtotal > 500) 0.0 else 50.0
    val tax = subtotal * 0.18
    val total = subtotal + shipping + tax

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Checkout") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            },
            bottomBar = {
                Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                    text = "Total Amount",
                                    style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                    text = "$${"%.2f".format(total)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        AppButton(
                                text = "Place Order",
                                onClick = onPlaceOrder,
                                enabled = selectedAddress != null && selectedPayment != null
                        )
                    }
                }
            }
    ) { padding ->
        LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order Items
            item {
                Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(cartItems) { (name, quantity, price) -> CheckoutItemCard(name, quantity, price) }

            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Delivery Address
            item {
                Text(
                        text = "Delivery Address",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card(onClick = onNavigateToAddressSelection, modifier = Modifier.fillMaxWidth()) {
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                    text = selectedAddress ?: "Select delivery address",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color =
                                            if (selectedAddress != null)
                                                    MaterialTheme.colorScheme.onSurface
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (selectedAddress != null) {
                                Text(
                                        text = "John Doe • +1234567890",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Payment Method
            item {
                Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card(onClick = onNavigateToPaymentMethod, modifier = Modifier.fillMaxWidth()) {
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                                Icons.Default.Payment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = selectedPayment ?: "Select payment method",
                                style = MaterialTheme.typography.bodyLarge,
                                color =
                                        if (selectedPayment != null)
                                                MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Price Breakdown
            item {
                Text(
                        text = "Price Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PriceRow("Subtotal", subtotal)
                        PriceRow("Shipping", shipping, showFree = shipping == 0.0)
                        PriceRow("Tax (18%)", tax)
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
                                    text = "$${"%.2f".format(total)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutItemCard(name: String, quantity: Int, price: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                    model = Constants.PLACEHOLDER_PRODUCT,
                    contentDescription = name,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                        text = "Qty: $quantity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                    text = "$${"%.2f".format(price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PriceRow(label: String, amount: Double, showFree: Boolean = false) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (showFree) {
            Text(
                    text = "FREE",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4CAF50)
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
