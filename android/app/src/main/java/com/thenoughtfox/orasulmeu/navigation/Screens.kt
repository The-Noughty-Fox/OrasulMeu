package com.thenoughtfox.orasulmeu.navigation

import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.thenoughtfox.orasulmeu.ui.MainActivity
import com.thenoughtfox.orasulmeu.ui.login.presentation.LoginFragment

object Screens {

    fun loginScreen() = FragmentScreen { LoginFragment() }

    fun mainActivity() = ActivityScreen { Intent(it, MainActivity::class.java) }

}