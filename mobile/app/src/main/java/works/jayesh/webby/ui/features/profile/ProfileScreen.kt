package works.jayesh.webby.ui.features.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
        onNavigateToEditProfile: () -> Unit,
        onNavigateToOrders: () -> Unit,
        onNavigateToAddresses: () -> Unit,
        onNavigateToWishlist: () -> Unit,
        onNavigateToSettings: () -> Unit,
        onToggleTheme: () -> Unit = {},
        onLogout: () -> Unit,
        modifier: Modifier = Modifier
) {
        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Profile") },
                                actions = {
                                        IconButton(onClick = onToggleTheme) {
                                                Icon(Icons.Default.DarkMode, "Toggle Theme")
                                        }
                                        IconButton(onClick = onNavigateToSettings) {
                                                Icon(Icons.Default.Settings, "Settings")
                                        }
                                }
                        )
                }
        ) { padding ->
                LazyColumn(modifier = modifier.fillMaxSize().padding(padding)) {
                        // Profile Header
                        item {
                                Card(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme
                                                                        .primaryContainer
                                                )
                                ) {
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clickable(
                                                                        onClick =
                                                                                onNavigateToEditProfile
                                                                )
                                                                .padding(20.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                AsyncImage(
                                                        model = Constants.PLACEHOLDER_USER,
                                                        contentDescription = "Profile Picture",
                                                        modifier =
                                                                Modifier.size(80.dp)
                                                                        .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                )

                                                Spacer(modifier = Modifier.width(16.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                text = "John Doe",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleLarge,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimaryContainer
                                                        )
                                                        Text(
                                                                text = "john.doe@example.com",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimaryContainer
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                                text = "+91 9876543210",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimaryContainer
                                                        )
                                                }

                                                Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Edit Profile",
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onPrimaryContainer
                                                )
                                        }
                                }
                        }

                        // Quick Stats
                        item {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        StatCard(
                                                icon = Icons.Default.ShoppingBag,
                                                label = "Orders",
                                                value = "12",
                                                modifier = Modifier.weight(1f),
                                                onClick = onNavigateToOrders
                                        )
                                        StatCard(
                                                icon = Icons.Default.Favorite,
                                                label = "Wishlist",
                                                value = "24",
                                                modifier = Modifier.weight(1f),
                                                onClick = onNavigateToWishlist
                                        )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Menu Items
                        item {
                                Text(
                                        "Account",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 8.dp
                                                )
                                )
                        }

                        item {
                                ProfileMenuItem(
                                        icon = Icons.Default.Receipt,
                                        title = "My Orders",
                                        subtitle = "View and track your orders",
                                        onClick = onNavigateToOrders
                                )
                        }

                        item {
                                ProfileMenuItem(
                                        icon = Icons.Default.LocationOn,
                                        title = "My Addresses",
                                        subtitle = "Manage your delivery addresses",
                                        onClick = onNavigateToAddresses
                                )
                        }

                        item {
                                ProfileMenuItem(
                                        icon = Icons.Default.Favorite,
                                        title = "Wishlist",
                                        subtitle = "Your favorite products",
                                        onClick = onNavigateToWishlist
                                )
                        }

                        item {
                                ProfileMenuItem(
                                        icon = Icons.Default.Person,
                                        title = "Edit Profile",
                                        subtitle = "Update your personal information",
                                        onClick = onNavigateToEditProfile
                                )
                        }

                        item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                        "More",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 8.dp
                                                )
                                )
                        }

                        item {
                                ProfileMenuItem(
                                        icon = Icons.Default.Notifications,
                                        title = "Notifications",
                                        subtitle = "Manage notification preferences",
                                        onClick = { /* TODO */}
                                )
                        }

                        item {
                                ProfileMenuItem(
                                        icon = Icons.Default.HelpOutline,
                                        title = "Help & Support",
                                        subtitle = "Get help and contact support",
                                        onClick = { /* TODO */}
                                )
                        }

                        item {
                                ProfileMenuItem(
                                        icon = Icons.Default.Info,
                                        title = "About",
                                        subtitle = "App version and information",
                                        onClick = { /* TODO */}
                                )
                        }

                        item {
                                Spacer(modifier = Modifier.height(16.dp))
                                ProfileMenuItem(
                                        icon = Icons.Default.Logout,
                                        title = "Logout",
                                        subtitle = "Sign out from your account",
                                        onClick = onLogout,
                                        isDestructive = true
                                )
                        }

                        item {
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                        text = "Webby v1.0.0",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                        }
                }
        }
}

@Composable
private fun StatCard(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.clickable(onClick = onClick),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                value,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                                label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }
        }
}

@Composable
private fun ProfileMenuItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        title: String,
        subtitle: String,
        onClick: () -> Unit,
        isDestructive: Boolean = false
) {
        ListItem(
                headlineContent = {
                        Text(
                                title,
                                color =
                                        if (isDestructive) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurface
                        )
                },
                supportingContent = { Text(subtitle) },
                leadingContent = {
                        Icon(
                                icon,
                                contentDescription = null,
                                tint =
                                        if (isDestructive) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary
                        )
                },
                trailingContent = {
                        Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                },
                modifier = Modifier.clickable(onClick = onClick)
        )
        HorizontalDivider()
}
