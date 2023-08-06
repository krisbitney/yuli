package io.github.krisbitney.yuli.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration

actual object DriverFactory {
    actual fun <C>createDriver(context: C): SqlDriver {
        return NativeSqliteDriver(
            schema = SocialDatabase.Schema,
            name = "social.db",
            onConfiguration = { config: DatabaseConfiguration ->
                config.copy(
                    extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
                )
            }
        )
    }
}