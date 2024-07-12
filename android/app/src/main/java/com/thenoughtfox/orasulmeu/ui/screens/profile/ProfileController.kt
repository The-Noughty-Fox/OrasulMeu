package com.thenoughtfox.orasulmeu.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.thenoughtfox.orasulmeu.navigation.LocalProfileNavigator
import com.thenoughtfox.orasulmeu.navigation.ProfileDestinations

@Composable
fun ProfileController() {
    val viewModel: ProfileViewModel = hiltViewModel()
    val profileNavController = LocalProfileNavigator.current

    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            viewModel.sendEvent(ProfileContract.Event.ChangePicture(uri))
        }

    ProfileScreen(
        state = viewModel.state.collectAsState().value,
        onSendEvent = viewModel::sendEvent,
        pickImage = { pickImageLauncher.launch("image/*") },
        sendNavAction = { profileNavController.navigate(ProfileDestinations.ProfileSettings) }
    )
}