package works.jayesh.webby.ui.features.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import works.jayesh.webby.ui.components.AppButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
        productId: Long = 1,
        productName: String = "iPhone 15 Pro",
        onNavigateBack: () -> Unit = {},
        onSubmitReview: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    var rating by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Write Review") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            }
    ) { padding ->
        Column(
                modifier =
                        modifier.fillMaxSize()
                                .padding(padding)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
        ) {
            // Product Info
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
            ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                                text = "Rating for",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                                text = productName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Rating
            Text(
                    text = "Rate this product",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                repeat(5) { index ->
                    IconButton(onClick = { rating = index + 1 }, modifier = Modifier.size(56.dp)) {
                        Icon(
                                if (index < rating) Icons.Filled.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFB300) else Color.Gray,
                                modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            if (rating > 0) {
                Text(
                        text =
                                when (rating) {
                                    1 -> "Poor"
                                    2 -> "Fair"
                                    3 -> "Good"
                                    4 -> "Very Good"
                                    5 -> "Excellent"
                                    else -> ""
                                },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Review Title
            Text(
                    text = "Review Title",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Summarize your review") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Review Comment
            Text(
                    text = "Your Review",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Share your experience with this product") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    maxLines = 6
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = "${comment.length}/500",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            AppButton(
                    text = "Submit Review",
                    onClick = onSubmitReview,
                    enabled = rating > 0 && title.isNotBlank() && comment.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Note
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                            text =
                                    "Your review will be visible after admin approval. Please be honest and fair in your review.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
