package io.github.krisbitney.yuli.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.github.krisbitney.yuli.database.models.Event

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): SocialDatabase {
    val driver = driverFactory.createDriver()
    return SocialDatabase(
        driver,
        eventAdapter = Event.Adapter(kindAdapter = EnumColumnAdapter())
    )
}