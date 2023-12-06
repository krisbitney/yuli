package io.github.krisbitney.yuli.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import io.github.krisbitney.yuli.settings.Localization
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
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        // Loading Indicator
        if (model.value.isLoading) {
            Box(
                // Semi-transparent black background
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp).offset(y = (-40).dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
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
            TitleColumn(null, Modifier.wrapContentSize(), MaterialTheme.colorScheme.onSurface)

            // Username and Password
            TextInput(
                label = Localization.stringResource("username"),
                value = component.usernameInput,
                onValueChange = { component.usernameInput = it },
                hideValue = false,
                enabled = !model.value.isLoading
            )
            TextInput(
                label = Localization.stringResource("password"),
                value = component.passwordInput,
                onValueChange = { component.passwordInput = it },
                hideValue = true,
                enabled = !model.value.isLoading
            )

            // Error Message
            if (model.value.errorMsg != null) {
                Text(
                    text = model.value.errorMsg ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

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
                modifier = Modifier.padding(top = 16.dp).height(48.dp).width(96.dp),
                enabled = !model.value.isLoading
            ) {
                Text(text = Localization.stringResource("login"), style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}
