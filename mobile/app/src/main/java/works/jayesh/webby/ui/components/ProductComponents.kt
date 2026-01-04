package works.jayesh.webby.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.domain.model.Product
import works.jayesh.webby.ui.theme.DiscountRed
import works.jayesh.webby.ui.theme.PriceGreen
import works.jayesh.webby.ui.theme.RatingYellow
import works.jayesh.webby.utils.Constants
import works.jayesh.webby.utils.toRupees

@Composable
fun ProductCard(
        product: Product,
        onClick: () -> Unit,
        onAddToCart: () -> Unit,
        isFavorite: Boolean,
        onToggleFavorite: () -> Unit,
        modifier: Modifier = Modifier
) {
    Card(
            modifier = modifier.width(160.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Product Image
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                AsyncImage(
                        model = product.mainImage.ifEmpty { Constants.PLACEHOLDER_PRODUCT },
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                )

                // Discount Badge
                if (product.discount > 0) {
                    Surface(
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                            color = DiscountRed,
                            shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                                text = "${product.discount}% OFF",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                        )
                    }
                }

                // Wishlist Icon
                IconButton(
                        onClick = { onToggleFavorite() },
                        modifier =
                                Modifier.align(Alignment.TopStart)
                                        .padding(4.dp)
                                        .size(32.dp)
                                        .background(
                                                Color.White.copy(alpha = 0.7f),
                                                RoundedCornerShape(16.dp)
                                        )
                ) {
                    Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Add to wishlist",
                            tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Product Details
            Column(modifier = Modifier.padding(8.dp)) {
                // Product Name
                Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.height(40.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating
                product.averageRating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = RatingYellow,
                                modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                                text = String.format("%.1f", rating),
                                style = MaterialTheme.typography.bodySmall
                        )
                        product.totalReviews?.let { reviews ->
                            Text(
                                    text = " ($reviews)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Price
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                            text = product.finalPrice.toRupees(),
                            style = MaterialTheme.typography.titleMedium,
                            color = PriceGreen
                    )
                    if (product.discountPrice != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = product.price.toRupees(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Add to Cart Button
                Button(
                        onClick = onAddToCart,
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun ProductListItem(
        product: Product,
        onClick: () -> Unit,
        onAddToCart: () -> Unit,
        modifier: Modifier = Modifier
) {
    Card(
            modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            // Product Image
            Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))) {
                AsyncImage(
                        model = product.mainImage.ifEmpty { Constants.PLACEHOLDER_PRODUCT },
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                )

                if (product.discount > 0) {
                    Surface(
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                            color = DiscountRed,
                            shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                                text = "${product.discount}% OFF",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Details
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating
                product.averageRating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = RatingYellow,
                                modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                                text = String.format("%.1f", rating),
                                style = MaterialTheme.typography.bodySmall
                        )
                        product.totalReviews?.let { reviews ->
                            Text(
                                    text = " ($reviews reviews)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Price Row
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                    text = product.finalPrice.toRupees(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PriceGreen
                            )
                            if (product.discountPrice != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = product.price.toRupees(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }

                    // Add to Cart Button
                    FilledTonalIconButton(onClick = onAddToCart, modifier = Modifier.size(36.dp)) {
                        Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Add to cart",
                                modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBar(rating: Double, modifier: Modifier = Modifier, starSize: Int = 16) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Icon(
                    imageVector =
                            if (index < rating.toInt()) Icons.Default.Star
                            else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = RatingYellow,
                    modifier = Modifier.size(starSize.dp)
            )
        }
    }
}
