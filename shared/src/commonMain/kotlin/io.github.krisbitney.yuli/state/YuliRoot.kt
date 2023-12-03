package io.github.krisbitney.yuli.state

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import io.github.krisbitney.yuli.state.follows.YuliFollows
import io.github.krisbitney.yuli.state.history.YuliHistory
import io.github.krisbitney.yuli.state.home.YuliHome
import io.github.krisbitney.yuli.state.login.YuliLogin
import io.github.krisbitney.yuli.state.settings.YuliSettings

interface YuliRoot {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Home(val component: YuliHome) : Child()
        data class Login(val component: YuliLogin) : Child()
        data class Follows(val component: YuliFollows) : Child()
        data class History(val component: YuliHistory) : Child()
        data class Settings(val component: YuliSettings) : Child()
    }
}