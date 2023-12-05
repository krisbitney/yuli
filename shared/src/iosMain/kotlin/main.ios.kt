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
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController

@OptIn(ExperimentalStdlibApi::class)
fun MainViewController(): UIViewController {
    BackgroundTaskLauncher.requestNotificationPermissions()
    BackgroundTaskLauncher.registerTasks()

    // disabled for now, as it does not persist through app close
//    BackgroundTaskLauncher.scheduleUpdateFollows(null)

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

//    return LifecycleManagingViewController()
}

@OptIn(ExperimentalStdlibApi::class)
class LifecycleManagingViewController : UIViewController(null, null) {

    lateinit var db: YuliDatabase

    override fun viewDidLoad() {
        super.viewDidLoad()

        // create app
        val lifecycle = LifecycleRegistry()
        db = YuliDatabase()
        val api = SocialApiFactory.get(null)
        val rootComponent = YuliRootComponent(
            componentContext = DefaultComponentContext(lifecycle),
            storeFactory = DefaultStoreFactory(),
            database = db,
            apiHandler = ApiHandler(api, db)
        )
        val app = ComposeUIViewController { App(rootComponent) }

        // handle lifecycle delegation
        addChildViewController(app)
        view.addSubview(app.view)
        app.didMoveToParentViewController(this)
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        db.close()
    }
}
