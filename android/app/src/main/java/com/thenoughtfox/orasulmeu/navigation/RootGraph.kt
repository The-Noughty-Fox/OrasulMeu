package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.ui.screens.login.LoginController
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedViewModel
import kotlinx.serialization.Serializable

interface RootNavDestinations {
    @Serializable
    data object Auth : RootNavDestinations

    @Serializable
    data object Main : RootNavDestinations

    @Serializable
    object CreatePostScreen : RootNavDestinations
}

@Composable
fun RootGraph(startDestinations: RootNavDestinations) {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = hiltViewModel()

    CompositionLocalProvider(LocalRootNavigator provides navController) {
        NavHost(
            navController = navController,
            startDestination = startDestinations,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<RootNavDestinations.Auth> { LoginController() }
            composable<RootNavDestinations.Main> { MainGraph(sharedViewModel) }
            composable<RootNavDestinations.CreatePostScreen> { CreatePostGraph(sharedViewModel) }
        }
    }
}

val LocalRootNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }

