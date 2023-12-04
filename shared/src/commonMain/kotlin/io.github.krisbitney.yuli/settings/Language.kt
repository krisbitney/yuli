package io.github.krisbitney.yuli.settings

enum class Language(val value: String) {
    ENGLISH("en"),
    RUSSIAN("ru"),
    SPANISH("es");

    override fun toString(): String {
        return this.name.lowercase().replaceFirstChar { it.titlecase() }
    }

    companion object {
        fun default(): Language = when (getSystemLanguageCode()) {
            "ru" -> RUSSIAN
            "es" -> SPANISH
            else -> ENGLISH
        }
    }
}

expect fun getSystemLanguageCode(): String