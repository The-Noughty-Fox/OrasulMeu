package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.CameraController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.map.MapSearchController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.media.CreatePostMediaController
import com.thenoughtfox.orasulmeu.ui.screens.login.LoginController
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeController
import kotlinx.serialization.Serializable

interface NavDestinations {

    @Serializable
    data object Home

    @Serializable
    object MediaPostScreen

    @Serializable
    object CreatePostScreen

    @Serializable
    object CameraScreen

    @Serializable
    object MapSearchScreen

    @Serializable
    object PostListScreen

    @Serializable
    data class ProfileScreen(val id: Int)

    @Serializable
    object AuthScreen
}

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavigator provides navController) {
        val createPostViewModel = hiltViewModel<CreatePostViewModel, CreatePostViewModel.Factory> { it.create(navController) }

        NavHost(
            navController = navController,
            startDestination = NavDestinations.Home
        ) {
            composable<NavDestinations.Home> {
                HomeController()
            }

            composable<NavDestinations.AuthScreen> {
                LoginController()
            }

            composable<NavDestinations.PostListScreen> {
                Column {
                    List<Int>(24) { it }.map { Text(text = "Some future post $it") }
                }
            }

            composable<NavDestinations.CreatePostScreen> {
                CreatePostController(createPostViewModel)
            }

            composable<NavDestinations.MapSearchScreen> {
                MapSearchController(createPostViewModel)
            }

            composable<NavDestinations.MediaPostScreen> {
                CreatePostMediaController(createPostViewModel)
            }

            composable<NavDestinations.CameraScreen> {
                CameraController(createPostViewModel)
            }
        }
    }

}

val LocalNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }
