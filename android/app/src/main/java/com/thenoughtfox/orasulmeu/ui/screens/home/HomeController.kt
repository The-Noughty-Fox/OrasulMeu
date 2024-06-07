package com.thenoughtfox.orasulmeu.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thenoughtfox.orasulmeu.navigation.LocalNavigator
import com.thenoughtfox.orasulmeu.navigation.NavDestinations

/**
 * @author Knurenko Bogdan 31.05.2024
 */

@Composable
fun HomeController() {

    // todo later it will be a screen for list/map of posts view with bottom navigation bar etc

    Column(modifier = Modifier.fillMaxSize()) {
        val navigator = LocalNavigator.current
        Button(onClick = { navigator.navigate(NavDestinations.AuthScreen)}) {
            Text(text = "go to auth")
        }
        Button(onClick = { navigator.navigate(NavDestinations.PostListScreen) }) {
            Text(text = "go to post list")
        }
        Button(onClick = {navigator.navigate(NavDestinations.CreatePostScreen) }) {
            Text("go to create post")
        }
    }
}