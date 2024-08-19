package com.thenoughtfox.orasulmeu.ui.screens.home.post_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.thenoughtfox.orasulmeu.navigation.LocalMainNavigator
import com.thenoughtfox.orasulmeu.navigation.MainGraphDestinations
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun PostListController(viewModel: HomeViewModel) {

    val scope = rememberCoroutineScope()
    val navigator = LocalMainNavigator.current

    PostListScreen(
        newPosts = viewModel.newPostsPager.collectAsLazyPagingItems(),
        popularPosts = viewModel.popularPostsPager.collectAsLazyPagingItems(),
        state = viewModel.state.collectAsState().value,
        sendEvent = { scope.launch { viewModel.sendEvent(it) } },
        onSearchClick = {
            navigator.navigate(MainGraphDestinations.SearchPostsScreen)
        }
    )
}