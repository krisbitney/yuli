package io.github.krisbitney.yuli.state

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.state.home.YuliHome
import io.github.krisbitney.yuli.state.home.integration.YuliHomeComponent
import io.github.krisbitney.yuli.state.login.YuliLogin
import io.github.krisbitney.yuli.state.login.integration.YuliLoginComponent
import kotlinx.serialization.Serializable

@OptIn(ExperimentalStdlibApi::class)
class YuliRootComponent(
    private val componentContext: ComponentContext,
    private val yuliHome: (ComponentContext, (YuliHome.Output) -> Unit) -> YuliHome,
    private val yuliLogin: (ComponentContext, (YuliLogin.Output) -> Unit) -> YuliLogin,
) : YuliRoot, ComponentContext by componentContext {

     constructor(
         componentContext: ComponentContext,
         storeFactory: StoreFactory,
         database: YuliDatabase,
         apiHandler: ApiHandler
    ) : this(
        componentContext = componentContext,
        yuliHome = { childContext, output ->
            YuliHomeComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                database = database,
                output = output
            )
        },
        yuliLogin = { childContext, output ->
            YuliLoginComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                database = database,
                apiHandler = apiHandler,
                output = output
            )
        }
    )

    private val navigation = StackNavigation<Configuration>()

    override val childStack: Value<ChildStack<*, YuliRoot.Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.Home,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(configuration: Configuration, componentContext: ComponentContext): YuliRoot.Child =
        when (configuration) {
            is Configuration.Home -> YuliRoot.Child.Home(yuliHome(componentContext, ::onHomeOutput))
            is Configuration.Login -> YuliRoot.Child.Login(yuliLogin(componentContext, ::onLoginOutput))
        }

    // TODO: Update as I add more screens
    private fun onHomeOutput(output: YuliHome.Output): Unit =
        when (output) {
            is YuliHome.Output.Home -> navigation.push(Configuration.Home)
        }

    private fun onLoginOutput(output: YuliLogin.Output): Unit =
        when (output) {
            is YuliLogin.Output.Close -> navigation.replaceCurrent(Configuration.Home)
        }

    @Serializable
    private sealed class Configuration {
        @Serializable
        data object Home : Configuration()
        @Serializable
        data object Login : Configuration()
    }
}