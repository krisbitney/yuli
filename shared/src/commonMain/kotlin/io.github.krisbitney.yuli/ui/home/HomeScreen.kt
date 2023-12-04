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
import dev.icerock.moko.resources.compose.stringResource
import io.github.krisbitney.yuli.models.FollowType
import io.github.krisbitney.yuli.resources.MR
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
                    fullName = stringResource(MR.strings.not_logged_in),
                    username = stringResource(MR.strings.please_log),
                    pic = null
                )
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
                    name = stringResource(MR.strings.mutuals),
                    count = model.value.mutualsCount,
                    portraitImg = "portrait_green.png",
                    flourishImg = "leaves_1.png",
                ) {
                    component.onFollowsClicked(FollowType.MUTUAL)
                }
                GroupButton(
                    name = stringResource(MR.strings.non_followers),
                    count = model.value.nonfollowersCount,
                    portraitImg = "portrait_pink.png",
                    flourishImg = "leaves_1.png",
                ) {
                    component.onFollowsClicked(FollowType.NONFOLLOWER)
                }
                GroupButton(
                    name = stringResource(MR.strings.fans),
                    count = model.value.fansCount,
                    portraitImg = "portrait_green.png",
                    flourishImg = "leaves_2.png",
                ) {
                    component.onFollowsClicked(FollowType.FAN)
                }
                GroupButton(
                    name = stringResource(MR.strings.former_follows),
                    count = model.value.formerFollowsCount,
                    portraitImg = "portrait_pink.png",
                    flourishImg = "leaves_1.png",
                ) {
                    component.onFollowsClicked(FollowType.FORMER)
                }
                Button(
                    onClick = {
                        if (model.value.user != null) {
                            component.onHistoryClicked()
                        } else {
                            component.onLoginClicked()
                        }
                    },
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
                    if (model.value.user != null) {
                        Text(
                            stringResource(MR.strings.history),
                            style = MaterialTheme.typography.displaySmall,
                        )
                    } else {
                        Text(
                            stringResource(MR.strings.login),
                            style = MaterialTheme.typography.displaySmall,
                        )
                    }
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
                        stringResource(MR.strings.settings),
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            }
        }
    }
}
