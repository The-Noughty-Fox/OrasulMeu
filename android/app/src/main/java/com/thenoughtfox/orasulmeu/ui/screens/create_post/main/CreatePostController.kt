package com.thenoughtfox.orasulmeu.ui.screens.create_post.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.thenoughtfox.orasulmeu.navigation.CreatePostDestinations
import com.thenoughtfox.orasulmeu.navigation.LocalCreatePostNavigator
import com.thenoughtfox.orasulmeu.navigation.LocalRootNavigator
import com.thenoughtfox.orasulmeu.navigation.RootNavDestinations
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun CreatePostController(viewModel: CreatePostViewModel, sharedViewModel: SharedViewModel) {

    val scope = rememberCoroutineScope()
    val navController = LocalCreatePostNavigator.current
    val uiState by viewModel.state.collectAsState()
    val rootNavigator = LocalRootNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    CreatePostContract.Action.GoMain -> {
                        sharedViewModel.sendEvent(Event.CreatePost)
                        rootNavigator.navigate(RootNavDestinations.Main) {
                            popUpTo(RootNavDestinations.Main) {
                                inclusive = true
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    CreatePostPage(
        uiState = uiState,
        onSendEvent = { scope.launch { viewModel.event.send(it) } },
        sendNavEvent = {
            when (it) {
                NavEvent.GoBack -> navController.navigateUp()
                NavEvent.GoToMapSearch -> navController.navigate(CreatePostDestinations.MapSearchScreen)
                NavEvent.GoToMedia -> navController.popBackStack()
                else -> Unit
            }
        }
    )
}