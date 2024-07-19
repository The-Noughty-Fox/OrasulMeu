package com.thenoughtfox.orasulmeu.ui.screens.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import com.thenoughtfox.orasulmeu.ui.post.PostView
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toState
import com.thenoughtfox.orasulmeu.ui.post.utils.PostPreviewPlaceholders
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme

@Composable
fun SearchPostsScreen(
    state: HomeContract.State,
    sendEvent: (HomeContract.Event) -> Unit,
    onBackPress: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = { SearchTopBar(onBackPress = onBackPress) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = OrasulMeuTheme.colors.backgroundGrey)
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
                    if (state.searchResult.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = stringResource(R.string.search_posts_no_results_message),
                                modifier = Modifier.align(Alignment.Center),
                                style = TextStyle(
                                    fontWeight = FontWeight(400),
                                    fontSize = 20.sp,
                                    color = OrasulMeuTheme.colors.onBackground
                                )
                            )
                        }
                    } else {
                        LazyColumn {
                            items(state.searchResult) {
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
    var state by remember { mutableStateOf(HomeContract.State()) }

    val onSearch: (String) -> Unit = { searchText ->
        val posts = PostPreviewPlaceholders.dummyPosts

        if (searchText.isEmpty()) {
            state = state.copy(searchResult = emptyList())
        }

        val filteredPosts = posts.filter {
            it.title.contains(searchText, ignoreCase = true)
                    || it.content.contains(searchText, ignoreCase = true)
        }

        state = state.copy(searchResult = filteredPosts)
    }

    SearchPostsScreen(
        state = state,
        sendEvent = {
            if (it is HomeContract.Event.SearchPostWithText) {
                onSearch(it.searchText)
            }
        }
    )
}