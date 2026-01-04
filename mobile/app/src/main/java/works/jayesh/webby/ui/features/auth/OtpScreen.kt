package works.jayesh.webby.ui.features.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import works.jayesh.webby.ui.components.AppButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
        email: String = "user@example.com",
        onVerifySuccess: () -> Unit = {},
        onNavigateBack: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    var otp by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(60) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("OTP Verification") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                )
            }
    ) { padding ->
        Column(
                modifier = modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "OTP",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                    text = "Verify Your Email",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                    text = "We've sent a 6-digit code to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
            )

            Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // OTP Input
            OtpInputField(value = otp, onValueChange = { if (it.length <= 6) otp = it }, length = 6)

            Spacer(modifier = Modifier.height(32.dp))

            // Timer
            if (timeLeft > 0) {
                Text(
                        text = "Resend code in ${timeLeft}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                TextButton(onClick = { timeLeft = 60 }) { Text("Resend OTP") }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Verify Button
            AppButton(
                    text = "Verify OTP",
                    onClick = onVerifySuccess,
                    loading = isLoading,
                    enabled = otp.length == 6 && !isLoading
            )
        }
    }
}

@Composable
fun OtpInputField(
        value: String,
        onValueChange: (String) -> Unit,
        length: Int,
        modifier: Modifier = Modifier
) {
    BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = modifier,
            decorationBox = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(length) { index ->
                        val char =
                                when {
                                    index < value.length -> value[index].toString()
                                    else -> ""
                                }
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .border(
                                                        width = 2.dp,
                                                        color =
                                                                if (index < value.length)
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                                else
                                                                        MaterialTheme.colorScheme
                                                                                .outline,
                                                        shape = MaterialTheme.shapes.medium
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                            Text(
                                    text = char,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
    )
}
