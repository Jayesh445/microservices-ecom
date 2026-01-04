package works.jayesh.webby.ui.features.product

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
fun ProductListScreen(
        categoryId: Long = 1,
        categoryName: String = "Smartphones",
        onNavigateBack: () -> Unit = {},
        onProductClick: (Long) -> Unit = {},
        onToggleFavorite: (Long) -> Unit = {},
        modifier: Modifier = Modifier
) {
        var sortBy by remember { mutableStateOf("Popular") }
        var showSortMenu by remember { mutableStateOf(false) }
        var viewType by remember { mutableStateOf("Grid") } // Grid or List

        val products = remember {
                listOf(
                        Product(
                                id = 1,
                                name = "iPhone 15 Pro",
                                sku = "SKU001",
                                description = "Latest iPhone with pro features",
                                shortDescription = "iPhone 15 Pro",
                                price = 999.99,
                                discountPrice = 1099.99,
                                stockQuantity = 50,
                                slug = "iphone-15-pro",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "Apple Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "Apple",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = true,
                                status = "active",
                                averageRating = 4.5,
                                totalReviews = 150,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 2,
                                name = "Samsung Galaxy S24",
                                sku = "SKU002",
                                description = "Latest Samsung flagship",
                                shortDescription = "Galaxy S24",
                                price = 899.99,
                                discountPrice = null,
                                stockQuantity = 40,
                                slug = "samsung-galaxy-s24",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "Samsung Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "Samsung",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = true,
                                status = "active",
                                averageRating = 4.3,
                                totalReviews = 120,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 3,
                                name = "Google Pixel 8",
                                sku = "SKU003",
                                description = "Google's latest Pixel phone",
                                shortDescription = "Pixel 8",
                                price = 699.99,
                                discountPrice = 799.99,
                                stockQuantity = 30,
                                slug = "google-pixel-8",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "Google Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "Google",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = false,
                                status = "active",
                                averageRating = 4.4,
                                totalReviews = 100,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 4,
                                name = "OnePlus 12",
                                sku = "SKU004",
                                description = "Flagship killer phone",
                                shortDescription = "OnePlus 12",
                                price = 799.99,
                                discountPrice = null,
                                stockQuantity = 25,
                                slug = "oneplus-12",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "OnePlus Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "OnePlus",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = false,
                                status = "active",
                                averageRating = 4.2,
                                totalReviews = 90,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 5,
                                name = "iPhone 15",
                                sku = "SKU005",
                                description = "Standard iPhone 15",
                                shortDescription = "iPhone 15",
                                price = 799.99,
                                discountPrice = 899.99,
                                stockQuantity = 60,
                                slug = "iphone-15",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "Apple Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "Apple",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = true,
                                status = "active",
                                averageRating = 4.6,
                                totalReviews = 200,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 6,
                                name = "Samsung Galaxy A54",
                                sku = "SKU006",
                                description = "Mid-range Samsung phone",
                                shortDescription = "Galaxy A54",
                                price = 449.99,
                                discountPrice = null,
                                stockQuantity = 80,
                                slug = "samsung-galaxy-a54",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "Samsung Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "Samsung",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = false,
                                status = "active",
                                averageRating = 4.0,
                                totalReviews = 75,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 7,
                                name = "Xiaomi 13 Pro",
                                sku = "SKU007",
                                description = "Xiaomi flagship phone",
                                shortDescription = "Xiaomi 13 Pro",
                                price = 899.99,
                                discountPrice = 999.99,
                                stockQuantity = 35,
                                slug = "xiaomi-13-pro",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "Xiaomi Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "Xiaomi",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = false,
                                status = "active",
                                averageRating = 4.3,
                                totalReviews = 85,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 8,
                                name = "Nothing Phone 2",
                                sku = "SKU008",
                                description = "Unique design phone",
                                shortDescription = "Nothing Phone 2",
                                price = 599.99,
                                discountPrice = null,
                                stockQuantity = 45,
                                slug = "nothing-phone-2",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "Nothing Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "Nothing",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = true,
                                status = "active",
                                averageRating = 4.1,
                                totalReviews = 95,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 9,
                                name = "OPPO Find X6",
                                sku = "SKU009",
                                description = "OPPO flagship phone",
                                shortDescription = "Find X6",
                                price = 749.99,
                                discountPrice = null,
                                stockQuantity = 30,
                                slug = "oppo-find-x6",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "OPPO Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "OPPO",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = false,
                                status = "active",
                                averageRating = 4.2,
                                totalReviews = 70,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        ),
                        Product(
                                id = 10,
                                name = "Vivo X90",
                                sku = "SKU010",
                                description = "Vivo camera phone",
                                shortDescription = "Vivo X90",
                                price = 799.99,
                                discountPrice = 899.99,
                                stockQuantity = 40,
                                slug = "vivo-x90",
                                categoryId = categoryId,
                                categoryName = categoryName,
                                sellerId = 1L,
                                sellerName = "Vivo Store",
                                images = listOf(Constants.PLACEHOLDER_PRODUCT),
                                brand = "Vivo",
                                manufacturer = null,
                                weight = null,
                                active = true,
                                featured = false,
                                status = "active",
                                averageRating = 4.3,
                                totalReviews = 80,
                                totalSold = null,
                                tags = emptyList(),
                                createdAt = null,
                                updatedAt = null
                        )
                )
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text(categoryName) },
                                navigationIcon = {
                                        IconButton(onClick = onNavigateBack) {
                                                Icon(Icons.Default.ArrowBack, "Back")
                                        }
                                },
                                actions = {
                                        IconButton(
                                                onClick = {
                                                        viewType =
                                                                if (viewType == "Grid") "List"
                                                                else "Grid"
                                                }
                                        ) {
                                                Icon(
                                                        if (viewType == "Grid")
                                                                Icons.Default.ViewList
                                                        else Icons.Default.GridView,
                                                        contentDescription = "Toggle View"
                                                )
                                        }
                                        IconButton(onClick = { /* Search */}) {
                                                Icon(Icons.Default.Search, "Search")
                                        }
                                }
                        )
                }
        ) { padding ->
                Column(modifier = modifier.fillMaxSize().padding(padding)) {
                        // Filter Bar
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = "${products.size} Products",
                                        style = MaterialTheme.typography.titleMedium
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Filter Button
                                        OutlinedButton(
                                                onClick = { /* Open filter */},
                                                contentPadding =
                                                        PaddingValues(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                        )
                                        ) {
                                                Icon(
                                                        Icons.Default.FilterList,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Filter")
                                        }

                                        // Sort Button
                                        Box {
                                                OutlinedButton(
                                                        onClick = { showSortMenu = true },
                                                        contentPadding =
                                                                PaddingValues(
                                                                        horizontal = 16.dp,
                                                                        vertical = 8.dp
                                                                )
                                                ) {
                                                        Icon(
                                                                Icons.Default.Sort,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(sortBy)
                                                }

                                                DropdownMenu(
                                                        expanded = showSortMenu,
                                                        onDismissRequest = { showSortMenu = false }
                                                ) {
                                                        listOf(
                                                                        "Popular",
                                                                        "Price: Low to High",
                                                                        "Price: High to Low",
                                                                        "Rating",
                                                                        "Newest"
                                                                )
                                                                .forEach { option ->
                                                                        DropdownMenuItem(
                                                                                text = {
                                                                                        Text(option)
                                                                                },
                                                                                onClick = {
                                                                                        sortBy =
                                                                                                option
                                                                                        showSortMenu =
                                                                                                false
                                                                                }
                                                                        )
                                                                }
                                                }
                                        }
                                }
                        }

                        HorizontalDivider()

                        // Products Grid
                        if (products.isEmpty()) {
                                // Empty State
                                Column(
                                        modifier = Modifier.fillMaxSize().padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        Icon(
                                                Icons.Default.SearchOff,
                                                contentDescription = null,
                                                modifier = Modifier.size(80.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                                text = "No Products Found",
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                                text = "Try adjusting your filters",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        } else {
                                LazyVerticalGrid(
                                        columns = GridCells.Fixed(if (viewType == "Grid") 2 else 1),
                                        contentPadding = PaddingValues(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        items(products) { product ->
                                                ProductCard(
                                                        product = product,
                                                        onClick = { onProductClick(product.id) },
                                                        onAddToCart = { /* Add to cart */},
                                                        isFavorite = product.featured,
                                                        onToggleFavorite = {
                                                                onToggleFavorite(product.id)
                                                        }
                                                )
                                        }
                                }
                        }
                }
        }
}
