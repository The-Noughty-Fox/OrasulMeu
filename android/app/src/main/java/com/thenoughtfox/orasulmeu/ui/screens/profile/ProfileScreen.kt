package com.thenoughtfox.orasulmeu.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import com.thenoughtfox.orasulmeu.ui.post.PostScreenType
import com.thenoughtfox.orasulmeu.ui.post.PostView
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toState
import com.thenoughtfox.orasulmeu.ui.screens.home.PostLoading
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.State
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.ClickableIcon
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.ProfileView
import com.thenoughtfox.orasulmeu.ui.screens.profile.components.TopBar
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import kotlinx.coroutines.flow.flowOf
import org.openapitools.client.models.PointDto
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto
import org.openapitools.client.models.UserDto

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    state: State,
    sendEvent: (Event) -> Unit = {},
    sendNavEvent: (NavEvent) -> Unit = {},
    pickImage: () -> Unit = {},
    posts: LazyPagingItems<PostDto>,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.background_color))
            .statusBarsPadding(),
        topBar = {
            TopBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                rightItem = {
                    if (state.isEditing) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { sendEvent(Event.SaveChanges) }
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = colorResource(R.color.icons_dark_grey)
                                )
                            } else {
                                Text(text = stringResource(R.string.save))
                            }
                        }
                    } else {
                        ClickableIcon(
                            painter = painterResource(R.drawable.ic_settings),
                            color = colorResource(R.color.icons_dark_grey),
                            onClick = { sendNavEvent(NavEvent.GoToSettings) }
                        )
                    }
                },
                titleText = stringResource(R.string.my_profile_screen_title),
                leftItem = {
                    if (state.isEditing) {
                        ClickableIcon(
                            painter = painterResource(R.drawable.ic_chevron_left),
                            color = colorResource(R.color.icons_dark_grey),
                            onClick = { sendEvent(Event.DiscardChanges) }
                        )
                    }
                }
            )
        },
        content = { padding ->
            val pullRefreshState = rememberPullRefreshState(state.isRefreshing, {
                sendEvent(Event.Refresh)
            })

            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                if (posts.loadState.refresh is LoadState.Loading) {
                    PostLoading(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = colorResource(R.color.background_color))
                            .padding(top = padding.calculateTopPadding())
                    ) {
                        item {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ProfileView(
                                    name = state.name,
                                    avatarImageUrl = state.imageUrl,
                                    postCount = state.postsCount,
                                    reactionsCount = state.reactionsCount,
                                    isEditionModeEnabled = state.isEditing,
                                    isEnabled = state.isEnabledToChangeUser,
                                    onEditPress = { sendEvent(Event.EditProfile) },
                                    onNameTextChange = { sendEvent(Event.ChangeName(it)) },
                                    onChangeImagePress = { pickImage() }
                                )
                            }
                        }

                        item {
                            Text(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    bottom = 6.dp
                                ),
                                text = stringResource(id = R.string.profile_my_post),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(600),
                                    color = colorResource(R.color.black),
                                )
                            )
                        }

                        items(
                            posts.itemCount,
                            key = posts.itemKey { it.id }
                        ) { index ->
                            val post = posts[index]
                            if (post != null) {
                                PostView(state = post.toState(), type = PostScreenType.PROFILE,
                                    sendEvent = { event ->
                                        when (event) {
                                            PostContract.Event.Edit -> {
                                                sendEvent(Event.EditPost(post.id))
                                            }

                                            PostContract.Event.Delete -> {
                                                sendEvent(Event.DeletePost(post.id))
                                            }

                                            else -> Unit
                                        }
                                    })
                            } else {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    state.isRefreshing,
                    pullRefreshState,
                    Modifier.align(Alignment.TopCenter)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() = OrasulMeuTheme {
    val state = State(name = "John Doe")
    val userDto = UserDto(id = 1, email = "", username = "John Doe")
    val reactions = PostReactionsDto(dislike = 0, like = 0)
    val location = PointDto(0.0, 0.0)
    val mockPosts = listOf(
        PostDto(
            id = 1, title = "Post 1", content = "Content 1",
            userDto, reactions, Any(), emptyList(), "", "", location
        ),

        PostDto(
            id = 2, title = "Post 2", content = "Content 2",
            userDto, reactions, Any(), emptyList(), "", "", location
        ),

        PostDto(
            id = 3, title = "Post 3", content = "Content 3",
            userDto, reactions, Any(), emptyList(), "", "", location
        ),
    )

    val posts = flowOf(
        PagingData.from(
            data = mockPosts,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
            )
        )
    ).collectAsLazyPagingItems()
    ProfileScreen(state = state, posts = posts)
}