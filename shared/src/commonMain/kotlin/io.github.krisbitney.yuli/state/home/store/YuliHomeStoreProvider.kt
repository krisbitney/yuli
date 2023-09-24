package io.github.krisbitney.yuli.state.home.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.github.krisbitney.yuli.models.User
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.Intent
import io.github.krisbitney.yuli.state.home.store.YuliHomeStore.State
import kotlinx.coroutines.flow.Flow

internal class YuliHomeStoreProvider(
    private val storeFactory: StoreFactory,
    private val database: Database
) {

    fun provide(): YuliHomeStore =
        object : YuliHomeStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "TodoListStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Msg {
        data class ItemsLoaded(val items: List<TodoItem>) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Unit, State, Nothing, Nothing>() {
        override fun executeAction(action: Unit, getState: () -> State) {
            // TODO initialize subscriptions
        }

        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.openSettings -> Unit
                is Intent.navigateTo -> Unit
            }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.ItemsLoaded -> copy(items = msg.items.sorted())
                is Msg.ItemDoneChanged -> update(id = msg.id) { copy(isDone = msg.isDone) }
            }
    }

    interface Database {
        fun selectUser(): User?

        fun countMutuals(): Flow<Long>

        fun countNonfollowers(): Flow<Long>

        fun countFans(): Flow<Long>

        fun countFormerConnections(): Flow<Long>
    }
}