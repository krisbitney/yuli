package io.github.krisbitney.yuli.ui.follows

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.resources.MR
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SortToggle(sortedBy: Profile.SortBy, onToggle: (Profile.SortBy) -> Unit) {
        Row(
            modifier = Modifier.wrapContentHeight().width(164.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${stringResource(MR.strings.sort)}: ",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconToggleButton(
                modifier = Modifier.wrapContentSize(),
                checked = sortedBy == Profile.SortBy.USERNAME,
                onCheckedChange = {
                    when (it) {
                        true -> onToggle(Profile.SortBy.USERNAME)
                        false -> onToggle(Profile.SortBy.NAME)
                    }
                }
            ) {
                val icon = if (sortedBy == Profile.SortBy.USERNAME) {
                    "toggle_on.xml"
                } else {
                    "toggle_off.xml"
                }
                Image(
                    painter = painterResource(icon),
                    contentDescription = "Toggle sort",
                    modifier = Modifier.size(64.dp),
                )
            }
            Text(
                text = if (sortedBy == Profile.SortBy.USERNAME) {
                    stringResource(MR.strings.username)
                } else {
                    stringResource(MR.strings.name)
                },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
}