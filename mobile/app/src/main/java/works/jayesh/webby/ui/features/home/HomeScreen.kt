package works.jayesh.webby.ui.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import works.jayesh.webby.domain.model.Product
import works.jayesh.webby.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
        onNavigateToProduct: (Long) -> Unit,
        onNavigateToCart: () -> Unit,
        onNavigateToSearch: () -> Unit,
        modifier: Modifier = Modifier
) {
    // Sample products - TODO: Connect to ViewModel
    val featuredProducts = remember {
        listOf(
                createSampleProduct(1, "iPhone 15 Pro", 999.99, 899.99),
                createSampleProduct(2, "Samsung Galaxy S24", 899.99, 799.99),
                createSampleProduct(3, "MacBook Pro M3", 1999.99, 1799.99),
                createSampleProduct(4, "Sony WH-1000XM5", 399.99, 349.99)
        )
    }

    val bestSellers = remember {
        listOf(
                createSampleProduct(5, "AirPods Pro", 249.99, 199.99),
                createSampleProduct(6, "iPad Air", 599.99, 549.99),
                createSampleProduct(7, "Apple Watch Series 9", 429.99, 379.99),
                createSampleProduct(8, "Kindle Paperwhite", 139.99, 119.99)
        )
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    "Webby",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.primary
                            )
                        },
                        actions = {
                            IconButton(onClick = onNavigateToSearch) {
                                Icon(Icons.Default.Search, "Search")
                            }
                            IconButton(onClick = onNavigateToCart) {
                                BadgedBox(
                                        badge = {
                                            Badge { Text("3") } // TODO: Connect to cart state
                                        }
                                ) { Icon(Icons.Default.ShoppingCart, "Cart") }
                            }
                            IconButton(onClick = { /* TODO: Notifications */}) {
                                BadgedBox(badge = { Badge { Text("5") } }) {
                                    Icon(Icons.Default.Notifications, "Notifications")
                                }
                            }
                        }
                )
            }
    ) { padding ->
        LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Search Bar
            item {
                AppSearchBar(
                        query = "",
                        onQueryChange = {},
                        onSearch = onNavigateToSearch,
                        placeholder = "Search products...",
                        modifier = Modifier.padding(16.dp),
                        enabled = false
                )
            }

            // Category Chips
            item {
                CategoryChips(onCategoryClick = { /* TODO */})
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Banner/Promo Section
            item {
                PromoBanner(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Featured Products
            item {
                ProductSection(
                        title = "Featured Products",
                        products = featuredProducts,
                        onProductClick = onNavigateToProduct,
                        onAddToCart = { /* TODO */},
                        onSeeAll = { /* TODO */}
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Best Sellers
            item {
                ProductSection(
                        title = "Best Sellers",
                        products = bestSellers,
                        onProductClick = onNavigateToProduct,
                        onAddToCart = { /* TODO */},
                        onSeeAll = { /* TODO */}
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Deals Section
            item {
                ProductSection(
                        title = "Today's Deals",
                        products = featuredProducts.shuffled(),
                        onProductClick = onNavigateToProduct,
                        onAddToCart = { /* TODO */},
                        onSeeAll = { /* TODO */}
                )
            }
        }
    }
}

@Composable
private fun CategoryChips(onCategoryClick: (String) -> Unit) {
    val categories = listOf("Electronics", "Fashion", "Home", "Beauty", "Sports", "Books")

    LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                    selected = false,
                    onClick = { onCategoryClick(category) },
                    label = { Text(category) }
            )
        }
    }
}

@Composable
private fun PromoBanner(modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth().height(160.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        "Special Offer!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        "Up to 50% OFF on Electronics",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* TODO */}) { Text("Shop Now") }
            }
        }
    }
}

@Composable
private fun ProductSection(
        title: String,
        products: List<Product>,
        onProductClick: (Long) -> Unit,
        onAddToCart: (Long) -> Unit,
        onSeeAll: () -> Unit
) {
    Column {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onSeeAll) {
                Text("See All")
                Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onAddToCart = { onAddToCart(product.id) },
                        isFavorite = false,
                        onToggleFavorite = { /* to implement in future */}
                )
            }
        }
    }
}

// Helper function to create sample products
private fun createSampleProduct(
        id: Long,
        name: String,
        price: Double,
        discountPrice: Double? = null
): Product {
    return Product(
            id = id,
            name = name,
            sku = "SKU$id",
            description = "This is a great product that you'll love!",
            shortDescription = "High quality product",
            price = price,
            discountPrice = discountPrice,
            stockQuantity = 50,
            slug = name.lowercase().replace(" ", "-"),
            categoryId = 1,
            categoryName = "Electronics",
            sellerId = 1,
            sellerName = "Webby Store",
            images = listOf("https://via.placeholder.com/400"),
            brand = "Brand",
            manufacturer = "Manufacturer",
            weight = 1.0,
            active = true,
            featured = true,
            status = "AVAILABLE",
            averageRating = 4.5,
            totalReviews = 150,
            totalSold = 500,
            tags = listOf("electronics", "popular"),
            createdAt = null,
            updatedAt = null
    )
}
