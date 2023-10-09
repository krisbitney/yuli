package io.github.krisbitney.yuli.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.ui.utils.toPainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@ExperimentalResourceApi
@Composable
fun UserHeadline(
    fullName: String,
    username: String,
    followersCount: Long,
    pic: List<Byte>?
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {

        Image(
            painter = pic?.toPainter() ?: painterResource("avatar_placeholder.png"),
            contentDescription = "User avatar",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = fullName, fontWeight = FontWeight.Bold)
                Text(text = "@$username")
            }

            Text(
                text = "Followers: $followersCount",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
