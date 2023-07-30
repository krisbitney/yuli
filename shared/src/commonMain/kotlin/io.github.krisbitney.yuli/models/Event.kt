package io.github.krisbitney.yuli.models

data class Event(
    val profile: Profile,
    val kind: Kind,
    val timestamp: Long
) {
    enum class Kind {
        FOLLOWED,
        UNFOLLOWED
    }
}