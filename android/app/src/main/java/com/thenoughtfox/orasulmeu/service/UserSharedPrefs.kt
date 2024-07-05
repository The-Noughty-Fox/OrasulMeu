package com.thenoughtfox.orasulmeu.service

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thenoughtfox.orasulmeu.net.model.user.User
import okhttp3.Cookie
import javax.inject.Inject


class UserSharedPrefs @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private companion object {
        const val KEY_COOKIE_DATA = "cookie_data"
        const val KEY_USER_DATA = "user_data"
    }

    var cookies: List<Cookie>? = null
        set(value) {
            field = value
            val gson = Gson()
            val json = gson.toJson(value)

            sharedPreferences
                .edit()
                .putString(KEY_COOKIE_DATA, json)
                .apply()
        }
        get() {
            val gson = Gson()
            val json = sharedPreferences.getString(KEY_COOKIE_DATA, null)
            val type = object : TypeToken<List<Cookie>>() {}.type
            return gson.fromJson(json, type)
        }

    var user: User? = null
        set(value) {
            field = value
            val gson = Gson()
            val json = gson.toJson(value)

            sharedPreferences
                .edit()
                .putString(KEY_USER_DATA, json)
                .apply()
        }
        get() {
            val gson = Gson()
            val json = sharedPreferences.getString(KEY_USER_DATA, null)
            val type = object : TypeToken<User>() {}.type
            return gson.fromJson(json, type)
        }
}