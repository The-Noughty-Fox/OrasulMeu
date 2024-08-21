package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.screens.create_post.main.CreatePostController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.CameraController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.map.MapSearchController
import com.thenoughtfox.orasulmeu.ui.screens.create_post.media.CreatePostMediaController
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedViewModel
import kotlinx.serialization.Serializable

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
fun CreatePostGraph(sharedViewModel: SharedViewModel) {
    val createPostNavController = rememberNavController()
    val createPostViewModel: CreatePostViewModel = hiltViewModel()

    CompositionLocalProvider(LocalCreatePostNavigator provides createPostNavController) {
        NavHost(
            navController = createPostNavController,
            startDestination = CreatePostDestinations.MediaPostScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.background_color))
                .safeDrawingPadding()
        ) {
            composable<CreatePostDestinations.MediaPostScreen> {
                CreatePostMediaController(createPostViewModel)
            }

            composable<CreatePostDestinations.CreatePostScreen> {
                CreatePostController(createPostViewModel, sharedViewModel)
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
