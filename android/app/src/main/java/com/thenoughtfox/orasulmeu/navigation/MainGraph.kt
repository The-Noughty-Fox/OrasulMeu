package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.thenoughtfox.orasulmeu.utils.view.BottomNavBar
import com.thenoughtfox.orasulmeu.utils.view.BottomNavTabs
import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * @author Knurenko Bogdan 28.06.2024
 */

@Composable
fun MainGraph() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavigator provides navController) {
        var currentNavItem by remember { mutableStateOf(BottomNavTabs.Map) }
        var isBottomNavBarVisible by remember { mutableStateOf(true) }

        Scaffold(bottomBar = {
            BottomNavBar(
                selected = currentNavItem,
                onSelectTab = { navTabs ->
                    if (navTabs == currentNavItem) return@BottomNavBar

                    when (navTabs.name) {
                        BottomNavTabs.Map.name -> navController.navigate(GlobalNavDestinations.MapScreen)
                        BottomNavTabs.Create.name -> navController.navigate(GlobalNavDestinations.CreatePostScreen)
                        BottomNavTabs.Profile.name -> navController.navigate(
                            GlobalNavDestinations.ProfileScreen(id = Random.nextInt())
                        )
                    }
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
            )

        }, content = { padding ->
            NavHost(
                navController = navController,
                startDestination = GlobalNavDestinations.AuthScreen,
                modifier = Modifier.padding(padding)
            ) {
                // we don't see it on bottom nav bar, but it seems to be placed here
                composable<GlobalNavDestinations.AuthScreen> {
                    AuthGraph()
                    SideEffect {
                        isBottomNavBarVisible = false
                    }
                }

                composable<GlobalNavDestinations.MapScreen> {
                    Text(text = "Map screen")

                    SideEffect {
                        currentNavItem = BottomNavTabs.Map
                        isBottomNavBarVisible = true
                    }
                }

                composable<GlobalNavDestinations.CreatePostScreen> {
                    CreatePostGraph()
                    SideEffect {
                        currentNavItem = BottomNavTabs.Create
                        isBottomNavBarVisible = true
                    }
                }

                composable<GlobalNavDestinations.ProfileScreen> {
                    val profile = it.toRoute<GlobalNavDestinations.ProfileScreen>()
                    val id = profile.id
                    Text(text = "profile id is $id")

                    SideEffect {
                        currentNavItem = BottomNavTabs.Profile
                        isBottomNavBarVisible = true
                    }
                }
            }
        })
    }
}

interface GlobalNavDestinations {

    @Serializable
    object CreatePostScreen

    @Serializable
    object MapScreen

    @Serializable
    data class ProfileScreen(val id: Int)

    @Serializable
    object AuthScreen
}

val LocalNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }

