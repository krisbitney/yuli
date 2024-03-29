package io.github.krisbitney.yuli.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.ui.common.TitleColumn
import org.jetbrains.compose.resources.ExperimentalResourceApi

@ExperimentalResourceApi
@Composable
fun UserHeadline(
    fullName: String,
    username: String,
    pic: List<Byte>?,
    onClickRightHeaderImage: () -> Unit = {},
    updateInProgress: Boolean = false,
    loggedIn: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(250.dp).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        flowerColumn(
            text = fullName,
            flowerImage = "flower_1.png",
            headerImage = "pink_heart.png",
            headerImageDescription = "pink heart icon",
            onClickHeaderImage = {}
        )
        TitleColumn(pic, Modifier.fillMaxHeight(), MaterialTheme.colorScheme.onBackground)
        if (updateInProgress) {
            flowerColumn(
                text = "@$username",
                flowerImage = "flower_2.png",
                headerImage = "downloading_icon.xml",
                headerImageDescription = "downloading icon",
                onClickHeaderImage = {}
            )
        } else if (loggedIn) {
            flowerColumn(
                text = "@$username",
                flowerImage = "flower_2.png",
                headerImage = "refresh_icon.xml",
                headerImageDescription = "refresh icon",
                onClickHeaderImage = onClickRightHeaderImage
            )
        } else {
            flowerColumn(
                text = "@$username",
                flowerImage = "flower_2.png",
                headerImage = "pink_heart.png",
                headerImageDescription = "pink heart icon",
                onClickHeaderImage = {}
            )
        }
    }
}
