package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeController
import com.thenoughtfox.orasulmeu.ui.screens.home.search.SearchPostsController
import com.thenoughtfox.orasulmeu.utils.view.BottomNavBar
import com.thenoughtfox.orasulmeu.utils.view.BottomNavTabs
import kotlinx.serialization.Serializable

interface MainGraphDestinations {

    @Serializable
    object HomeScreen : MainGraphDestinations

    @Serializable
    object SearchPostsScreen : MainGraphDestinations

    @Serializable
    object ProfileScreen : MainGraphDestinations
}

@Composable
fun MainGraph() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalMainNavigator provides navController) {
        var currentNavItem by remember { mutableStateOf(BottomNavTabs.Map) }
        var isBottomNavBarVisible by remember { mutableStateOf(true) }
        val rootNavigator = LocalRootNavigator.current

        Scaffold(
            topBar = {},
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomNavBarVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    BottomNavBar(
                        selected = currentNavItem,
                        onSelectTab = { navTabs ->
                            if (navTabs == currentNavItem) return@BottomNavBar

                            when (navTabs.name) {
                                BottomNavTabs.Map.name -> navController.navigate(
                                    MainGraphDestinations.HomeScreen
                                )

                                BottomNavTabs.Create.name -> {
                                    rootNavigator.navigate(
                                        RootNavDestinations.CreatePostScreen
                                    )
                                }

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

                        LaunchedEffect(Unit) {
                            currentNavItem = BottomNavTabs.Map
                            isBottomNavBarVisible = true
                        }
                    }

                    composable<MainGraphDestinations.SearchPostsScreen> {
                        SearchPostsController()

                        LaunchedEffect(Unit) {
                            isBottomNavBarVisible = false
                        }
                    }

                    composable<MainGraphDestinations.ProfileScreen> {
                        ProfileGraph()

                        LaunchedEffect(Unit) {
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

