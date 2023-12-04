package io.github.krisbitney.yuli.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import dev.icerock.moko.resources.compose.stringResource
import io.github.krisbitney.yuli.resources.MR
import io.github.krisbitney.yuli.ui.utils.toPainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TitleColumn(pic: List<Byte>? = null, modifier: Modifier, titleColor: Color) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(MR.strings.app_title),
            style = MaterialTheme.typography.displayLarge,
            color = titleColor,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if (pic == null) 164.dp else 168.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(color = MaterialTheme.colorScheme.onBackground)
        ) {
            Image(
                painter = pic?.toPainter() ?: painterResource("avatar_placeholder.png"),
                contentDescription = "User avatar",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(164.dp).clip(RoundedCornerShape(percent = 50))
            )
        }
    }
}