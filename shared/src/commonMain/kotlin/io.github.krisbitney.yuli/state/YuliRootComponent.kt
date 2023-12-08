package io.github.krisbitney.yuli.state

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.state.follows.YuliFollows
import io.github.krisbitney.yuli.state.follows.integration.YuliFollowsComponent
import io.github.krisbitney.yuli.state.history.YuliHistory
import io.github.krisbitney.yuli.state.history.integration.YuliHistoryComponent
import io.github.krisbitney.yuli.state.home.YuliHome
import io.github.krisbitney.yuli.state.home.integration.YuliHomeComponent
import io.github.krisbitney.yuli.state.login.YuliLogin
import io.github.krisbitney.yuli.state.login.integration.YuliLoginComponent
import io.github.krisbitney.yuli.state.settings.YuliSettings
import io.github.krisbitney.yuli.state.settings.integration.YuliSettingsComponent
import kotlinx.serialization.Serializable

@OptIn(ExperimentalStdlibApi::class)
class YuliRootComponent(
    private val componentContext: ComponentContext,
    private val yuliHome: (ComponentContext, (YuliHome.Output) -> Unit, Boolean) -> YuliHome,
    private val yuliLogin: (ComponentContext, (YuliLogin.Output) -> Unit) -> YuliLogin,
    private val yuliFollows: (ComponentContext, (YuliFollows.Output) -> Unit, FollowType) -> YuliFollows,
    private val yuliHistory: (ComponentContext, (YuliHistory.Output) -> Unit) -> YuliHistory,
    private val yuliSettings: (ComponentContext, (YuliSettings.Output) -> Unit) -> YuliSettings,
) : YuliRoot, ComponentContext by componentContext {

     constructor(
         componentContext: ComponentContext,
         storeFactory: StoreFactory,
         database: YuliDatabase,
         apiHandler: ApiHandler
    ) : this(
        componentContext = componentContext,
        yuliHome = { childContext, output, isUpdating ->
            YuliHomeComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                database = database,
                apiHandler = apiHandler,
                output = output,
                isUpdating = isUpdating
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
        },
        yuliFollows = { childContext, output, type ->
            YuliFollowsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                database = database,
                output = output,
                type = type,
            )
        },
        yuliHistory = { childContext, output ->
            YuliHistoryComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                database = database,
                output = output
            )
        },
        yuliSettings = { childContext, output ->
            YuliSettingsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                output = output
            )
        }
    )

    private val navigation = StackNavigation<Configuration>()

    override val childStack: Value<ChildStack<*, YuliRoot.Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.Login,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(configuration: Configuration, componentContext: ComponentContext): YuliRoot.Child =
        when (configuration) {
            is Configuration.Home -> YuliRoot.Child.Home(
                yuliHome(componentContext, ::onHomeOutput, configuration.isUpdating)
            )
            is Configuration.Login -> YuliRoot.Child.Login(yuliLogin(componentContext, ::onLoginOutput))
            is Configuration.Follows -> YuliRoot.Child.Follows(
                yuliFollows(componentContext, ::onFollowsOutput, configuration.type)
            )
            is Configuration.History -> YuliRoot.Child.History(yuliHistory(componentContext, ::onHistoryOutput))
            is Configuration.Settings -> YuliRoot.Child.Settings(yuliSettings(componentContext, ::onSettingsOutput))
        }

    private fun onHomeOutput(output: YuliHome.Output): Unit =
        when (output) {
            is YuliHome.Output.Login -> navigation.replaceCurrent(Configuration.Login)
            is YuliHome.Output.Follows -> navigation.push(Configuration.Follows(type = output.type))
            is YuliHome.Output.History -> navigation.push(Configuration.History)
            is YuliHome.Output.Settings -> navigation.push(Configuration.Settings)
        }

    private fun onLoginOutput(output: YuliLogin.Output): Unit =
        when (output) {
            is YuliLogin.Output.Close -> navigation.replaceCurrent(Configuration.Home(output.isUpdating))
        }

    private fun onFollowsOutput(output: YuliFollows.Output): Unit =
        when (output) {
            is YuliFollows.Output.Back -> navigation.pop()
        }

    private fun onHistoryOutput(output: YuliHistory.Output): Unit =
        when (output) {
            is YuliHistory.Output.Back -> navigation.pop()
        }

    private fun onSettingsOutput(output: YuliSettings.Output): Unit =
        when (output) {
            is YuliSettings.Output.Back -> navigation.pop()
        }

    @Serializable
    private sealed class Configuration {
        @Serializable
        data class Home(val isUpdating: Boolean) : Configuration()
        @Serializable
        data object Login : Configuration()
        @Serializable
        data class Follows(val type: FollowType) : Configuration()
        @Serializable
        data object History : Configuration()
        @Serializable
        data object Settings : Configuration()
    }
}