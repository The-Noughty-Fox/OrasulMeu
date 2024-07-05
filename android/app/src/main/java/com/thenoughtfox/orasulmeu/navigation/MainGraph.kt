package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeController
import com.thenoughtfox.orasulmeu.utils.view.BottomNavBar
import com.thenoughtfox.orasulmeu.utils.view.BottomNavTabs
import kotlinx.serialization.Serializable

interface MainGraphDestinations {

    @Serializable
    object CreatePostScreen

    @Serializable
    object HomeScreen

    @Serializable
    object ProfileScreen
}

@Composable
fun MainGraph() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalMainNavigator provides navController) {
        var currentNavItem by remember { mutableStateOf(BottomNavTabs.Map) }
        var isBottomNavBarVisible by remember { mutableStateOf(true) }

        Scaffold(
            topBar = {},
            bottomBar = {
                if (isBottomNavBarVisible) {
                    BottomNavBar(
                        selected = currentNavItem,
                        onSelectTab = { navTabs ->
                            if (navTabs == currentNavItem) return@BottomNavBar

                            when (navTabs.name) {
                                BottomNavTabs.Map.name -> navController.navigate(
                                    MainGraphDestinations.HomeScreen
                                )

                                BottomNavTabs.Create.name -> navController.navigate(
                                    MainGraphDestinations.CreatePostScreen
                                )

                                BottomNavTabs.Profile.name -> navController.navigate(
                                    MainGraphDestinations.ProfileScreen
                                )
                            }
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .fillMaxWidth()
                    )
                }
            },
            content = { padding ->
                NavHost(
                    navController = navController,
                    startDestination = MainGraphDestinations.HomeScreen,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding())
                ) {
                    composable<MainGraphDestinations.HomeScreen> {
                        HomeController()

                        SideEffect {
                            currentNavItem = BottomNavTabs.Map
                            isBottomNavBarVisible = true
                        }
                    }

                    composable<MainGraphDestinations.CreatePostScreen> {
                        CreatePostGraph()
                        SideEffect {
                            currentNavItem = BottomNavTabs.Create
                            isBottomNavBarVisible = false
                        }
                    }

                    composable<MainGraphDestinations.ProfileScreen> {
                        ProfileGraph()

                        SideEffect {
                            currentNavItem = BottomNavTabs.Profile
                            isBottomNavBarVisible = true
                        }
                    }
                }
            }
        )
    }
}


val LocalMainNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }

