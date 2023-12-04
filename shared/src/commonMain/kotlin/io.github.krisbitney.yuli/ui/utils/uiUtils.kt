package io.github.krisbitney.yuli.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import dev.icerock.moko.resources.compose.stringResource
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.resources.MR
import io.github.krisbitney.yuli.settings.Language
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

@Composable
fun Language.localized(): String = when (this) {
    Language.ENGLISH -> stringResource(MR.strings.english)
    Language.RUSSIAN -> stringResource(MR.strings.russian)
    Language.SPANISH -> stringResource(MR.strings.spanish)
}

@Composable
fun Event.localizedMessage(): String  {
    return when (this.kind) {
        Event.Kind.GAINED_FOLLOWER -> "$name ${stringResource(MR.strings.followed_you)}"
        Event.Kind.LOST_FOLLOWER -> "$name ${stringResource(MR.strings.unfollowed_you)}"
        Event.Kind.STARTED_FOLLOWING -> "${stringResource(MR.strings.you_followed)} $name"
        Event.Kind.STOPPED_FOLLOWING -> "${stringResource(MR.strings.you_unfollowed)} $name"
    }
}