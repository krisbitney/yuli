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
    pic: List<Byte>?
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(250.dp).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        flowerColumn(fullName, "flower_1.png", "flower icon")
        TitleColumn(pic, Modifier.fillMaxHeight(), MaterialTheme.colorScheme.onBackground)
        flowerColumn("@$username", "flower_2.png", "flower icon")
    }
}
