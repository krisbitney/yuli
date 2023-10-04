package io.github.krisbitney.yuli.state.login.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.github.krisbitney.yuli.state.login.YuliLogin
import io.github.krisbitney.yuli.state.login.store.YuliLoginStoreProvider
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.state.login.store.YuliLoginStore
import io.github.krisbitney.yuli.state.utils.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class YuliLoginComponent (
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    database: YuliDatabase,
    apiHandler: ApiHandler,
    output: (YuliLogin.Output) -> Unit
) : YuliLogin, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        YuliLoginStoreProvider(
            storeFactory = storeFactory,
            database = YuliLoginStoreDatabase(database = database),
            apiHandler = apiHandler
        ).provide()
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    override val model: StateFlow<YuliLogin.Model> = store.stateFlow.map(scope, stateToModel)

    init {
        lifecycle.doOnDestroy {
            if (scope.isActive) {
                scope.cancel()
            }
        }
    }

    override suspend fun onLoginClicked() {
        // TODO: Get username and password from input boxes
        store.accept(YuliLoginStore.Intent.Login("", ""))
    }
}