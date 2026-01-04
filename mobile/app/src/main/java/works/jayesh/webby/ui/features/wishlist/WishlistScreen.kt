package works.jayesh.webby.ui.features.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import works.jayesh.webby.domain.model.Product
import works.jayesh.webby.ui.components.ProductCard
import works.jayesh.webby.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
        onNavigateBack: () -> Unit = {},
        onNavigateToProduct: (Long) -> Unit = {},
        modifier: Modifier = Modifier
) {
    val wishlistItems = remember {
        List(8) { index ->
            Product(
                    id = index.toLong(),
                    name = "Wishlist Product ${index + 1}",
                    sku = "SKU$index",
                    description = "Sample description",
                    shortDescription = "Short desc",
                    price = 99.99 + (index * 10),
                    discountPrice = if (index % 2 == 0) 89.99 + (index * 10) else null,
                    stockQuantity = 100,
                    slug = "product-$index",
                    categoryId = 1L,
                    categoryName = "Category",
                    sellerId = 1L,
                    sellerName = "Seller",
                    images = listOf(Constants.PLACEHOLDER_PRODUCT),
                    brand = "Brand",
                    manufacturer = null,
                    weight = null,
                    active = true,
                    featured = false,
                    status = "active",
                    averageRating = 4.0 + (index % 10) * 0.1,
                    totalReviews = 100 + index,
                    totalSold = null,
                    tags = emptyList(),
                    createdAt = null,
                    updatedAt = null
            )
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("My Wishlist") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            }
    ) { padding ->
        if (wishlistItems.isEmpty()) {
            Box(
                    modifier = modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                            text = "Your wishlist is empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = "Add items you love to your wishlist",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(modifier = modifier.fillMaxSize().padding(padding)) {
                // Wishlist count
                Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                            text = "${wishlistItems.size} items in wishlist",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(wishlistItems) { product ->
                        ProductCard(
                                product = product,
                                onClick = { onNavigateToProduct(product.id) },
                                onAddToCart = { /* Add to cart */},
                                isFavorite = true,
                                onToggleFavorite = { /* Remove from wishlist */}
                        )
                    }
                }
            }
        }
    }
}
