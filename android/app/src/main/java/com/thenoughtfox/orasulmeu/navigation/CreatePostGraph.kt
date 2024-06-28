package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.CameraController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.map.MapSearchController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.media.CreatePostMediaController
import kotlinx.serialization.Serializable

/**
 * @author Knurenko Bogdan 28.06.2024
 */

interface CreatePostDestinations {
    @Serializable
    object CreatePostScreen

    @Serializable
    object MapSearchScreen

    @Serializable
    object MediaPostScreen

    @Serializable
    object CameraScreen
}

@Composable
fun CreatePostGraph() {
    val createPostNavController = rememberNavController()
    val createPostViewModel: CreatePostViewModel = hiltViewModel()

    CompositionLocalProvider(LocalCreatePostNavigator provides createPostNavController) {

        NavHost(
            navController = createPostNavController,
            startDestination = CreatePostDestinations.MediaPostScreen
        ) {
            composable<CreatePostDestinations.MediaPostScreen> {
                CreatePostMediaController(createPostViewModel)
            }

            composable<CreatePostDestinations.CreatePostScreen> {
                CreatePostController(createPostViewModel)
            }

            composable<CreatePostDestinations.MapSearchScreen> {
                MapSearchController(createPostViewModel)
            }

            composable<CreatePostDestinations.CameraScreen> {
                CameraController(createPostViewModel)
            }
        }
    }
}

val LocalCreatePostNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }
