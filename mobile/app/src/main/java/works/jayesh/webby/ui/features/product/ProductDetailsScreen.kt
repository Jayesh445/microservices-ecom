package works.jayesh.webby.ui.features.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.ui.components.RatingBar
import works.jayesh.webby.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
        productId: Long = 1,
        onNavigateBack: () -> Unit = {},
        onNavigateToCart: () -> Unit = {},
        onNavigateToReviews: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    var selectedImageIndex by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf(1) }
    var isFavorite by remember { mutableStateOf(false) }

    // Sample product images
    val images = remember {
        listOf(
                Constants.PLACEHOLDER_PRODUCT,
                Constants.PLACEHOLDER_PRODUCT,
                Constants.PLACEHOLDER_PRODUCT
        )
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Product Details") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { isFavorite = !isFavorite }) {
                                Icon(
                                        if (isFavorite) Icons.Filled.Favorite
                                        else Icons.Default.FavoriteBorder,
                                        "Favorite",
                                        tint =
                                                if (isFavorite) Color.Red
                                                else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = { /* Share */}) {
                                Icon(Icons.Default.Share, "Share")
                            }
                        }
                )
            },
            bottomBar = {
                Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(onClick = onNavigateToCart, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart")
                        }
                        AppButton(
                                text = "Buy Now",
                                onClick = { /* Buy now */},
                                modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
    ) { padding ->
        LazyColumn(modifier = modifier.fillMaxSize().padding(padding)) {
            // Main Image
            item {
                AsyncImage(
                        model = images[selectedImageIndex],
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxWidth().height(400.dp),
                        contentScale = ContentScale.Crop
                )
            }

            // Image Gallery
            item {
                LazyRow(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(images.size) { index ->
                        Card(
                                onClick = { selectedImageIndex = index },
                                modifier = Modifier.size(60.dp),
                                border =
                                        if (selectedImageIndex == index)
                                                CardDefaults.outlinedCardBorder()
                                                        .copy(
                                                                width = 2.dp,
                                                                brush =
                                                                        androidx.compose.ui.graphics
                                                                                .SolidColor(
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                                )
                                                        )
                                        else null
                        ) {
                            AsyncImage(
                                    model = images[index],
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Product Info
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Brand
                    Text(
                            text = "Apple",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Product Name
                    Text(
                            text = "iPhone 15 Pro Max",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingBar(rating = 4.50, starSize = 20)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = "4.5 (2,345 reviews)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onNavigateToReviews) { Text("See all") }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                                text = "$899.99",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = "$999.99",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                    text = "10% OFF",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stock Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = "In Stock (125 units available)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4CAF50)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quantity Selector
                    Text(
                            text = "Quantity",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.size(40.dp)
                        ) { Icon(Icons.Default.Remove, "Decrease") }

                        Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        IconButton(onClick = { quantity++ }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Add, "Increase")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                            text =
                                    "The iPhone 15 Pro Max features a stunning titanium design, A17 Pro chip, " +
                                            "ProMotion technology with up to 120Hz refresh rate, and the most advanced " +
                                            "camera system ever in an iPhone. Experience the power of computational photography " +
                                            "with 48MP main camera, improved night mode, and 5x optical zoom.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Specifications
                    Text(
                            text = "Specifications",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SpecificationItem("Display", "6.7\" Super Retina XDR OLED")
                    SpecificationItem("Processor", "A17 Pro chip")
                    SpecificationItem("Storage", "256GB / 512GB / 1TB")
                    SpecificationItem("Camera", "48MP + 12MP + 12MP")
                    SpecificationItem("Battery", "4,441 mAh")
                    SpecificationItem("OS", "iOS 17")
                    SpecificationItem("Weight", "221g")

                    Spacer(modifier = Modifier.height(24.dp))

                    // Features
                    Text(
                            text = "Key Features",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureChip("Titanium Design")
                    FeatureChip("5G Enabled")
                    FeatureChip("Face ID")
                    FeatureChip("Wireless Charging")
                    FeatureChip("Water Resistant")
                    FeatureChip("ProMotion 120Hz")
                }
            }
        }
    }
}

@Composable
fun SpecificationItem(label: String, value: String) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FeatureChip(text: String) {
    Surface(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small
    ) {
        Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
