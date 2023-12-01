package io.github.krisbitney.yuli.ui.utils

import androidx.compose.ui.graphics.painter.BitmapPainter
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

expect fun List<Byte>.toPainter(): BitmapPainter

expect fun openUrl(url: String, androidContext: Any? = null)

expect fun platformIsIos(): Boolean

fun formatDatetime(epochMilliseconds: Long): String {
    val datetime = Instant.fromEpochMilliseconds(epochMilliseconds)
    val localDateTime = datetime.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "${localDateTime.dayOfMonth} $month ${localDateTime.year}"
}