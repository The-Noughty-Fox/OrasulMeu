package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.thenoughtfox.orasulmeu.ui.post.utils.Post
import com.thenoughtfox.orasulmeu.ui.screens.login.LoginController
import com.thenoughtfox.orasulmeu.ui.screens.logout.LogoutUseCase
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedViewModel
import com.thenoughtfox.orasulmeu.utils.serializableType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

interface RootNavDestinations {
    @Serializable
    data object Auth : RootNavDestinations

    @Serializable
    data object Main : RootNavDestinations

    @Serializable
    data class CreatePost(val post: Post) : RootNavDestinations {
        companion object {
            val typeMap = mapOf(typeOf<Post>() to serializableType<Post>())

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<CreatePost>(typeMap)
        }
    }
}

@Composable
fun RootGraph(logoutUseCase: LogoutUseCase, startDestinations: RootNavDestinations) {
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
            composable<RootNavDestinations.CreatePost>(
                typeMap = RootNavDestinations.CreatePost.typeMap
            ) {
                CreatePostGraph(sharedViewModel)
            }
        }
    }
}

val LocalRootNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }

