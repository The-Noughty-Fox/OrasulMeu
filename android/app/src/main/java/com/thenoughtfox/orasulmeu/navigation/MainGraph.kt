package com.thenoughtfox.orasulmeu.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.post.utils.Post
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeController
import com.thenoughtfox.orasulmeu.ui.screens.home.search.SearchPostsController
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedViewModel
import com.thenoughtfox.orasulmeu.utils.view.BottomNavBar
import com.thenoughtfox.orasulmeu.utils.view.BottomNavTabs
import kotlinx.serialization.Serializable

interface MainGraphDestinations {

    @Serializable
    data class HomeScreen(val isAnonymous: Boolean) : MainGraphDestinations

    @Serializable
    data class SearchPostsScreen(val isAnonymous: Boolean) : MainGraphDestinations

    @Serializable
    data object ProfileScreen : MainGraphDestinations
}

const val homeScreen = "HomeScreen"
const val profileScreen = "ProfileScreen"

@Composable
fun MainGraph(sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalMainNavigator provides navController) {
        var currentNavItem by remember { mutableStateOf(BottomNavTabs.Map) }
        val rootNavigator = LocalRootNavigator.current
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val context = LocalContext.current

        LaunchedEffect(navBackStackEntry) {
            val route = navBackStackEntry?.destination?.route
                ?.substringBefore("/")
                ?.substringAfterLast(".")

            when (route) {
                homeScreen -> {
                    currentNavItem = BottomNavTabs.Map
                }

                profileScreen -> {
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
                                navController.navigate(
                                    MainGraphDestinations.HomeScreen(
                                        isAnonymous = sharedViewModel.state.value.isAnonymous
                                    )
                                ) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                            BottomNavTabs.Create.name -> {
                                if (sharedViewModel.state.value.isAnonymous) {
                                    rootNavigator.navigate(
                                        RootNavDestinations.AnonymousDialog(
                                            context.getString(R.string.auth_screen_desc_create_post)
                                        )
                                    )
                                } else {
                                    rootNavigator.navigate(
                                        RootNavDestinations.CreatePost(
                                            post = Post(),
                                            isAnonymous = sharedViewModel.state.value.isAnonymous
                                        )
                                    )
                                }
                            }

                            BottomNavTabs.Profile.name -> {
                                if (sharedViewModel.state.value.isAnonymous) {
                                    rootNavigator.navigate(
                                        RootNavDestinations.AnonymousDialog(
                                            context.getString(R.string.auth_screen_desc_profile)
                                        )
                                    )
                                } else {
                                    navController.navigate(MainGraphDestinations.ProfileScreen) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }

                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
                    startDestination = MainGraphDestinations.HomeScreen(
                        isAnonymous = sharedViewModel.state.collectAsState().value.isAnonymous
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding())
                ) {
                    composable<MainGraphDestinations.HomeScreen> {
                        HomeController(sharedViewModel)
                    }

                    composable<MainGraphDestinations.SearchPostsScreen> {
                        SearchPostsController()
                    }

                    composable<MainGraphDestinations.ProfileScreen> {
                        ProfileGraph(sharedViewModel)
                    }
                }
            }
        )
    }
}

val LocalMainNavigator =
    staticCompositionLocalOf<NavHostController> { error("Error! navController wasn't initialized!") }

