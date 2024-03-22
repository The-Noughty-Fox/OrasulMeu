package com.thenoughtfox.orasulmeu

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.noughtyfox.authentication.google.GoogleSignIn
import com.thenoughtfox.orasulmeu.ui.login.presentation.LoginPage
import com.thenoughtfox.orasulmeu.ui.login.presentation.LoginViewModel
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private val googleSignIn by lazy {
        GoogleSignIn(this, activityResultRegistry,
            onSignIn = { account ->
                //On success, get data from google account.
                Timber.i("GOOD $account")
            },
            onFails = { exception ->
                Timber.i("GOOD $exception")
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(googleSignIn)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        setContent {
            OrasulMeuTheme {
                LoginPage(viewModel) {
                    googleSignIn.signInWithGoogle(getString(R.string.default_web_client_id))
                }
            }
        }
    }
}