package io.github.krisbitney.yuli.state.home

import io.github.krisbitney.yuli.models.User
import kotlinx.coroutines.flow.StateFlow

interface YuliHome {
    val model: StateFlow<Model>

    // TODO: should variables be State to avoid recomposing the whole page on change to one?
    data class Model(
        val user: User?,
        val mutualsCount: Long,
        val nonfollowersCount: Long,
        val fansCount: Long,
        val formerConnectionsCount: Long,
    )

    sealed class Output {
        data object Home : Output()
    }
}