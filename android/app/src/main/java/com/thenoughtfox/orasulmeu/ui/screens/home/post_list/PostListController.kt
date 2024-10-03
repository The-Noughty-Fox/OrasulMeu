package com.thenoughtfox.orasulmeu.ui.screens.home.post_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.navigation.LocalMainNavigator
import com.thenoughtfox.orasulmeu.navigation.LocalRootNavigator
import com.thenoughtfox.orasulmeu.navigation.MainGraphDestinations
import com.thenoughtfox.orasulmeu.navigation.RootNavDestinations
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeViewModel
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun PostListController(viewModel: HomeViewModel, sharedViewModel: SharedViewModel) {

    val scope = rememberCoroutineScope()
    val mainNavigator = LocalMainNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val rootNavigator = LocalRootNavigator.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    HomeContract.Action.GoToAuth -> {
                        rootNavigator.navigate(
                            RootNavDestinations.AnonymousDialog(
                                context.getString(R.string.auth_screen_desc_react)
                            )
                        )
                    }

                    else -> Unit
                }
            }
        }
    }


    PostListScreen(
        newPosts = viewModel.newPostsPager.collectAsLazyPagingItems(),
        popularPosts = viewModel.popularPostsPager.collectAsLazyPagingItems(),
        state = viewModel.state.collectAsState().value,
        sendEvent = { scope.launch { viewModel.sendEvent(it) } },
        onSearchClick = {
            mainNavigator.navigate(
                MainGraphDestinations.SearchPostsScreen(
                    isAnonymous = sharedViewModel.state.value.isAnonymous
                )
            )
        }
    )
}