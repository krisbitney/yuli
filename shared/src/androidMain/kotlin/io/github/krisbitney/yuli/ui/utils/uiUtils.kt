package io.github.krisbitney.yuli.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.content.ContextCompat.startActivity

actual fun List<Byte>.toPainter(): BitmapPainter {
    val bmp = BitmapFactory.decodeByteArray(this.toByteArray(), 0, this.size)
    return BitmapPainter(bmp.asImageBitmap())
}

actual fun openUrl(url: String, androidContext: Any?) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(androidContext as Context, intent, null)
}

actual fun platformIsIos(): Boolean = false