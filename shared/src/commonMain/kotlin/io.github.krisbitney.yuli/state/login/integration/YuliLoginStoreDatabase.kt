package io.github.krisbitney.yuli.state.login.integration

import io.github.krisbitney.yuli.state.login.store.YuliLoginStoreProvider
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.User

@OptIn(ExperimentalStdlibApi::class)
class YuliLoginStoreDatabase(private val database: YuliDatabase) : YuliLoginStoreProvider.Database {
    override suspend fun selectUser(): User? = database.selectUser()
}