package io.github.krisbitney.yuli.state.follows.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.state.follows.YuliFollows
import io.github.krisbitney.yuli.state.follows.store.YuliFollowsStore
import io.github.krisbitney.yuli.state.follows.store.YuliFollowsStoreProvider
import io.github.krisbitney.yuli.state.utils.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class YuliFollowsComponent (
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    database: YuliDatabase,
    private val output: (YuliFollows.Output) -> Unit,
    private val type: FollowType
) : YuliFollows, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        YuliFollowsStoreProvider(
            storeFactory = storeFactory,
            database = YuliFollowsStoreDatabase(database = database),
            type
        ).provide()
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    override val model: StateFlow<YuliFollows.Model> = store.stateFlow.map(scope, stateToModel)

    init {
        lifecycle.doOnDestroy {
            if (scope.isActive) {
                scope.cancel()
            }
        }
    }

    override fun onBackClicked() {
        output(YuliFollows.Output.Back)
    }

    override fun sortFollows(sortBy: Profile.SortBy) {
        if (sortBy != model.value.sortedBy) {
            store.accept(YuliFollowsStore.Intent.Sort(sortBy))
        }
    }
}