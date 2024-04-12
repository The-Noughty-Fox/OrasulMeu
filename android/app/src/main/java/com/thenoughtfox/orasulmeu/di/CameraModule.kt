package com.thenoughtfox.orasulmeu.di

import android.content.Context
import androidx.camera.core.ImageCapture
import com.thenoughtfox.orasulmeu.ui.create_post.camera.utils.CameraInitializer
import com.thenoughtfox.orasulmeu.ui.create_post.camera.utils.ImageCaptureFactory
import com.thenoughtfox.orasulmeu.ui.create_post.camera.utils.PerformImageCaptureUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CameraModule {

    @Provides
    @Singleton
    fun provideExecutorService(): ExecutorService = Executors.newSingleThreadExecutor()

    @Provides
    @Singleton
    fun provideImageCapture(): ImageCapture = ImageCaptureFactory.create()

    @Provides
    @Singleton
    fun provideCameraInitializer(
        @ApplicationContext context: Context,
        imageCapture: ImageCapture
    ): CameraInitializer = CameraInitializer(
        context = context,
        imageCapture = imageCapture
    )

    @Provides
    @Singleton
    fun providePerformImageCaptureUseCase(
        imageCapture: ImageCapture,
        cameraExecutor: ExecutorService,
        @ApplicationContext context: Context
    ) : PerformImageCaptureUseCase = PerformImageCaptureUseCase(
        context = context,
        imageCapture = imageCapture,
        cameraExecutor = cameraExecutor
    )
}