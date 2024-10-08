package com.thenoughtfox.orasulmeu.ui.screens.create_post.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
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
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.launch

@Composable
fun CreatePostController(viewModel: CreatePostViewModel, sharedViewModel: SharedViewModel) {

    val scope = rememberCoroutineScope()
    val navController = LocalCreatePostNavigator.current
    val uiState by viewModel.state.collectAsState()
    val rootNavigator = LocalRootNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    CreatePostContract.Action.GoMain -> {
                        sharedViewModel.sendEvent(Event.UpdatePosts)
                        rootNavigator.navigate(RootNavDestinations.Main) {
                            popUpTo(RootNavDestinations.Main) {
                                inclusive = true
                            }
                        }
                    }

                    is CreatePostContract.Action.GoBackToProfile -> {
                        sharedViewModel.sendEvent(Event.UpdatePost(action.post))
                        rootNavigator.navigateUp()
                    }

                    is CreatePostContract.Action.ShowToast -> context.showToast(action.msg)
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