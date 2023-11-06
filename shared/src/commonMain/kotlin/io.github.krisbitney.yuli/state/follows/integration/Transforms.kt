package io.github.krisbitney.yuli.state.follows.integration

import io.github.krisbitney.yuli.state.follows.YuliFollows
import io.github.krisbitney.yuli.state.follows.store.YuliFollowsStore

internal val stateToModel: (YuliFollowsStore.State) -> YuliFollows.Model = {
    YuliFollows.Model(
        follows = it.follows,
        sortedBy = it.sortedBy
    )
}