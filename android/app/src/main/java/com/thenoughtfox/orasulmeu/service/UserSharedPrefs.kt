package com.thenoughtfox.orasulmeu.service

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Cookie
import javax.inject.Inject

private const val KEY_COOKIE_DATA = "cookie_data"

class UserSharedPrefs @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun setCookie(cookie: List<Cookie>) {
        val gson = Gson()
        val json = gson.toJson(cookie)

        sharedPreferences
            .edit()
            .putString(KEY_COOKIE_DATA, json)
            .commit()
    }

    fun getCookie(): List<Cookie>? {
        val gson = Gson()
        val json = sharedPreferences.getString(KEY_COOKIE_DATA, null)
        val type = object : TypeToken<List<Cookie>>() {}.type
        return gson.fromJson(json, type)
    }

}