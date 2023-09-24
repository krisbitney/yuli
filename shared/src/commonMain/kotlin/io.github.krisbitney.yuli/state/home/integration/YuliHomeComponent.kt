package io.github.krisbitney.yuli.state.home.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.github.krisbitney.yuli.state.home.YuliHome
import io.github.krisbitney.yuli.state.home.store.YuliHomeStoreProvider
import io.github.krisbitney.yuli.database.YuliDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class YuliHomeComponent (
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    database: YuliDatabase
) : YuliHome, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        YuliHomeStoreProvider(
            storeFactory = storeFactory,
            database = YuliHomeStoreDatabase(database = database)
        ).provide()
    }

    override val model: Flow<YuliHome.Model> = store.stateFlow.map(stateToModel)

    override fun onGroupClicked() {
        TODO("Not yet implemented")
    }
}