package works.jayesh.webby.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        loading: Boolean = false,
        icon: ImageVector? = null,
        colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().height(56.dp),
            enabled = enabled && !loading,
            colors = colors,
            shape = RoundedCornerShape(12.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
            )
        } else {
            Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun AppOutlinedButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        icon: ImageVector? = null
) {
    OutlinedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().height(56.dp),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp)
    ) {
        Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun AppTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        label: String = "",
        placeholder: String = "",
        leadingIcon: ImageVector? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        isPassword: Boolean = false,
        isError: Boolean = false,
        errorMessage: String? = null,
        keyboardType: KeyboardType = KeyboardType.Text,
        imeAction: ImeAction = ImeAction.Done,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        singleLine: Boolean = true,
        maxLines: Int = 1,
        enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label =
                        if (label.isNotEmpty()) {
                            { Text(label) }
                        } else null,
                placeholder =
                        if (placeholder.isNotEmpty()) {
                            { Text(placeholder) }
                        } else null,
                leadingIcon =
                        leadingIcon?.let { { Icon(imageVector = it, contentDescription = null) } },
                trailingIcon =
                        if (isPassword) {
                            {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                            imageVector =
                                                    if (passwordVisible) Icons.Default.Visibility
                                                    else Icons.Default.VisibilityOff,
                                            contentDescription =
                                                    if (passwordVisible) "Hide password"
                                                    else "Show password"
                                    )
                                }
                            }
                        } else trailingIcon,
                visualTransformation =
                        if (isPassword && !passwordVisible) {
                            PasswordVisualTransformation()
                        } else {
                            VisualTransformation.None
                        },
                keyboardOptions =
                        KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                isError = isError,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors =
                        OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
        )

        if (isError && !errorMessage.isNullOrEmpty()) {
            Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun AppSearchBar(
        query: String,
        onQueryChange: (String) -> Unit,
        onSearch: () -> Unit,
        modifier: Modifier = Modifier,
        placeholder: String = "Search...",
        enabled: Boolean = true
) {
    OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon =
                    if (query.isNotEmpty()) {
                        {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear"
                                )
                            }
                        }
                    } else null,
            singleLine = true,
            enabled = enabled,
            shape = RoundedCornerShape(28.dp),
            colors =
                    OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(24.dp))
        AppButton(text = "Retry", onClick = onRetry, modifier = Modifier.width(200.dp))
    }
}

@Composable
fun EmptyView(
        message: String,
        icon: ImageVector = Icons.Default.ShoppingCart,
        actionText: String? = null,
        onAction: (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(text = actionText, onClick = onAction, modifier = Modifier.width(200.dp))
        }
    }
}
