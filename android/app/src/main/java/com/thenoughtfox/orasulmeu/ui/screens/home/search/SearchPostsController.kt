package com.thenoughtfox.orasulmeu.ui.screens.home.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.thenoughtfox.orasulmeu.navigation.LocalMainNavigator
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchPostsController() {

    val viewModel: HomeViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val navigator = LocalMainNavigator.current

    SearchPostsScreen(
        sendEvent = {
            scope.launch { viewModel.sendEvent(it) }
        },
        sendNavEvent = { event ->
            when (event) {
                NavEvent.GoBack -> navigator.navigateUp()
            }
        },
        searchPosts = viewModel.searchPostsPager.collectAsLazyPagingItems()
    )
}