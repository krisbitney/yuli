package io.github.krisbitney.yuli.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.state.home.YuliHome
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HomeScreen(component: YuliHome) {
    val model = component.model.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            if (model.value.user != null) {
                UserHeadline(
                    fullName = model.value.user?.name ?: "",
                    username = model.value.user?.username ?: "",
                    pic = model.value.user?.pic,
                    onClickRightHeaderImage = component::onRefreshClicked,
                    updateInProgress = model.value.updateInProgress
                )
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
                        Text("\uD83E\uDE77 Click here to log in. \uD83E\uDE77")
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 52.dp, topEnd = 52.dp))
                    .background(MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupButton(
                    name = "Mutuals",
                    count = model.value.mutualsCount,
                    portraitImg = "portrait_green.png",
                    flourishImg = "leaves_1.png",
                ) {
                    component.onFollowsClicked(FollowType.MUTUAL)
                }
                GroupButton(
                    name = "Non-followers",
                    count = model.value.nonfollowersCount,
                    portraitImg = "portrait_pink.png",
                    flourishImg = "leaves_1.png",
                ) {
                    component.onFollowsClicked(FollowType.NONFOLLOWER)
                }
                GroupButton(
                    name = "Fans",
                    count = model.value.fansCount,
                    portraitImg = "portrait_green.png",
                    flourishImg = "leaves_2.png",
                ) {
                    component.onFollowsClicked(FollowType.FAN)
                }
                GroupButton(
                    name = "Former Connections",
                    count = model.value.formerConnectionsCount,
                    portraitImg = "portrait_pink.png",
                    flourishImg = "leaves_1.png",
                ) {
                    component.onFollowsClicked(FollowType.FORMER)
                }
                Button(
                    onClick = component::onHistoryClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp
                    ),
                    modifier = Modifier
                        .padding(start = 64.dp, end = 64.dp, top = 24.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Text(
                        "History",
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
                Button(
                    onClick = component::onSettingsClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp
                    ),
                    modifier = Modifier
                        .padding(start = 64.dp, end = 64.dp, top = 24.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            }
        }
    }
}
