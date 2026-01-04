package works.jayesh.webby.ui.features.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.domain.model.CartItem
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.ui.components.EmptyView
import works.jayesh.webby.ui.theme.PriceGreen
import works.jayesh.webby.utils.Constants
import works.jayesh.webby.utils.toRupees

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
        onNavigateToCheckout: () -> Unit,
        onNavigateBack: () -> Unit,
        onNavigateToHome: () -> Unit,
        modifier: Modifier = Modifier
) {
    // Sample cart items - TODO: Connect to ViewModel
    var cartItems by remember {
        mutableStateOf(
                listOf(
                        CartItem(
                                1,
                                1,
                                "iPhone 15 Pro",
                                "https://via.placeholder.com/100",
                                999.99,
                                899.99,
                                1,
                                899.99
                        ),
                        CartItem(
                                2,
                                2,
                                "AirPods Pro",
                                "https://via.placeholder.com/100",
                                249.99,
                                199.99,
                                2,
                                399.98
                        ),
                        CartItem(
                                3,
                                3,
                                "Apple Watch",
                                "https://via.placeholder.com/100",
                                429.99,
                                379.99,
                                1,
                                379.99
                        )
                )
        )
    }

    val subtotal = cartItems.sumOf { it.subtotal }
    val shipping = if (subtotal > 500) 0.0 else 50.0
    val tax = subtotal * 0.18
    val total = subtotal + shipping + tax

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("My Cart (${cartItems.size})") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                )
            },
            bottomBar = {
                if (cartItems.isNotEmpty()) {
                    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            PriceRow("Subtotal", subtotal)
                            PriceRow("Shipping", shipping)
                            PriceRow("Tax (18%)", tax)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            PriceRow("Total", total, isTotal = true)

                            Spacer(modifier = Modifier.height(16.dp))

                            AppButton(
                                    text = "Proceed to Checkout (${total.toRupees()})",
                                    onClick = onNavigateToCheckout
                            )
                        }
                    }
                }
            }
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyView(
                    message = "Your cart is empty",
                    icon = Icons.Default.ShoppingCart,
                    actionText = "Continue Shopping",
                    onAction = onNavigateToHome,
                    modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                    modifier = modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                            item = item,
                            onQuantityChange = { newQty ->
                                cartItems =
                                        cartItems.map {
                                            if (it.id == item.id) {
                                                it.copy(
                                                        quantity = newQty,
                                                        subtotal = it.finalPrice * newQty
                                                )
                                            } else it
                                        }
                            },
                            onRemove = { cartItems = cartItems.filter { it.id != item.id } }
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItem, onQuantityChange: (Int) -> Unit, onRemove: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            // Product Image
            AsyncImage(
                    model = item.productImage ?: Constants.PLACEHOLDER_PRODUCT,
                    contentDescription = item.productName,
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = item.productName,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                            text = item.finalPrice.toRupees(),
                            style = MaterialTheme.typography.titleMedium,
                            color = PriceGreen
                    )
                    if (item.discountPrice != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = item.price.toRupees(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Controls and Remove
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconButton(
                                onClick = {
                                    if (item.quantity > 1) {
                                        onQuantityChange(item.quantity - 1)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                                text = "${item.quantity}",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.titleMedium
                        )

                        FilledTonalIconButton(
                                onClick = { onQuantityChange(item.quantity + 1) },
                                modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Remove Button
                    TextButton(onClick = onRemove) {
                        Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remove",
                                modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remove")
                    }
                }

                // Subtotal
                Text(
                        text = "Subtotal: ${item.subtotal.toRupees()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, amount: Double, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
                text = label,
                style =
                        if (isTotal) MaterialTheme.typography.titleMedium
                        else MaterialTheme.typography.bodyMedium
        )
        Text(
                text = amount.toRupees(),
                style =
                        if (isTotal) MaterialTheme.typography.titleLarge
                        else MaterialTheme.typography.bodyMedium,
                color = if (isTotal) PriceGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}
