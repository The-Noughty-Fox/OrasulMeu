package com.thenoughtfox.orasulmeu.ui.screens.home.post_list

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import com.thenoughtfox.orasulmeu.ui.post.PostView
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toState
import com.thenoughtfox.orasulmeu.ui.post.utils.PostPreviewPlaceholders
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.PostListSorting
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun PostListScreen(
    state: HomeContract.State,
    sendEvent: (HomeContract.Event) -> Unit,
    onSearchClick: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopBar(
                currentSorting = state.postListSorting,
                onChangeSorting = { sendEvent(HomeContract.Event.SelectListSorting(it)) },
                onSearchClick = onSearchClick
            )
        },
        content = { padding ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(128.dp)
                            .align(Alignment.Center),
                        color = colorResource(R.color.primary),
                        strokeWidth = 4.dp
                    )
                }
            } else {
                val popularListState = rememberLazyListState()
                val newListState = rememberLazyListState()
                val scrollState by rememberUpdatedState(
                    newValue = if (state.postListSorting == PostListSorting.Popular) {
                        popularListState
                    } else {
                        newListState
                    }
                )

                val posts = if (state.postListSorting == PostListSorting.Popular) {
                    state.paginationPopularPosts.collectAsLazyPagingItems()
                } else {
                    state.paginationNewPosts.collectAsLazyPagingItems()
                }

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .padding(top = padding.calculateTopPadding())
                        .background(color = colorResource(R.color.background_color)),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        posts.itemCount,
                        key = posts.itemKey { it.id }
                        //Fetch the message at the specific index from lazyPagingItems.
                    ) { index ->
                        val post = posts[index]
                        // Display the message or a placeholder.
                        if (post != null) {
                            PostView(
                                state = post.toState(),
                                modifier = if (index == posts.itemCount - 1) {
                                    Modifier.padding(bottom = 80.dp)
                                } else {
                                    Modifier
                                }
                            ) { action ->
                                when (action) {
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
    )
}

@Composable
private fun TopBar(
    currentSorting: PostListSorting,
    onChangeSorting: (PostListSorting) -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        val (switch, search) = createRefs()

        TextSwitch(
            modifier = Modifier.constrainAs(switch) {
                centerTo(parent)
                width = Dimension.value(256.dp)
            },
            selectedIndex = currentSorting.ordinal,
            items = listOf(
                PostListSorting.Popular,
                PostListSorting.New
            ),
            onSelectionChange = {
                val newSorting = PostListSorting.entries[it]
                onChangeSorting(newSorting)
            }
        )

        Box(modifier = Modifier
            .constrainAs(search) {
                start.linkTo(switch.end, 16.dp)
                centerVerticallyTo(parent)
            }
            .clip(CircleShape)
            .size(32.dp)
            .clickable { onSearchClick() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "search",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
private fun TextSwitch(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    items: List<PostListSorting>,
    onSelectionChange: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xfff3f3f2))
            .padding(2.dp)
    ) {
        if (items.isNotEmpty()) {

            val maxWidth = this.maxWidth
            val tabWidth = maxWidth / items.size

            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "indicator offset"
            )

            // This is for shadow layer matching white background
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(tabWidth)
                    .wrapContentHeight()
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    // This is for setting black tex while drawing on white background
                    val padding = 4.dp.toPx()
                    drawRoundRect(
                        topLeft = Offset(x = indicatorOffset.toPx() + padding, padding),
                        size = Size(size.width / 2 - padding * 2, size.height - padding * 2),
                        color = Color.Black,
                        cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx()),
                    )

                    drawWithLayer {
                        drawContent()
                        // This is white top rounded rectangle
                        drawRoundRect(
                            topLeft = Offset(x = indicatorOffset.toPx(), 0f),
                            size = Size(size.width / 2, size.height),
                            color = Color.White,
                            cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx()),
                            blendMode = BlendMode.SrcOut
                        )

                    }
                }
            ) {
                items.forEachIndexed { index, sortingType ->
                    Box(
                        modifier = Modifier
                            .width(tabWidth)
                            .wrapContentHeight()
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null,
                                onClick = {
                                    onSelectionChange(index)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = when (sortingType) {
                                PostListSorting.Popular -> stringResource(R.string.post_list_sort_popular)
                                PostListSorting.New -> stringResource(R.string.post_list_sort_new)
                            },
                            fontSize = 13.sp,
                            color = OrasulMeuTheme.colors.onBackground,
                            fontWeight = FontWeight(600)
                        )
                    }
                }
            }
        }
    }
}


private fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

@Preview
@Composable
private fun Preview() = OrasulMeuTheme {
    var state by remember {
        mutableStateOf(
            HomeContract.State(
                popularPosts = PostPreviewPlaceholders.dummyPosts
            )
        )
    }

    PostListScreen(state = state, sendEvent = {
        if (it is HomeContract.Event.SelectListSorting) {
            state = state.copy(postListSorting = it.sortType)
        }
    })
}