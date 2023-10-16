package io.github.krisbitney.yuli.state.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> StateFlow<T>.map(
    scope: CoroutineScope,
    transform: (T) -> R
): StateFlow<R> {
    val mappedStateFlow = MutableStateFlow(transform(this.value))
    scope.launch {
        this@map.mapLatest(transform).collect {
            mappedStateFlow.value = it
        }
    }
    return mappedStateFlow
}
