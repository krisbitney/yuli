package io.github.krisbitney.yuli.ui

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import io.github.krisbitney.yuli.state.YuliRoot
import io.github.krisbitney.yuli.ui.follows.FollowsScreen
import io.github.krisbitney.yuli.ui.history.HistoryScreen
import io.github.krisbitney.yuli.ui.home.HomeScreen
import io.github.krisbitney.yuli.ui.login.LoginScreen
import io.github.krisbitney.yuli.ui.settings.SettingsScreen

@Composable
fun RootContent(component: YuliRoot) {
    Children(
        stack = component.childStack,
        animation = stackAnimation(fade() + scale()),
    ) {
        when (val child = it.instance) {
            is YuliRoot.Child.Login -> LoginScreen(child.component)
            is YuliRoot.Child.Home -> HomeScreen(child.component)
            is YuliRoot.Child.Follows -> FollowsScreen(child.component)
            is YuliRoot.Child.History -> HistoryScreen(child.component)
            is YuliRoot.Child.Settings -> SettingsScreen(child.component)
        }
    }
}