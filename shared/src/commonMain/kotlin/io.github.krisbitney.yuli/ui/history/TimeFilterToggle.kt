package io.github.krisbitney.yuli.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.models.Event
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TimeFilterToggle(timePeriod: Event.TimePeriod, onToggle: (Event.TimePeriod) -> Unit) {
    TabRow(
        modifier = Modifier.wrapContentHeight().width(164.dp),
        selectedTabIndex = timePeriod.ordinal,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Event.TimePeriod.entries.forEachIndexed { index, it ->
            Tab(
                icon = {
                    Image(
                        painter = painterResource("calendar_$timePeriod.xml"),
                        contentDescription = "Toggle time period to ${it.name}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(64.dp),
                    )
                },
                text = { Text("Days") },
                selected = index == timePeriod.ordinal,
                onClick = { onToggle(it) },
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        }
    }
}