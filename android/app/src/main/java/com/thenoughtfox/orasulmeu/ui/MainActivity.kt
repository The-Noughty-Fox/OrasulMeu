package com.thenoughtfox.orasulmeu.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.thenoughtfox.orasulmeu.navigation.RootGraph
import com.thenoughtfox.orasulmeu.navigation.RootNavDestinations
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import com.thenoughtfox.orasulmeu.ui.screens.logout.LogoutUseCase
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userSharedPrefs: UserSharedPrefs

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))

        setContent {
            OrasulMeuTheme {
                RootGraph(
                    logoutUseCase,
                    startDestinations = if (userSharedPrefs.user != null) {
                        RootNavDestinations.Main
                    } else {
                        RootNavDestinations.Auth
                    }
                )
            }
        }
    }
}