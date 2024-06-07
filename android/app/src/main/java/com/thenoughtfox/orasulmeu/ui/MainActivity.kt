package com.thenoughtfox.orasulmeu.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.thenoughtfox.orasulmeu.navigation.NavigationRoot
import com.thenoughtfox.orasulmeu.ui.theme.OrasulMeuTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OrasulMeuTheme {
                NavigationRoot()
            }
        }
    }
}