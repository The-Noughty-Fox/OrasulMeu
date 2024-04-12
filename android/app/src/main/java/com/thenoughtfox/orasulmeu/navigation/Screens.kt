package com.thenoughtfox.orasulmeu.navigation

import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.thenoughtfox.orasulmeu.ui.MainActivity
import com.thenoughtfox.orasulmeu.ui.create_post.camera.CameraFragment
import com.thenoughtfox.orasulmeu.ui.create_post.create.CreatePostFragment
import com.thenoughtfox.orasulmeu.ui.create_post.map.MapSearchFragment
import com.thenoughtfox.orasulmeu.ui.create_post.media.CreatePostMediaFragment
import com.thenoughtfox.orasulmeu.ui.login.presentation.LoginFragment

object Screens {

    fun loginScreen() = FragmentScreen { LoginFragment() }
    fun mediaPostScreen() = FragmentScreen { CreatePostMediaFragment() }
    fun createPostScreen() = FragmentScreen { CreatePostFragment() }
    fun cameraScreen() = FragmentScreen { CameraFragment() }
    fun mapSearchScreen() = FragmentScreen { MapSearchFragment() }

    fun mainActivity() = ActivityScreen { Intent(it, MainActivity::class.java) }

}