package works.jayesh.webby.ui.features.address

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import works.jayesh.webby.domain.model.AddressType
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.ui.components.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
        onNavigateBack: () -> Unit = {},
        onSaveAddress: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var addressLine1 by remember { mutableStateOf("") }
    var addressLine2 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AddressType.HOME) }
    var isDefault by remember { mutableStateOf(false) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Add New Address") },
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
                                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Address Type Selection
            Text(
                    text = "Address Type",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AddressType.values().forEach { type ->
                    FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name) },
                            leadingIcon = {
                                Icon(
                                        when (type) {
                                            AddressType.HOME -> Icons.Default.Home
                                            AddressType.WORK -> Icons.Default.Work
                                            AddressType.OTHER -> Icons.Default.LocationOn
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                )
                            }
                    )
                }
            }

            HorizontalDivider()

            // Contact Details
            Text(
                    text = "Contact Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )

            AppTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Full Name",
                    placeholder = "Enter full name",
                    leadingIcon = Icons.Default.Person,
                    imeAction = ImeAction.Next
            )

            AppTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Phone Number",
                    placeholder = "Enter phone number",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
            )

            HorizontalDivider()

            // Address Details
            Text(
                    text = "Address Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )

            AppTextField(
                    value = addressLine1,
                    onValueChange = { addressLine1 = it },
                    label = "Address Line 1",
                    placeholder = "House no., Building name",
                    leadingIcon = Icons.Default.LocationOn,
                    imeAction = ImeAction.Next
            )

            AppTextField(
                    value = addressLine2,
                    onValueChange = { addressLine2 = it },
                    label = "Address Line 2 (Optional)",
                    placeholder = "Road name, Area, Colony",
                    leadingIcon = Icons.Default.LocationOn,
                    imeAction = ImeAction.Next
            )

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "City",
                        placeholder = "Enter city",
                        leadingIcon = Icons.Default.LocationCity,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f)
                )

                AppTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = "State",
                        placeholder = "Enter state",
                        leadingIcon = Icons.Default.Map,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f)
                )
            }

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = "Country",
                        placeholder = "Enter country",
                        leadingIcon = Icons.Default.Public,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f)
                )

                AppTextField(
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        label = "ZIP Code",
                        placeholder = "Enter ZIP",
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            // Default Address Checkbox
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                        text = "Set as default address",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            AppButton(
                    text = "Save Address",
                    onClick = onSaveAddress,
                    enabled =
                            fullName.isNotBlank() &&
                                    phoneNumber.isNotBlank() &&
                                    addressLine1.isNotBlank() &&
                                    city.isNotBlank() &&
                                    state.isNotBlank() &&
                                    country.isNotBlank() &&
                                    zipCode.isNotBlank()
            )
        }
    }
}
