package io.github.krisbitney.yuli.ui.utils

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

actual fun List<Byte>.toPainter(): BitmapPainter {
    val bmp = BitmapFactory.decodeByteArray(this.toByteArray(), 0, this.size)
    return BitmapPainter(bmp.asImageBitmap())
}