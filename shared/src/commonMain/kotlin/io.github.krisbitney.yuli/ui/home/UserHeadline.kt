package io.github.krisbitney.yuli.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.ui.utils.toPainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@ExperimentalResourceApi
@Composable
fun UserHeadline(
    fullName: String,
    username: String,
    pic: List<Byte>?
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(250.dp).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        flowerColumn(fullName, "flower_1.png", "flower icon")
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Yuli",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(168.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(percent = 50)
                    )
            ) {
                Image(
                    painter = pic?.toPainter() ?: painterResource("avatar_placeholder.png"),
                    contentDescription = "User avatar",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(164.dp).clip(RoundedCornerShape(percent = 50))
                )
            }
        }
        flowerColumn("@$username", "flower_2.png", "flower icon")
    }
}
