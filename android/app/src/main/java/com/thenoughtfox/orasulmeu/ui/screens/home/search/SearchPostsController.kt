package com.thenoughtfox.orasulmeu.ui.screens.home.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thenoughtfox.orasulmeu.navigation.LocalMainNavigator
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchPostsController() {

    val homeViewModel: HomeViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val navigator = LocalMainNavigator.current

    SearchPostsScreen(
        state = homeViewModel.state.collectAsStateWithLifecycle().value,
        sendEvent = {
            scope.launch { homeViewModel.sendEvent(it) }
        },
        sendNavEvent = { event ->
            when (event) {
                NavEvent.GoBack -> navigator.navigateUp()
            }
        }
    )
}