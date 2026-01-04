package works.jayesh.webby.ui.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import works.jayesh.webby.ui.components.AppButton
import works.jayesh.webby.ui.components.AppTextField
import works.jayesh.webby.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
        onNavigateBack: () -> Unit = {},
        onSaveProfile: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("John") }
    var lastName by remember { mutableStateOf("Doe") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var phone by remember { mutableStateOf("+1234567890") }
    var dateOfBirth by remember { mutableStateOf("") }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Edit Profile") },
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
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                        model = Constants.PLACEHOLDER_USER,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(120.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                )
                FilledIconButton(onClick = { /* Change photo */}, modifier = Modifier.size(36.dp)) {
                    Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { /* Change photo */}) { Text("Change Profile Picture") }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            AppTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "First Name",
                    placeholder = "Enter first name",
                    leadingIcon = Icons.Default.Person,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Last Name",
                    placeholder = "Enter last name",
                    leadingIcon = Icons.Default.Person,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "Enter email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            AppTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = "Date of Birth",
                    placeholder = "DD/MM/YYYY",
                    leadingIcon = Icons.Default.DateRange,
                    imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            AppButton(
                    text = "Save Changes",
                    onClick = onSaveProfile,
                    enabled =
                            firstName.isNotBlank() &&
                                    lastName.isNotBlank() &&
                                    email.isNotBlank() &&
                                    phone.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Delete Account
            TextButton(
                    onClick = { /* Delete account */},
                    colors =
                            ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                            )
            ) { Text("Delete Account") }
        }
    }
}
