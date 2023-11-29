package io.github.krisbitney.yuli.ui.follows

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import io.github.krisbitney.yuli.ui.utils.openUrl

@Composable
actual fun ImageLink(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale,
    url: String
) {
    val context = LocalContext.current
    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.clickable { openUrl(url, context) }
    )
}