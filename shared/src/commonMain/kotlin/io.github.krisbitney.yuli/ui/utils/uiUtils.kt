package io.github.krisbitney.yuli.ui.utils

import androidx.compose.ui.graphics.painter.BitmapPainter

expect fun List<Byte>.toPainter(): BitmapPainter

expect fun openUrl(url: String, androidContext: Any? = null)