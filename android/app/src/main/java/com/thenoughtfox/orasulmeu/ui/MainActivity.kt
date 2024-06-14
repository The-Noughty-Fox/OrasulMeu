package com.thenoughtfox.orasulmeu.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.isVisible
import com.github.terrakok.cicerone.BackTo
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Forward
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.databinding.ActivityMainBinding
import com.thenoughtfox.orasulmeu.navigation.Screens
import com.thenoughtfox.orasulmeu.navigation.Screens.loginScreen
import com.thenoughtfox.orasulmeu.navigation.Screens.mediaPostScreen
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import com.thenoughtfox.orasulmeu.utils.view.BottomNavBar
import com.thenoughtfox.orasulmeu.utils.view.BottomNavTabs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var userSharedPrefs: UserSharedPrefs

    private var currentNavItem by mutableStateOf(BottomNavTabs.Map)

    private val navigator: Navigator =
        object : AppNavigator(this, R.id.main_container) {
            override fun applyCommands(commands: Array<out Command>) {
                super.applyCommands(commands)
                supportFragmentManager.executePendingTransactions()
                val key = commands.lastOrNull()?.let { command ->
                    (command as? Forward)?.screen?.screenKey
                        ?: (command as? Replace)?.screen?.screenKey
                        ?: (command as? BackTo)?.screen?.screenKey
                }

                currentNavItem = BottomNavTabs.entries.find { it.name == key } ?: currentNavItem
                binding.bottomNavigationView.isVisible = when (key) {
                    Screens.mapSearchScreen.screenKey,
                    Screens.createPostScreen.screenKey,
                    Screens.profileScreen.screenKey -> true

                    else -> false
                }
            }
        }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomBar()
        handlePrimaryNavigationScreen()
    }

    private fun handlePrimaryNavigationScreen() {
        if (supportFragmentManager.fragments.isEmpty()) {
            val screen = if (userSharedPrefs.user != null) {
                loginScreen
            } else {
                mediaPostScreen
            }

            router.newRootScreen(screen)
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