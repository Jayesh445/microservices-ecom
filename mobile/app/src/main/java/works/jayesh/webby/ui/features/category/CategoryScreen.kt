package works.jayesh.webby.ui.features.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.domain.model.Category
import works.jayesh.webby.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
        onNavigateToCategoryProducts: (Long) -> Unit = {},
        modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf(
                Category(
                        1,
                        "Electronics",
                        "electronics",
                        "Latest gadgets",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                ),
                Category(
                        2,
                        "Fashion",
                        "fashion",
                        "Trendy clothes",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                ),
                Category(
                        3,
                        "Home & Living",
                        "home-living",
                        "Furniture & decor",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                ),
                Category(
                        4,
                        "Beauty",
                        "beauty",
                        "Cosmetics & skincare",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                ),
                Category(
                        5,
                        "Sports",
                        "sports",
                        "Fitness equipment",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                ),
                Category(
                        6,
                        "Books",
                        "books",
                        "Books & magazines",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                ),
                Category(7, "Toys", "toys", "Kids toys", null, Constants.PLACEHOLDER_PRODUCT, true),
                Category(
                        8,
                        "Groceries",
                        "groceries",
                        "Food items",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                ),
                Category(
                        9,
                        "Automotive",
                        "automotive",
                        "Car accessories",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                ),
                Category(
                        10,
                        "Health",
                        "health",
                        "Health products",
                        null,
                        Constants.PLACEHOLDER_PRODUCT,
                        true
                )
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Categories") }) }) { padding ->
        LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryCard(
                        category = category,
                        onClick = { onNavigateToCategoryProducts(category.id) }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.aspectRatio(1f)) {
        Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                    model = category.imageUrl,
                    contentDescription = category.name,
                    modifier = Modifier.size(80.dp).padding(8.dp),
                    contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )

            if (category.description != null) {
                Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
