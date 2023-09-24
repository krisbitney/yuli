package io.github.krisbitney.yuli.state.home.integration

import io.github.krisbitney.yuli.state.home.store.YuliHomeStoreProvider
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.User
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalStdlibApi::class)
class YuliHomeStoreDatabase(private val database: YuliDatabase) : YuliHomeStoreProvider.Database {
    override fun selectUser(): User? = database.selectUser()

    override fun countMutuals(): Flow<Long> = database.countMutuals()

    override fun countNonfollowers(): Flow<Long> = database.countNonfollowers()

    override fun countFans(): Flow<Long> = database.countFans()

    override fun countFormerConnections(): Flow<Long> = database.countFormerConnections()
}