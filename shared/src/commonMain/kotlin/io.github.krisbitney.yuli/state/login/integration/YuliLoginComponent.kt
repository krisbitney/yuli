package io.github.krisbitney.yuli.state.login.integration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class YuliLoginComponent (
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    database: YuliDatabase,
    apiHandler: ApiHandler,
    private val output: (YuliLogin.Output) -> Unit
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

    override var showWarning by mutableStateOf(false)
    override var usernameInput by mutableStateOf("")
    override var passwordInput by mutableStateOf("")

    init {
        lifecycle.doOnCreate {
            scope.launch {
                model.collect {
                    if (it.isLoggedIn) {
                        withContext(Dispatchers.Main) {
                            output(YuliLogin.Output.Close)
                        }
                    }
                }
            }
        }
        lifecycle.doOnDestroy {
            if (scope.isActive) {
                scope.cancel()
            }
        }
    }

    override fun onLoginClicked(username: String, password: String) {
        store.accept(YuliLoginStore.Intent.Login(username.trim(), password.trim()))
        // TODO: add loading indicator with a timeout that produces an error message if it takes too long
    }

    override fun onCloseClicked() {
        output(YuliLogin.Output.Close)
    }

    override fun showConfirmation() {
        showWarning = true
    }

    override fun onConfirmationClosed() {
        usernameInput = model.value.username ?: ""
        store.accept(YuliLoginStore.Intent.SetUsername(null))
        showWarning = false
    }

}