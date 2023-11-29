package io.github.krisbitney.yuli.state.login.integration

import io.github.krisbitney.yuli.state.login.YuliLogin.Model
import io.github.krisbitney.yuli.state.login.store.YuliLoginStore.State

internal val stateToModel: (State) -> Model = {
    Model(
        username = it.username,
        isLoggedIn = it.isLoggedIn,
        errorMsg = it.errorMsg,
        isLoading = it.isLoading
    )
}