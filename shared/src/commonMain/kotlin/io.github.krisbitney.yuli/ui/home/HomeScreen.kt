package io.github.krisbitney.yuli.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.krisbitney.yuli.state.home.YuliHome
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HomeScreen(component: YuliHome) {
    val model = component.model.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            UserHeadline(
                fullName = "Kris Bitney",
                username = "krisbitney",
                followersCount = 0,
                profilePictureUrl = "https://pbs.twimg.com/profile_images/1441195726437872640/6Z6Z1Z3-_400x400.jpg"
            )
            Column(Modifier.fillMaxWidth()) {
                GroupCard("Mutuals", 0)
                GroupCard("Non-followers", 0)
                GroupCard("Fans", 0)
                GroupCard("Former Connections", 0)
            }
            HistoryCard()
        }
    }
}
