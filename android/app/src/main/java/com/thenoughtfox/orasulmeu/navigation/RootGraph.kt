package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.ui.screens.login.LoginController
import kotlinx.serialization.Serializable

interface RootNavDestinations {
    @Serializable
    data object Auth

    @Serializable
    data object Main
}

@Composable
fun RootGraph() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalRootNavigator provides navController) {
        NavHost(navController = navController, startDestination = RootNavDestinations.Main) {
            composable<RootNavDestinations.Auth> { LoginController() }
            composable<RootNavDestinations.Main> { MainGraph() }
        }
    }
}

val LocalRootNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }

