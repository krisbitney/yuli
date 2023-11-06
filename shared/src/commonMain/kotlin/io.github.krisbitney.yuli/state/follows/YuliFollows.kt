package io.github.krisbitney.yuli.state.follows

import io.github.krisbitney.yuli.models.Profile
import kotlinx.coroutines.flow.StateFlow

interface YuliFollows {
    val model: StateFlow<Model>

    fun sortFollows(sortBy: Profile.SortBy)

    data class Model(
        val follows: List<Profile>,
        val sortedBy: Profile.SortBy
    )

    sealed class Output {
        data object Back : Output()
    }
}