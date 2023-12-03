package io.github.krisbitney.yuli.state.settings.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.github.krisbitney.yuli.state.settings.YuliSettings
import io.github.krisbitney.yuli.state.settings.store.YuliSettingsStoreProvider
import io.github.krisbitney.yuli.state.utils.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class YuliSettingsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (YuliSettings.Output) -> Unit,
) : YuliSettings, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        YuliSettingsStoreProvider(storeFactory = storeFactory).provide()
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    override val model: StateFlow<YuliSettings.Model> = store.stateFlow.map(scope, stateToModel)

    init {
        lifecycle.doOnDestroy {
            if (scope.isActive) {
                scope.cancel()
            }
        }
    }

    override fun onBackClicked() {
        output(YuliSettings.Output.Back)
    }
}