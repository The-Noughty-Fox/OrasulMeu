package com.thenoughtfox.orasulmeu.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PREF_FILE_NAME = "orasul_meu_data"

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext mContext: Context): SharedPreferences =
        mContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

}