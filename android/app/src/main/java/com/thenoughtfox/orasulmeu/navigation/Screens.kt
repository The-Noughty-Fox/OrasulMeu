package com.thenoughtfox.orasulmeu.navigation

import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.thenoughtfox.orasulmeu.ui.MainActivity
import com.thenoughtfox.orasulmeu.ui.create_post.camera.CameraFragment
import com.thenoughtfox.orasulmeu.ui.create_post.create.CreatePostFragment
import com.thenoughtfox.orasulmeu.ui.create_post.map.MapSearchFragment
import com.thenoughtfox.orasulmeu.ui.create_post.media.CreatePostMediaFragment
import com.thenoughtfox.orasulmeu.ui.login.LoginFragment
import com.thenoughtfox.orasulmeu.ui.post_list.PostListFragment
import com.thenoughtfox.orasulmeu.ui.profile_settings.ProfileSettingsFragment
import com.thenoughtfox.orasulmeu.utils.view.BottomNavTabs

object Screens {

    val loginScreen = FragmentScreen { LoginFragment() }
    val mediaPostScreen = FragmentScreen { CreatePostMediaFragment() }
    val createPostScreen = FragmentScreen(key = BottomNavTabs.Create.name) { CreatePostFragment() }
    val cameraScreen = FragmentScreen { CameraFragment() }
    val mapSearchScreen = FragmentScreen(key = BottomNavTabs.Map.name) { MapSearchFragment() }
    val profileScreen = FragmentScreen(key = BottomNavTabs.Profile.name) { MapSearchFragment() }
    val mainActivity = ActivityScreen { Intent(it, MainActivity::class.java) }
    val postListScreen = FragmentScreen { PostListFragment() }
    val profileSettingsScreen = FragmentScreen { ProfileSettingsFragment() }
}