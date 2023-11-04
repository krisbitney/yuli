package io.github.krisbitney.yuli.state.follows.integration

import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.state.follows.store.YuliFollowsStoreProvider

@OptIn(ExperimentalStdlibApi::class)
class YuliFollowsStoreDatabase(private val database: YuliDatabase) : YuliFollowsStoreProvider.Database {
    override fun selectProfiles(type: FollowType): List<Profile> = database.selectProfiles(type)

}