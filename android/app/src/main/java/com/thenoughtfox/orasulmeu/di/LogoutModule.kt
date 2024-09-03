package com.thenoughtfox.orasulmeu.di

import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import com.thenoughtfox.orasulmeu.ui.screens.logout.LogoutUseCase
import com.thenoughtfox.orasulmeu.ui.screens.logout.LogoutUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LogoutModule {

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        userSharedPrefs: UserSharedPrefs
    ): LogoutUseCase = LogoutUseCaseImpl(userSharedPrefs)

}