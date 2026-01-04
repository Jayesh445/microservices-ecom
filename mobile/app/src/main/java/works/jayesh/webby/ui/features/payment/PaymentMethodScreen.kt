package works.jayesh.webby.ui.features.payment

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
import works.jayesh.webby.domain.model.PaymentMethod
import works.jayesh.webby.ui.components.AppButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
        onNavigateBack: () -> Unit = {},
        onPaymentSelected: (PaymentMethod) -> Unit = {},
        modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    val paymentMethods = remember {
        listOf(
                Triple(PaymentMethod.CARD, "Credit/Debit Card", Icons.Default.CreditCard),
                Triple(PaymentMethod.UPI, "UPI Payment", Icons.Default.AccountBalance),
                Triple(PaymentMethod.NET_BANKING, "Net Banking", Icons.Default.AccountBalance),
                Triple(PaymentMethod.COD, "Cash on Delivery", Icons.Default.Money)
        )
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Select Payment Method") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            },
            bottomBar = {
                Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AppButton(
                                text = "Continue",
                                onClick = { selectedMethod?.let { onPaymentSelected(it) } },
                                enabled = selectedMethod != null
                        )
                    }
                }
            }
    ) { padding ->
        LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                        text = "Choose your payment method",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(paymentMethods) { (method, name, icon) ->
                PaymentMethodCard(
                        method = method,
                        name = name,
                        icon = icon,
                        isSelected = selectedMethod == method,
                        onSelect = { selectedMethod = method }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Card(
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                MaterialTheme.colorScheme.secondaryContainer
                                )
                ) {
                    Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                    text = "100% Secure Payments",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                    text = "All transactions are encrypted and secure",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
        method: PaymentMethod,
        name: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        isSelected: Boolean,
        onSelect: () -> Unit,
        modifier: Modifier = Modifier
) {
    Card(
            onClick = onSelect,
            modifier = modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                    ),
            border =
                    if (isSelected)
                            CardDefaults.outlinedCardBorder()
                                    .copy(
                                            width = 2.dp,
                                            brush =
                                                    androidx.compose.ui.graphics.SolidColor(
                                                            MaterialTheme.colorScheme.primary
                                                    )
                                    )
                    else null
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                    icon,
                    contentDescription = null,
                    tint =
                            if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color =
                                if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                )
                if (method == PaymentMethod.COD) {
                    Text(
                            text = "Pay when you receive",
                            style = MaterialTheme.typography.bodySmall,
                            color =
                                    if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            RadioButton(selected = isSelected, onClick = onSelect)
        }
    }
}
