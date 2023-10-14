import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.github.krisbitney.yuli.App
import io.github.krisbitney.yuli.api.SocialApiFactory
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.repository.BackgroundTaskLauncher
import io.github.krisbitney.yuli.state.YuliRootComponent
import platform.UIKit.UIViewController

@OptIn(ExperimentalStdlibApi::class)
fun MainViewController(): UIViewController {
    BackgroundTaskLauncher.registerTasks()

    // prepare root component
    val lifecycle = LifecycleRegistry()
    val db = YuliDatabase()
    val api = SocialApiFactory.get(null)
    val rootComponent = YuliRootComponent(
        componentContext = DefaultComponentContext(lifecycle),
        storeFactory = DefaultStoreFactory(),
        database = db,
        apiHandler = ApiHandler(api, db)
    )
    return ComposeUIViewController { App(rootComponent) }
}