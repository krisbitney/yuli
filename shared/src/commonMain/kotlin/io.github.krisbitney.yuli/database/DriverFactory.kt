package io.github.krisbitney.yuli.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.github.krisbitney.yuli.database.models.Event
import io.github.krisbitney.yuli.database.models.Profile
import io.github.krisbitney.yuli.database.models.User

expect object DriverFactory {
    fun <C>createDriver(context: C): SqlDriver
}

fun <C>createDatabase(context: C): SocialDatabase {
    val driver = DriverFactory.createDriver(context)
    return SocialDatabase(
        driver,
        eventAdapter = Event.Adapter(
            kindAdapter = EnumColumnAdapter()
        ),
        profileAdapter = Profile.Adapter(
            followerAdapter = BooleanColumnAdapter,
            followingAdapter = BooleanColumnAdapter
        ),
        userAdapter = User.Adapter(
            isLoggedInAdapter = BooleanColumnAdapter
        )
    )
}

object BooleanColumnAdapter : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long) = databaseValue > 0
    override fun encode(value: Boolean): Long = if (value) 1 else 0
}