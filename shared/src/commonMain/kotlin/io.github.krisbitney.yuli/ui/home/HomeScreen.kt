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
            if (model.value.user != null) {
                UserHeadline(
                    fullName = model.value.user?.name ?: "",
                    username = model.value.user?.username ?: "",
                    followersCount = model.value.mutualsCount + model.value.fansCount,
                    pic = model.value.user?.pic
                )
            // TODO: Add login required screen
            } else {
                UserHeadline(
                    fullName = "",
                    username = "",
                    followersCount = 0,
                    pic = null
                )
            }
            Column(Modifier.fillMaxWidth()) {
                GroupCard("Mutuals", model.value.mutualsCount)
                GroupCard("Non-followers", model.value.nonfollowersCount)
                GroupCard("Fans", model.value.fansCount)
                GroupCard("Former Connections", model.value.formerConnectionsCount)
            }
            HistoryCard()
        }
    }
}
