package com.thenoughtfox.orasulmeu.ui.screens.profile_settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.thenoughtfox.orasulmeu.navigation.LocalProfileNavigator
import com.thenoughtfox.orasulmeu.navigation.LocalRootNavigator
import com.thenoughtfox.orasulmeu.navigation.RootNavDestinations
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ProfileSettingsController() {
    val viewModel: ProfileSettingsViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val rootNavigator = LocalRootNavigator.current
    val profileNavigator = LocalProfileNavigator.current

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    is ProfileSettingsContract.Action.ShowToast -> context.showToast(action.msg)
                    ProfileSettingsContract.Action.Logout -> {
                        rootNavigator.navigate(RootNavDestinations.Auth) {
                            popUpTo(RootNavDestinations.Main) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }

    ProfileSettingsPage(
        onSendEvent = { scope.launch { viewModel.event.send(it) } },
        sendNavEvent = { event ->
            when (event) {
                ProfileSettingsContract.NavEvent.GoBack -> profileNavigator.navigateUp()
            }
        }
    )
}