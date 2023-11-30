package io.github.krisbitney.yuli.state.home.integration

import io.github.krisbitney.yuli.state.home.store.YuliHomeStoreProvider.Database
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.models.User
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalStdlibApi::class)
class YuliHomeStoreDatabase(private val database: YuliDatabase) : Database {
    override suspend fun selectUser(): User? = database.selectUser()

    override suspend fun countFollows(type: FollowType): Flow<Long> = database.countProfilesAsFlow(type)

    override suspend fun watchLastUpdate(): Flow<Long> = database.watchLastUpdate()
}