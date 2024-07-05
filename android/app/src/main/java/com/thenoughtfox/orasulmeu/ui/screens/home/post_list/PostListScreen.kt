package com.thenoughtfox.orasulmeu.ui.screens.home.post_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import com.thenoughtfox.orasulmeu.ui.post.PostView
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toState
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract

/**
 * @author Knurenko Bogdan 14.06.2024
 */
@Composable
fun PostListScreen(
    state: HomeContract.State,
    sendEvent: (HomeContract.Event) -> Unit
) {
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.Center),
                color = colorResource(R.color.primary),
                strokeWidth = 4.dp
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.background_color)),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.postsToShow) {
                PostView(state = it.toState()) { e ->
                    when (e) {
                        PostContract.Action.ConfirmReport -> {
                            sendEvent(HomeContract.Event.SendReport(it.id))
                        }

                        PostContract.Action.Dislike -> {
                            sendEvent(HomeContract.Event.DislikePost(it.id))
                        }

                        PostContract.Action.Like -> {
                            sendEvent(HomeContract.Event.LikePost(it.id))
                        }

                        PostContract.Action.RevokeReaction -> {
                            sendEvent(HomeContract.Event.RevokeReaction(it.id))
                        }
                    }
                }
            }
        }

        if (state.messageToShow != null) {
            AlertDialog(
                onDismissRequest = {
                    sendEvent(HomeContract.Event.CloseMessage)
                },
                confirmButton = {
                    Button(onClick = {
                        sendEvent(HomeContract.Event.CloseMessage)
                    }) {
                        Text(text = "Okay")
                    }
                },
                title = { Text(text = state.messageToShow) },
            )
        }
    }
}