package io.github.krisbitney.yuli.state.settings.integration

import io.github.krisbitney.yuli.state.settings.YuliSettings
import io.github.krisbitney.yuli.state.settings.store.YuliSettingsStore

internal val stateToModel: (YuliSettingsStore.State) -> YuliSettings.Model = {
    YuliSettings.Model(
        language = it.language,
    )
}