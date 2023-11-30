package io.github.krisbitney.yuli.state.history.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.state.history.YuliHistory
import io.github.krisbitney.yuli.state.history.store.YuliHistoryStore
import io.github.krisbitney.yuli.state.history.store.YuliHistoryStoreProvider
import io.github.krisbitney.yuli.state.utils.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class YuliHistoryComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    database: YuliDatabase,
    private val output: (YuliHistory.Output) -> Unit,
) : YuliHistory, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        YuliHistoryStoreProvider(
            storeFactory = storeFactory,
            database = YuliHistoryStoreDatabase(database = database),
        ).provide()
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    override val model: StateFlow<YuliHistory.Model> = store.stateFlow.map(scope, stateToModel)

    init {
        lifecycle.doOnDestroy {
            if (scope.isActive) {
                scope.cancel()
            }
        }
    }

    override fun onBackClicked() {
        output(YuliHistory.Output.Back)
    }

    override fun onTimePeriodClicked(timePeriod: Event.TimePeriod) {
        store.accept(YuliHistoryStore.Intent.FilterTime(timePeriod))
    }


}