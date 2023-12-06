import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.lifecycle.ApplicationLifecycle
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.github.krisbitney.yuli.App
import io.github.krisbitney.yuli.api.SocialApiFactory
import io.github.krisbitney.yuli.database.YuliDatabase
import io.github.krisbitney.yuli.repository.ApiHandler
import io.github.krisbitney.yuli.state.YuliRootComponent
import platform.UIKit.UIViewController

@OptIn(ExperimentalStdlibApi::class, ExperimentalDecomposeApi::class)
fun MainViewController(): UIViewController {
    val lifecycle = ApplicationLifecycle()
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
