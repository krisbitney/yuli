package io.github.krisbitney.yuli.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.ui.common.ImageLink
import io.github.krisbitney.yuli.ui.utils.formatDatetime
import io.github.krisbitney.yuli.ui.utils.localizedMessage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EventItem(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxHeight().wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    when (event.kind) {
                        Event.Kind.GAINED_FOLLOWER -> "user_star.xml"
                        Event.Kind.LOST_FOLLOWER -> "user_none.xml"
                        Event.Kind.STARTED_FOLLOWING -> "user_plus.xml"
                        Event.Kind.STOPPED_FOLLOWING -> "user_minus.xml"
                    }
                ),
                contentDescription = "event kind icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(
                    when (event.kind) {
                        Event.Kind.GAINED_FOLLOWER -> MaterialTheme.colorScheme.onSurface
                        Event.Kind.LOST_FOLLOWER -> MaterialTheme.colorScheme.secondaryContainer
                        Event.Kind.STARTED_FOLLOWING -> MaterialTheme.colorScheme.primaryContainer
                        Event.Kind.STOPPED_FOLLOWING -> MaterialTheme.colorScheme.background
                    }
                )
            )
            Column(
                modifier = Modifier.fillMaxHeight().wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = event.localizedMessage(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatDatetime(event.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        ImageLink(
            painter = painterResource("instagram_glyph_icon.xml"),
            contentDescription = "instagram glyph icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(48.dp).padding(end = 12.dp),
            url = "https://instagram.com/${event.username}"
        )
    }
}