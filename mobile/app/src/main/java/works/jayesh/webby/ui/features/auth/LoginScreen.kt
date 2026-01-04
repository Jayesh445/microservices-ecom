package works.jayesh.webby.ui.features.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.ui.components.AppOutlinedButton
import works.jayesh.webby.ui.components.AppTextField

@Composable
fun LoginScreen(
        onLoginSuccess: () -> Unit,
        onNavigateToRegister: () -> Unit,
        onNavigateToOtp: () -> Unit,
        modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(LoginState()) }

    Column(
            modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // App Logo/Icon
        Icon(
                imageVector = Icons.Default.Lock, // Replace with actual logo
                contentDescription = "Webby Logo",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome Text
        Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
                text = "Login to continue shopping",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Email TextField
        AppTextField(
                value = state.email,
                onValueChange = { state = state.copy(email = it, emailError = null) },
                label = "Email",
                placeholder = "Enter your email",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                isError = state.emailError != null,
                errorMessage = state.emailError,
                enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        AppTextField(
                value = state.password,
                onValueChange = { state = state.copy(password = it, passwordError = null) },
                label = "Password",
                placeholder = "Enter your password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                imeAction = ImeAction.Done,
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
                enabled = !state.isLoading,
                keyboardActions =
                        androidx.compose.foundation.text.KeyboardActions(
                                onDone = { /* Handle login */}
                        )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password
        Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End).clickable { /* Handle forgot password */}
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error Message
        state.error?.let { error ->
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                            )
            ) {
                Text(
                        text = error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Login Button
        AppButton(
                text = "Login",
                onClick = {
                    // Validation
                    val emailError = if (state.email.isBlank()) "Email is required" else null
                    val passwordError =
                            if (state.password.isBlank()) "Password is required" else null

                    if (emailError == null && passwordError == null) {
                        // TODO: Actual login logic
                        onLoginSuccess()
                    } else {
                        state = state.copy(emailError = emailError, passwordError = passwordError)
                    }
                },
                loading = state.isLoading,
                enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Divider with OR
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login with OTP Button
        AppOutlinedButton(text = "Login with OTP", onClick = onNavigateToOtp)

        Spacer(modifier = Modifier.height(32.dp))

        // Register Link
        Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
