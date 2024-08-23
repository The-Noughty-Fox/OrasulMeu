package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileController
import com.thenoughtfox.orasulmeu.ui.screens.profile_settings.ProfileSettingsController
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedViewModel
import kotlinx.serialization.Serializable

interface ProfileDestinations {
    @Serializable
    data object UserProfile

    @Serializable
    data object ProfileSettings
}

@Composable
fun ProfileGraph(sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalProfileNavigator provides navController) {
        NavHost(navController = navController, startDestination = ProfileDestinations.UserProfile) {
            composable<ProfileDestinations.UserProfile> {
                ProfileController(sharedViewModel)
            }

            composable<ProfileDestinations.ProfileSettings> {
                ProfileSettingsController()
            }
        }
    }
}

val LocalProfileNavigator =
    staticCompositionLocalOf<NavHostController> { error("Profile navigator wasn't initialized!") }