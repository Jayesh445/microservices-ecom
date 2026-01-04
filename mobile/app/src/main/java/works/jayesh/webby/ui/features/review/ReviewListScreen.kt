package works.jayesh.webby.ui.features.review

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Review(
        val id: Long,
        val userName: String,
        val rating: Int,
        val title: String,
        val comment: String,
        val date: String,
        val verified: Boolean,
        val helpful: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewListScreen(
        productId: Long = 1,
        productName: String = "iPhone 15 Pro",
        averageRating: Float = 4.5f,
        totalReviews: Int = 156,
        onNavigateBack: () -> Unit = {},
        onWriteReview: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    val reviews = remember {
        listOf(
                Review(
                        1,
                        "Alice Johnson",
                        5,
                        "Excellent product!",
                        "Absolutely love this product. Quality is top-notch and delivery was super fast. Highly recommended!",
                        "Jan 15, 2024",
                        true,
                        24
                ),
                Review(
                        2,
                        "Bob Smith",
                        4,
                        "Good value for money",
                        "Product is good but could be better in some aspects. Overall satisfied with the purchase.",
                        "Jan 14, 2024",
                        true,
                        15
                ),
                Review(
                        3,
                        "Carol White",
                        5,
                        "Perfect!",
                        "Exactly what I was looking for. The quality exceeded my expectations. Will definitely buy again.",
                        "Jan 13, 2024",
                        false,
                        32
                ),
                Review(
                        4,
                        "David Brown",
                        3,
                        "Average",
                        "It's okay. Nothing special but does the job. Expected more for the price.",
                        "Jan 12, 2024",
                        true,
                        8
                ),
                Review(
                        5,
                        "Emma Davis",
                        5,
                        "Love it!",
                        "Amazing product! Fast shipping and excellent customer service. Very happy with my purchase.",
                        "Jan 11, 2024",
                        true,
                        19
                ),
                Review(
                        6,
                        "Frank Miller",
                        4,
                        "Recommended",
                        "Good quality product. Minor issues but overall satisfied. Would recommend to friends.",
                        "Jan 10, 2024",
                        false,
                        12
                )
        )
    }

    var sortBy by remember { mutableStateOf("Most Recent") }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Customer Reviews") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                        onClick = onWriteReview,
                        icon = { Icon(Icons.Default.Edit, "Write Review") },
                        text = { Text("Write Review") }
                )
            }
    ) { padding ->
        LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rating Summary
            item {
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                ) {
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                    text = productName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                        text = String.format("%.1f", averageRating),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                    text = "$totalReviews reviews",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            repeat(5) { index ->
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                            text = "${5 - index}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                            Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    LinearProgressIndicator(
                                            progress = (5 - index) * 0.15f,
                                            modifier = Modifier.width(60.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sort Options
            item {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = "${reviews.size} Reviews",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )

                    Box {
                        TextButton(onClick = { showSortMenu = true }) {
                            Text(sortBy)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                        ) {
                            listOf("Most Recent", "Highest Rating", "Lowest Rating", "Most Helpful")
                                    .forEach { option ->
                                        DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    sortBy = option
                                                    showSortMenu = false
                                                }
                                        )
                                    }
                        }
                    }
                }
            }

            // Reviews List
            items(reviews) { review -> ReviewCard(review) }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                                text = review.userName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                        )
                        if (review.verified) {
                            Spacer(modifier = Modifier.width(8.dp))
                            AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                                "Verified",
                                                style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                    Text(
                            text = review.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating
            Row {
                repeat(5) { index ->
                    Icon(
                            if (index < review.rating) Icons.Filled.Star
                            else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < review.rating) Color(0xFFFFB300) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                    text = review.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Comment
            Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = { /* Mark helpful */}) {
                    Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Helpful (${review.helpful})")
                }

                TextButton(onClick = { /* Report */}) {
                    Icon(
                            Icons.Default.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report")
                }
            }
        }
    }
}
