package com.thenoughtfox.orasulmeu.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.profile.components.ProfileToolbar
import com.thenoughtfox.orasulmeu.ui.profile.components.ProfileView
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import org.openapitools.client.models.PointDto
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto
import org.openapitools.client.models.UserDto

@Composable
fun ProfileScreen(
    state: ProfileContract.State,
    onSendEvent: (ProfileContract.Event) -> Unit
) {
    Scaffold(topBar = {
        ProfileToolbar(
            onBackPressed = { onSendEvent(ProfileContract.Event.OnNavigationBackPressed) },
            onSettingsPressed = { onSendEvent(ProfileContract.Event.OnSettingsPressed) }
        )
    },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(R.color.background_color))
                    .padding(padding)
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ProfileView(
                        name = state.name,
                        avatarImageUrl = state.imageUrl,
                        postCount = state.postsCount,
                        reactionsCount = state.reactionsCount
                    )
                }

                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    text = "Postările mele",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600),
                        color = colorResource(R.color.black),
                    )
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.ownedPost) {
                        // todo change it to post when branch is merged
                        Text(text = "there will be some post")
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() = OrasulMeuTheme {
    val state = ProfileContract.State(
        name = "John Doe", ownedPost = listOf(
            PostDto(
                0,
                "",
                "",
                UserDto(0, "", "", ""),
                comments = "",
                media = emptyList(),
                locationAddress = "test address",
                location = PointDto(0.0, 0.0),
                reactions = PostReactionsDto(dislike = 4, like = 13, userReaction = null)
            ),
            PostDto(
                0,
                "",
                "",
                UserDto(0, "", "", ""),
                comments = "",
                media = emptyList(),
                locationAddress = "test address",
                location = PointDto(0.0, 0.0),
                reactions = PostReactionsDto(dislike = 4, like = 13, userReaction = null)
            ),
            PostDto(
                0,
                "",
                "",
                UserDto(0, "", "", ""),
                comments = "",
                media = emptyList(),
                locationAddress = "test address",
                location = PointDto(0.0, 0.0),
                reactions = PostReactionsDto(dislike = 4, like = 13, userReaction = null)
            ),
        )
    )
    ProfileScreen(state = state, onSendEvent = {})
}