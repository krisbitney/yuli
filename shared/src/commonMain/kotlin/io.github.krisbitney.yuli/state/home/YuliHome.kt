package io.github.krisbitney.yuli.state.home

import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.models.FollowType
import kotlinx.coroutines.flow.StateFlow

interface YuliHome {
    val model: StateFlow<Model>

    fun onFollowsClicked(type: FollowType)

    fun onLoginClicked()

    fun onRefreshClicked()

    data class Model(
        val user: User?,
        val mutualsCount: Long,
        val nonfollowersCount: Long,
        val fansCount: Long,
        val formerConnectionsCount: Long,
        val updateInProgress: Boolean,
    )

    sealed class Output {
        data object Login : Output()
        data class Follows(val type: FollowType) : Output()
    }
}