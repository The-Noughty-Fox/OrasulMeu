package com.thenoughtfox.orasulmeu.di

import android.content.Context
import com.thenoughtfox.orasulmeu.net.interceptor.HeaderInterceptor
import com.thenoughtfox.orasulmeu.net.interceptor.NetworkConnectionInterceptor
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.openapitools.client.apis.AuthApi
import org.openapitools.client.apis.EchoApi
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.apis.UsersApi
import org.openapitools.client.infrastructure.ApiClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetModule {

    private const val TIME_OUT = 60L

    @Provides
    @Singleton
    fun provideOkHttpClientBuilder(
        @ApplicationContext context: Context,
        userSharedPrefs: UserSharedPrefs
    ): OkHttpClient.Builder {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(NetworkConnectionInterceptor(context))
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .cookieJar(SessionCookie(userSharedPrefs))
            .retryOnConnectionFailure(true)
    }

    @Provides
    @Singleton
    fun provideApiClient(
        okHttpBuilder: OkHttpClient.Builder,
        @ApplicationContext context: Context
    ): ApiClient {
        val baseUrl = "http://192.168.0.34:8080"
        return ApiClient(okHttpClientBuilder = okHttpBuilder, baseUrl = baseUrl)
    }

    @Provides
    @Singleton
    fun provideEchoApi(apiClient: ApiClient): EchoApi =
        apiClient.createService(EchoApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(apiClient: ApiClient): AuthApi =
        apiClient.createService(AuthApi::class.java)

    @Provides
    @Singleton
    fun providePostsApi(apiClient: ApiClient): PostsApi =
        apiClient.createService(PostsApi::class.java)

    @Provides
    @Singleton
    fun provideUsersApi(apiClient: ApiClient): UsersApi =
        apiClient.createService(UsersApi::class.java)

    private class SessionCookie(private val userSharedPrefs: UserSharedPrefs) : CookieJar {
        private val cookies = mutableListOf<Cookie>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            this.cookies.apply {
                clear()
                addAll(cookies)

                if (!isNullOrEmpty()) {
                    userSharedPrefs.cookies = cookies
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> =
            if (cookies.isEmpty()) {
                userSharedPrefs.cookies ?: listOf()
            } else {
                cookies
            }
    }

}