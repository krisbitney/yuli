@file:JvmName("AndroidTypographyKt")
package io.github.krisbitney.yuli.ui.theme

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@SuppressLint("DiscouragedApi")
@Composable
actual fun font(res: String, weight: FontWeight, style: FontStyle): Font {
    val context = LocalContext.current
    val id = context.resources.getIdentifier(res, "font", context.packageName)
    return Font(id, weight, style)
}