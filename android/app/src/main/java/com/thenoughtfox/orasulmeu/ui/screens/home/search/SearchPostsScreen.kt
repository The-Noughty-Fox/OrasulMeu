package com.thenoughtfox.orasulmeu.ui.screens.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import com.thenoughtfox.orasulmeu.ui.post.PostView
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toState
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.State
import com.thenoughtfox.orasulmeu.ui.screens.home.PostError
import com.thenoughtfox.orasulmeu.ui.screens.home.PostLoading
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun SearchPostsScreen(
    state: State = State(),
    sendEvent: (HomeContract.Event) -> Unit = {},
    sendNavEvent: (HomeContract.NavEvent) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(color = colorResource(R.color.background_color)),
        topBar = {
            SearchTopBar(onBackPress = {
                sendNavEvent(HomeContract.NavEvent.GoBack)
            })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                var searchText by remember { mutableStateOf("") }
                SearchBarView(
                    searchText = searchText,
                    onValueChanged = {
                        searchText = it
                        sendEvent(HomeContract.Event.SearchPostWithText(it))
                    },
                    onClear = {
                        searchText = ""
                        sendEvent(HomeContract.Event.SearchPostWithText(""))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                if (searchText.isNotEmpty()) {
                    val posts = state.searchResult.collectAsLazyPagingItems()

                    when (posts.loadState.refresh) {
                        is LoadState.Loading -> {
                            PostLoading(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(padding)
                            )
                        }

                        else -> {
                            if (posts.itemCount == 0) {
                                PostError(modifier = Modifier.fillMaxSize())
                                return@Column
                            }

                            LazyColumn {
                                items(
                                    posts.itemCount,
                                    key = posts.itemKey { it.id }
                                ) { index ->
                                    val post = posts[index]
                                    // Display the message or a placeholder.
                                    if (post != null) {
                                        PostView(state = post.toState()) { e ->
                                            when (e) {
                                                PostContract.Action.ConfirmReport -> {
                                                    sendEvent(HomeContract.Event.SendReport(post.id))
                                                }

                                                PostContract.Action.Dislike -> {
                                                    sendEvent(HomeContract.Event.DislikePost(post.id))
                                                }

                                                PostContract.Action.Like -> {
                                                    sendEvent(HomeContract.Event.LikePost(post.id))
                                                }

                                                PostContract.Action.RevokeReaction -> {
                                                    sendEvent(HomeContract.Event.RevokeReaction(post.id))
                                                }
                                            }
                                        }
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

                                if (posts.loadState.append is LoadState.Loading) {
                                    item {
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
                    }
                }
            }
        }
    )
}

@Composable
private fun SearchTopBar(onBackPress: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        val (navIcon, title) = createRefs()

        Box(modifier = Modifier
            .constrainAs(navIcon) {
                centerVerticallyTo(parent)
                start.linkTo(parent.start, 4.dp)
            }
            .size(32.dp)
            .clip(CircleShape)
            .clickable { onBackPress() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = "search icon",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
            )
        }

        Text(
            text = stringResource(R.string.search_screen_title),
            modifier = Modifier.constrainAs(title) {
                centerTo(parent)
            },
            style = TextStyle(
                fontWeight = FontWeight(700),
                fontSize = 14.sp,
                color = OrasulMeuTheme.colors.onBackground
            )
        )
    }
}

@Composable
private fun SearchBarView(
    modifier: Modifier = Modifier,
    searchText: String,
    onValueChanged: (String) -> Unit,
    onClear: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "search",
            tint = OrasulMeuTheme.colors.onBackground,
            modifier = Modifier.size(24.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { text ->
                onValueChanged(text)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
            maxLines = 1,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_for_posts),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            ),
            modifier = Modifier.weight(1f)
        )

        if (searchText.isNotEmpty()) {
            Box(modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable { onClear() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "close",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = OrasulMeuTheme {
    SearchPostsScreen()
}