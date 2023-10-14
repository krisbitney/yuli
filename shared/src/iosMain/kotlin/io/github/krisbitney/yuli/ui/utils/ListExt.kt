package io.github.krisbitney.yuli.ui.utils

import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image

actual fun List<Byte>.toPainter(): BitmapPainter {
    val img = Image.makeFromEncoded(toByteArray())
    val bmp = Bitmap.makeFromImage(img).asComposeImageBitmap()
    return BitmapPainter(bmp)
}