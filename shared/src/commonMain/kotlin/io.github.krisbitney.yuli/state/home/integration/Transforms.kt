package io.github.krisbitney.yuli.state.home.integration

import io.github.krisbitney.yuli.state.home.YuliHome
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore


internal val stateToModel: (YuliHomeStore.State) -> YuliHome.Model = {
    YuliHome.Model(
        user = it.user,
        mutualsCount = it.mutualsCount,
        nonfollowersCount = it.nonfollowersCount,
        fansCount = it.fansCount,
        formerFollowsCount = it.formerFollowsCount,
        updateInProgress = it.updateInProgress,
        updateError = it.updateError,
    )
}