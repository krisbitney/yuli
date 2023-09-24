package io.github.krisbitney.yuli.state.home.integration

import io.github.krisbitney.yuli.state.home.YuliHome.Model
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.State

internal val stateToModel: (State) -> Model = {
    Model(
        user = it.user,
        mutualsCount = it.mutualsCount,
        nonfollowersCount = it.nonfollowersCount,
        fansCount = it.fansCount,
        formerConnectionsCount = it.formerConnectionsCount,
    )
}