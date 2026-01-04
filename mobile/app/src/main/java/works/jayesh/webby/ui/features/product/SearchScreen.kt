package works.jayesh.webby.ui.features.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import works.jayesh.webby.domain.model.Product
import works.jayesh.webby.ui.components.ProductCard
import works.jayesh.webby.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
        onNavigateBack: () -> Unit = {},
        onNavigateToProduct: (Long) -> Unit = {},
        modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Electronics", "Fashion", "Home", "Beauty", "Sports")

    // Sample search results
    val searchResults = remember {
        List(10) { index ->
            Product(
                    id = index.toLong(),
                    name = "Product ${index + 1}",
                    sku = "SKU$index",
                    description = "Sample description",
                    shortDescription = "Short desc",
                    price = 99.99 + (index * 10),
                    discountPrice = if (index % 2 == 0) 89.99 + (index * 10) else null,
                    stockQuantity = 100,
                    slug = "product-$index",
                    categoryId = 1L,
                    categoryName = filters[index % filters.size],
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
                        title = { Text("Search") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            }
    ) { padding ->
        Column(modifier = modifier.fillMaxSize().padding(padding)) {
            // Search Bar
            OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Search products...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    },
                    singleLine = true
            )

            // Filter Chips
            ScrollableTabRow(
                    selectedTabIndex = filters.indexOf(selectedFilter),
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 16.dp
            ) {
                filters.forEach { filter ->
                    FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sort and Filter Row
            Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { /* Sort */}) {
                    Icon(Icons.Default.Sort, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sort")
                }
                TextButton(onClick = { /* Filter */}) {
                    Icon(Icons.Default.FilterList, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Filter")
                }
            }

            HorizontalDivider()

            // Results Count
            Text(
                    text = "${searchResults.size} products found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
            )

            // Search Results Grid
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(searchResults.chunked(2)) { rowProducts ->
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowProducts.forEach { product ->
                            ProductCard(
                                    product = product,
                                    onClick = { onNavigateToProduct(product.id) },
                                    onAddToCart = { /* Add to cart */},
                                    modifier = Modifier.weight(1f),
                                    onToggleFavorite = {/* Add to favorites */},
                                    isFavorite = false
                            )
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
