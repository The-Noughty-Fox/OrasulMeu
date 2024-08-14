package com.thenoughtfox.orasulmeu.navigation

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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeController
import com.thenoughtfox.orasulmeu.ui.screens.home.search.SearchPostsController
import com.thenoughtfox.orasulmeu.utils.view.BottomNavBar
import com.thenoughtfox.orasulmeu.utils.view.BottomNavTabs
import kotlinx.serialization.Serializable

interface MainGraphDestinations {

    @Serializable
    data object HomeScreen : MainGraphDestinations

    @Serializable
    data object SearchPostsScreen : MainGraphDestinations

    @Serializable
    data object ProfileScreen : MainGraphDestinations
}

@Composable
fun MainGraph() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalMainNavigator provides navController) {
        var currentNavItem by remember { mutableStateOf(BottomNavTabs.Map) }
        val rootNavigator = LocalRootNavigator.current
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        LaunchedEffect(navBackStackEntry) {
            when (navBackStackEntry?.destination?.route?.substringAfterLast(".")) {
                MainGraphDestinations.HomeScreen.toString() -> {
                    currentNavItem = BottomNavTabs.Map
                }

                MainGraphDestinations.SearchPostsScreen.toString() -> {
                    currentNavItem = BottomNavTabs.Create
                }

                MainGraphDestinations.ProfileScreen.toString() -> {
                    currentNavItem = BottomNavTabs.Profile
                }
            }
        }

        Scaffold(
            topBar = {},
            bottomBar = {
                BottomNavBar(
                    selected = currentNavItem,
                    onSelectTab = { navTabs ->
                        if (navTabs == currentNavItem) return@BottomNavBar
                        when (navTabs.name) {
                            BottomNavTabs.Map.name -> {
                                navController.navigate(MainGraphDestinations.HomeScreen) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                            BottomNavTabs.Create.name -> {
                                rootNavigator.navigate(RootNavDestinations.CreatePostScreen)
                            }

                            BottomNavTabs.Profile.name -> {
                                navController.navigate(MainGraphDestinations.ProfileScreen) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                )
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
                    }

                    composable<MainGraphDestinations.SearchPostsScreen> {
                        SearchPostsController()
                    }

                    composable<MainGraphDestinations.ProfileScreen> {
                        ProfileGraph()
                    }
                }
            }
        )
    }
}

val LocalMainNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }

