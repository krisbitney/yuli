package io.github.krisbitney.yuli.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.state.login.YuliLogin
import io.github.krisbitney.yuli.ui.common.TitleColumn

@Composable
fun LoginScreen(component: YuliLogin) {
    val model = component.model.collectAsState()
    if (model.value.username != null) {
        component.usernameInput = model.value.username ?: ""
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Close button
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.Start,
            ) {
                IconButton(
                    onClick = { component.onCloseClicked() },
                    colors = iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    modifier = Modifier.size(24.dp)
                ) { Icon(Icons.Rounded.Close, "Close login screen") }
            }

            // Title
            TitleColumn(null, Modifier.wrapContentSize())

            // Username and Password
            TextInput(
                label = "Username",
                value = component.usernameInput,
                onValueChange = { component.usernameInput = it },
                hideValue = false
            )
            TextInput(
                label = "Password",
                value = component.passwordInput,
                onValueChange = { component.passwordInput = it },
                hideValue = true
            )

            // Login Button
            Button(onClick = {
                component.onLoginClicked(component.usernameInput, component.passwordInput)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp
                ),
                modifier = Modifier.padding(top = 16.dp).height(48.dp).width(96.dp)
            ) {
                Text(text = "Login", style = MaterialTheme.typography.headlineLarge)
            }

            // Error Message
            if (model.value.errorMsg != null) {
                Text(
                    text = model.value.errorMsg ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
