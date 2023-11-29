package io.github.krisbitney.yuli.state.home.integration

import com.arkivanov.decompose.ComponentContext
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class YuliHomeComponent (
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    database: YuliDatabase,
    apiHandler: ApiHandler,
    private val output: (Output) -> Unit,
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
        lifecycle.doOnDestroy {
            if (scope.isActive) {
                scope.cancel()
            }
        }
    }

    override fun onFollowsClicked(type: FollowType) {
        output(Output.Follows(type))
    }

    override fun onLoginClicked() {
        output(Output.Login)
    }

    override fun onRefreshClicked() {
        store.accept(YuliHomeStore.Intent.RefreshFollowsData)
        scope.launch {
            delay(600_000)
            if (model.value.updateInProgress) {
                withContext(Dispatchers.Main) {
                    store.accept(YuliHomeStore.Intent.SetUpdateInProgress(false))
                }
            }
        }
    }
}