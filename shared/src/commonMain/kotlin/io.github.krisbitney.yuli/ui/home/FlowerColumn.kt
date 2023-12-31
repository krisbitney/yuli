package io.github.krisbitney.yuli.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun flowerColumn(
    text: String,
    flowerImage: String,
    headerImage: String,
    headerImageDescription: String? = null,
    onClickHeaderImage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxHeight().width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(headerImage),
            contentDescription = headerImageDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(48.dp).clickable { onClickHeaderImage() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(flowerImage),
            contentDescription = "flower image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}