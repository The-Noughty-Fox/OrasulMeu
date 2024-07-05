package com.thenoughtfox.orasulmeu.ui.screens.profile_settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.thenoughtfox.orasulmeu.navigation.LocalProfileNavigator
import com.thenoughtfox.orasulmeu.navigation.LocalRootNavigator
import com.thenoughtfox.orasulmeu.navigation.RootNavDestinations
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ProfileSettingsController() {
    val vm: ProfileSettingsViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle

    val rootNavigator = LocalRootNavigator.current
    val profileNavigator = LocalProfileNavigator.current

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            vm.action.collectLatest {
                when (it) {
                    is ProfileSettingsContract.Action.ShowToast -> context.showToast(it.msg)
                    ProfileSettingsContract.Action.Logout -> rootNavigator.navigate(
                        RootNavDestinations.Auth
                    )
                }
            }
        }
    }

    ProfileSettingsPage(
        onSendEvent = { scope.launch { vm.event.send(it) } },
        onBackPressed = { profileNavigator.navigateUp() }
    )
}