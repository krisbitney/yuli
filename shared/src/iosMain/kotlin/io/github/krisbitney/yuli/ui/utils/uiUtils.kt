package io.github.krisbitney.yuli.ui.utils

import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import platform.UIKit.UIApplication
import platform.Foundation.NSURL

actual fun List<Byte>.toPainter(): BitmapPainter {
    val img = Image.makeFromEncoded(toByteArray())
    val bmp = Bitmap.makeFromImage(img).asComposeImageBitmap()
    return BitmapPainter(bmp)
}

actual fun openUrl(url: String, androidContext: Any?) {
    val nsUrl = NSURL(string = url)
    UIApplication.sharedApplication.openURL(nsUrl)
}

actual fun platformIsIos(): Boolean = true