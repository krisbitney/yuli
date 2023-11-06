package io.github.krisbitney.yuli.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.state.home.YuliHome
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HomeScreen(component: YuliHome) {
    val model = component.model.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
            ) {
            if (model.value.user != null) {
                UserHeadline(
                    fullName = model.value.user?.name ?: "",
                    username = model.value.user?.username ?: "",
                    pic = model.value.user?.pic
                )
                Box(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("You're amazing!");
                }
            } else {
                UserHeadline(
                    fullName = "Not Logged In",
                    username = "please_log_in",
                    pic = null
                )
                Button(onClick = component::onLoginClicked) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("\uD83E\uDE77 Click here to log in. \uD83E\uDE77");
                    }
                }
            }
            Button(onClick = { component.onFollowsClicked(FollowType.MUTUAL) }) {
                GroupCard("Mutuals", model.value.mutualsCount)
            }
            Button(onClick = { component.onFollowsClicked(FollowType.NONFOLLOWER) }) {
                GroupCard("Non-followers", model.value.nonfollowersCount)
            }
            Button(onClick = { component.onFollowsClicked(FollowType.FAN) }) {
                GroupCard("Fans", model.value.fansCount)
            }
            Button(onClick = { component.onFollowsClicked(FollowType.FORMER) }) {
                GroupCard("Former Connections", model.value.formerConnectionsCount)
            }
            HistoryCard()
        }
    }
}
