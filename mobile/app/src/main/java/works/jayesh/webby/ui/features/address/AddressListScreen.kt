package works.jayesh.webby.ui.features.address

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import works.jayesh.webby.domain.model.Address
import works.jayesh.webby.domain.model.AddressType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
        onNavigateBack: () -> Unit = {},
        onNavigateToAddAddress: () -> Unit = {},
        onNavigateToEditAddress: (Long) -> Unit = {},
        modifier: Modifier = Modifier
) {
    val addresses = remember {
        listOf(
                Address(
                        id = 1,
                        userId = 1,
                        fullName = "John Doe",
                        phoneNumber = "+1234567890",
                        addressLine1 = "123 Main Street",
                        addressLine2 = "Apartment 4B",
                        city = "New York",
                        state = "NY",
                        country = "USA",
                        zipCode = "10001",
                        addressType = AddressType.HOME,
                        isDefault = true
                ),
                Address(
                        id = 2,
                        userId = 1,
                        fullName = "John Doe",
                        phoneNumber = "+1234567890",
                        addressLine1 = "456 Office Plaza",
                        addressLine2 = "Floor 5",
                        city = "New York",
                        state = "NY",
                        country = "USA",
                        zipCode = "10002",
                        addressType = AddressType.WORK,
                        isDefault = false
                )
        )
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("My Addresses") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToAddAddress) {
                    Icon(Icons.Default.Add, "Add Address")
                }
            }
    ) { padding ->
        if (addresses.isEmpty()) {
            Box(
                    modifier = modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                            Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                            text = "No addresses saved",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onNavigateToAddAddress) { Text("Add New Address") }
                }
            }
        } else {
            LazyColumn(
                    modifier = modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(addresses) { address ->
                    AddressCard(
                            address = address,
                            onEditClick = { onNavigateToEditAddress(address.id) },
                            onDeleteClick = { /* Handle delete */ }
                    )
                }
            }
        }
    }
}

@Composable
fun AddressCard(
        address: Address,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            when (address.addressType) {
                                AddressType.HOME -> Icons.Default.Home
                                AddressType.WORK -> Icons.Default.Work
                                AddressType.OTHER -> Icons.Default.LocationOn
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                            text = address.addressType.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                    )
                    if (address.isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                    text = "Default",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = address.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                    text = address.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = address.fullAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                TextButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}
