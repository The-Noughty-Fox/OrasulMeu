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
import com.thenoughtfox.orasulmeu.ui.map.MapFragment

object Screens {

    val loginScreen = FragmentScreen { LoginFragment() }
    val mediaPostScreen = FragmentScreen { CreatePostMediaFragment() }
    val createPostScreen = FragmentScreen { CreatePostFragment() }
    val cameraScreen = FragmentScreen { CameraFragment() }
    val mapSearchScreen = FragmentScreen { MapSearchFragment() }
    val mapScreen = FragmentScreen { MapFragment() }
    val mainActivity = ActivityScreen { Intent(it, MainActivity::class.java) }

}