package io.github.krisbitney.yuli.state.home.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.github.krisbitney.yuli.state.home.YuliHome
import io.github.krisbitney.yuli.state.home.YuliHome.Output
import io.github.krisbitney.yuli.state.home.YuliHome.Model
import io.github.krisbitney.yuli.state.home.store.YuliHomeStoreProvider
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore
import io.github.krisbitney.yuli.state.utils.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class YuliHomeComponent (
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    database: YuliDatabase,
    apiHandler: ApiHandler,
    private val output: (Output) -> Unit,
    isUpdating: Boolean
) : YuliHome, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        YuliHomeStoreProvider(
            storeFactory = storeFactory,
            database = YuliHomeStoreDatabase(database = database),
            apiHandler = apiHandler
        ).provide()
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    override val model: StateFlow<Model> = store.stateFlow.map(scope, stateToModel)

    init {
        lifecycle.doOnCreate {
            store.accept(YuliHomeStore.Intent.SetIsUpdating(isUpdating))
        }
        lifecycle.doOnDestroy {
            if (scope.isActive) {
                scope.cancel()
            }
        }
    }

    override fun onFollowsClicked(type: FollowType) {
        output(Output.Follows(type))
    }

    override fun onHistoryClicked() {
        output(Output.History)
    }

    override fun onLoginClicked() {
        output(Output.Login)
    }

    override fun onRefreshClicked() {
        store.accept(YuliHomeStore.Intent.RefreshFollowsData)
    }
}