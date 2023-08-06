package io.github.krisbitney.yuli.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual object DriverFactory {

    actual fun <C>createDriver(context: C): SqlDriver {
        return AndroidSqliteDriver(
            schema = SocialDatabase.Schema,
            context = context as Context,
            name = "social.db",
            callback = object : AndroidSqliteDriver.Callback(SocialDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }
}