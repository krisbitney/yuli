package io.github.krisbitney.yuli.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.state.login.YuliLogin
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoginScreen(component: YuliLogin) {
    val model = component.model.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { component.onCloseClicked() }) { Icons.Rounded.Close }
            }

            // App Logo
            Image(painter = painterResource("ic_launcher-playstore.png"), contentDescription = "App Logo")

            // Username Input or Display
            if (model.value.username == null) {
                BasicTextField(
                    value = component.usernameInput,
                    onValueChange = { component.usernameInput = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Stylized Username
                Text(text = model.value.username ?: "")
                // Button to login with a different username
                Button(onClick = { component.showConfirmation() }) {
                    Text("Change User")
                }
            }

            if (component.showWarning) {
                ConfirmationBox(
                    message = "If you log in with a new account, your saved data will be cleared and replaced with the new account's data.",
                    buttonText = "I understand"
                ) {
                    component.onConfirmationClosed()
                }
            }

            // Password Input
            BasicTextField(
                value = component.passwordInput,
                onValueChange = { component.passwordInput = it },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            // Login Button
            Button(onClick = { component.onLoginClicked(component.usernameInput, component.passwordInput) }) {
                Text("Login")
            }

            // Error Message
            if (model.value.errorMsg != null) {
                Text(text = model.value.errorMsg ?: "", color = Color.Red)
            }
        }
    }
}
