package com.thenoughtfox.orasulmeu.ui.screens.home.post_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun PostListController(viewModel: HomeViewModel) {
    val scope = rememberCoroutineScope()
    PostListScreen(
        state = viewModel.state.collectAsState().value,
        sendEvent = { scope.launch { viewModel.sendEvent(it) } }
    )
}