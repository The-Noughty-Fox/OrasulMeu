package com.thenoughtfox.orasulmeu.ui.screens.create_post.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.thenoughtfox.orasulmeu.navigation.CreatePostDestinations
import com.thenoughtfox.orasulmeu.navigation.LocalCreatePostNavigator
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import kotlinx.coroutines.launch

/**
 * @author Knurenko Bogdan 07.06.2024
 */

@Composable
fun CreatePostController(viewModel: CreatePostViewModel) {
    val scope = rememberCoroutineScope()
    val navController = LocalCreatePostNavigator.current
    val uiState by viewModel.state.collectAsState()
    CreatePostPage(
        uiState = uiState,
        onSendEvent = { scope.launch { viewModel.event.send(it) } },
        sendNavEvent = {
            when (it) {
                NavEvent.GoBack -> navController.navigateUp()
                NavEvent.GoToMapSearch -> navController.navigate(CreatePostDestinations.MapSearchScreen)
                NavEvent.GoToMedia -> navController.navigateUp()
                else -> Unit
            }
        }
    )
}