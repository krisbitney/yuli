package io.github.krisbitney.yuli.ui.follows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.state.follows.YuliFollows

@Composable
fun FollowsScreen(component: YuliFollows) {
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = component::onBackClicked) {
                    Text(text = "<-")
                }
                OutlinedIconToggleButton(
                    modifier = Modifier.wrapContentSize(),
                    checked = model.value.sortedBy == Profile.SortBy.USERNAME,
                    onCheckedChange = {
                        when (it) {
                            true -> component.sortFollows(Profile.SortBy.USERNAME)
                            false -> component.sortFollows(Profile.SortBy.NAME)
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = "Sorted by ${model.value.sortedBy}"
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                items(model.value.follows) { follow ->
                    Text(text = follow.toString())
                }
            }
        }
    }
}
