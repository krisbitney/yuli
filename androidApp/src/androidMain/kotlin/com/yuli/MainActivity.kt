package com.yuli

import MainView
import android.os.Build.*
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.github.krisbitney.yuli.api.SocialApiFactory
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.repository.BackgroundTaskLauncher
import io.github.krisbitney.yuli.state.YuliRootComponent

@OptIn(ExperimentalStdlibApi::class)
class MainActivity : AppCompatActivity() {

    lateinit var db: YuliDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = YuliDatabase()
        val api = SocialApiFactory.get(this)
        val rootComponent = YuliRootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = DefaultStoreFactory(),
            database = db,
            apiHandler = ApiHandler(api, db)
        )

        BackgroundTaskLauncher.scheduleUpdateFollows(applicationContext)

        setContent {
            MainView(rootComponent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}