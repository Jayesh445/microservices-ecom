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
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.ui.components.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressScreen(
        addressId: Long = 1,
        onNavigateBack: () -> Unit = {},
        onUpdateAddress: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    // Pre-filled values for editing
    var selectedType by remember { mutableStateOf("HOME") }
    var fullName by remember { mutableStateOf("John Doe") }
    var phone by remember { mutableStateOf("+1234567890") }
    var addressLine1 by remember { mutableStateOf("123 Main Street") }
    var addressLine2 by remember { mutableStateOf("Apt 4B") }
    var city by remember { mutableStateOf("New York") }
    var state by remember { mutableStateOf("NY") }
    var country by remember { mutableStateOf("United States") }
    var zipCode by remember { mutableStateOf("10001") }
    var isDefault by remember { mutableStateOf(true) }

    val addressTypes = listOf("HOME", "WORK", "OTHER")

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Edit Address") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Delete address */}) {
                                Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                )
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
            // Address Type
            Text(text = "Address Type", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                addressTypes.forEach { type ->
                    FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type) },
                            leadingIcon =
                                    if (selectedType == type) {
                                        {
                                            Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contact Details
            Text(text = "Contact Details", style = MaterialTheme.typography.titleMedium)

            AppTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Full Name",
                    placeholder = "Enter full name",
                    leadingIcon = Icons.Default.Person,
                    imeAction = ImeAction.Next
            )

            AppTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number",
                    placeholder = "Enter phone number",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Address Details
            Text(text = "Address Details", style = MaterialTheme.typography.titleMedium)

            AppTextField(
                    value = addressLine1,
                    onValueChange = { addressLine1 = it },
                    label = "Address Line 1",
                    placeholder = "Street address",
                    leadingIcon = Icons.Default.Home,
                    imeAction = ImeAction.Next
            )

            AppTextField(
                    value = addressLine2,
                    onValueChange = { addressLine2 = it },
                    label = "Address Line 2 (Optional)",
                    placeholder = "Apartment, suite, etc.",
                    leadingIcon = Icons.Default.Home,
                    imeAction = ImeAction.Next
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "City",
                        placeholder = "City",
                        leadingIcon = Icons.Default.LocationCity,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f)
                )

                AppTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = "State",
                        placeholder = "State",
                        leadingIcon = Icons.Default.LocationOn,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = "Country",
                        placeholder = "Country",
                        leadingIcon = Icons.Default.Public,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f)
                )

                AppTextField(
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        label = "ZIP Code",
                        placeholder = "ZIP",
                        leadingIcon = Icons.Default.Pin,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Default Address Checkbox
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                        text = "Set as default address",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Update Button
            AppButton(
                    text = "Update Address",
                    onClick = onUpdateAddress,
                    enabled =
                            fullName.isNotBlank() &&
                                    phone.isNotBlank() &&
                                    addressLine1.isNotBlank() &&
                                    city.isNotBlank() &&
                                    state.isNotBlank() &&
                                    country.isNotBlank() &&
                                    zipCode.isNotBlank()
            )
        }
    }
}
