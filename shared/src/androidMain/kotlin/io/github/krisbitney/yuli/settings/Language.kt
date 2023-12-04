package io.github.krisbitney.yuli.settings

import androidx.compose.ui.text.intl.Locale

actual fun getSystemLanguageCode(): String {
    return Locale.current.language
}