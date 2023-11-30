package io.github.krisbitney.yuli.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
expect fun ImageLink(
    painter: Painter,
    contentDescription: String? = null,
    modifier: Modifier,
    contentScale: ContentScale,
    url: String,
)