package io.github.krisbitney.yuli.state

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import io.github.krisbitney.yuli.state.login.YuliLogin

interface YuliRoot {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Home(val component: YuliLogin) : Child()
    }
}