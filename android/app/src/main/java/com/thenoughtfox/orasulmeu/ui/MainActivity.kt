package com.thenoughtfox.orasulmeu.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.databinding.ActivityMainBinding
import com.thenoughtfox.orasulmeu.navigation.Screens.loginScreen
import com.thenoughtfox.orasulmeu.navigation.Screens.mapSearchScreen
import com.thenoughtfox.orasulmeu.navigation.Screens.mediaPostScreen
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
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

    private val navigator: Navigator =
        object : AppNavigator(this, R.id.main_container) {
            override fun applyCommands(commands: Array<out Command>) {
                super.applyCommands(commands)
                supportFragmentManager.executePendingTransactions()
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
}