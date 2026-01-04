package works.jayesh.webby.ui.features.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.ui.components.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
        onRegisterSuccess: () -> Unit = {},
        onNavigateBack: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Create Account") },
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
                                .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Registration Icon
            Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Register",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                    text = "Create Your Account",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                    text = "Fill in the details to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // First Name
            AppTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "First Name",
                    placeholder = "Enter first name",
                    leadingIcon = Icons.Default.Person,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Last Name
            AppTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Last Name",
                    placeholder = "Enter last name",
                    leadingIcon = Icons.Default.Person,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "Enter your email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            AppTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number",
                    placeholder = "Enter phone number",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Create password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            AppTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    placeholder = "Re-enter password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and Conditions
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = agreeToTerms, onCheckedChange = { agreeToTerms = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                        text = "I agree to the Terms & Conditions and Privacy Policy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            AppButton(
                    text = "Create Account",
                    onClick = onRegisterSuccess,
                    loading = isLoading,
                    enabled = !isLoading && agreeToTerms
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Link
            Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateBack() }
                )
            }
        }
    }
}
