package io.github.krisbitney.yuli.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen() {
    Column(Modifier.fillMaxWidth()) {
        Card {
            Text("Mutuals")
        }
        Card {
            Text("Non-followers")
        }
        Card {
            Text("Fans")
        }
        Card {
            Text("Former Connections")
        }
    }
}
