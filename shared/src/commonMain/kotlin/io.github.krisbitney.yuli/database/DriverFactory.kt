package io.github.krisbitney.yuli.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.github.krisbitney.yuli.database.models.Event

expect object DriverFactory {
    fun <C>createDriver(context: C): SqlDriver
}

fun <C>createDatabase(context: C): SocialDatabase {
    val driver = DriverFactory.createDriver(context)
    return SocialDatabase(
        driver,
        eventAdapter = Event.Adapter(
            kindAdapter = EnumColumnAdapter()
        )
    )
}
