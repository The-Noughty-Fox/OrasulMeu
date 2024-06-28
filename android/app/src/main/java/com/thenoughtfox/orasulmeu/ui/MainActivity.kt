package com.thenoughtfox.orasulmeu.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.thenoughtfox.orasulmeu.navigation.NavigationRoot
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))

        setContent {
            OrasulMeuTheme {
                NavigationRoot()
            }
        }
    }

    private fun setupBottomBar() = binding.bottomNavigationView.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            OrasulMeuTheme {
                BottomNavBar(
                    selected = currentNavItem,
                    onSelectTab = { navTabs ->
                        if (navTabs == currentNavItem) return@BottomNavBar
                        when (navTabs.name) {
                            BottomNavTabs.Map.name -> router.replaceScreen(Screens.mapSearchScreen)
                            BottomNavTabs.Create.name -> router.replaceScreen(Screens.createPostScreen)
                            BottomNavTabs.Profile.name -> router.replaceScreen(Screens.profileScreen)
                        }
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                )
            }
        }
    }
}