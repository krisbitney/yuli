import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        BackgroundTaskLauncher().requestNotificationPermissions()
        BackgroundTaskLauncher().registerTasks()
        // disabled for now, because scheduled background updates don't survive app close
        // BackgroundTaskLauncher().scheduleUpdateFollows(null)
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
