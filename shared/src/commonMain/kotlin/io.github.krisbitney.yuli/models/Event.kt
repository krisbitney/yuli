package io.github.krisbitney.yuli.models

data class Event(
    val profile: Profile,
    val kind: Kind,
    val timestamp: Long
) {
    enum class Kind {
        FAN_TO_MUTUAL,
        FAN_TO_NONE,
        NONFOLLOWER_TO_MUTUAL,
        NONFOLLOWER_TO_NONE,
        MUTUAL_TO_NONFOLLOWER,
        MUTUAL_TO_FAN,
        NONE_TO_NONFOLLOWER,
        NONE_TO_FAN,
        NONFOLLOWER_TO_FAN,
        FAN_TO_NONFOLLOWER,
        NONE_TO_MUTUAL,
        MUTUAL_TO_NONE
    }
    
    fun message(): String = when (this.kind) {
        Kind.FAN_TO_MUTUAL -> "You followed ${profile.name} back"
        Kind.FAN_TO_NONE -> "${profile.name} unfollowed you"
        Kind.NONFOLLOWER_TO_MUTUAL -> "${profile.name} followed you back"
        Kind.NONFOLLOWER_TO_NONE -> "You unfollowed ${profile.name}"
        Kind.MUTUAL_TO_NONFOLLOWER -> "${profile.name} unfollowed you"
        Kind.MUTUAL_TO_FAN -> "You unfollowed ${profile.name}"
        Kind.NONE_TO_NONFOLLOWER -> "You followed ${profile.name}"
        Kind.NONE_TO_FAN -> "${profile.name} followed you"
        Kind.NONFOLLOWER_TO_FAN -> "You unfollowed ${profile.name} and they followed you"
        Kind.FAN_TO_NONFOLLOWER -> "You followed ${profile.name} and they unfollowed you"
        Kind.NONE_TO_MUTUAL -> "You and ${profile.name} began following each other"
        Kind.MUTUAL_TO_NONE -> "You and ${profile.name} unfollowed each other"
    }
}
