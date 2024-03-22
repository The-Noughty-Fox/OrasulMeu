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
import org.openapitools.client.infrastructure.ApiClient
import retrofit2.Retrofit
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
        userSharedPrefs: UserSharedPrefs,
//        loginUseCase: LoginUseCase
    ): OkHttpClient.Builder {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(NetworkConnectionInterceptor(context))
//            .addInterceptor(LoginInterceptor(loginUseCase))
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
        val baseUrl = "https://c16e-178-168-82-61.ngrok-free.app/"
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

    private class SessionCookie(private val userSharedPrefs: UserSharedPrefs) : CookieJar {
        private val cookies = mutableListOf<Cookie>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            this.cookies.apply {
                clear()
                addAll(cookies)

                if (!isNullOrEmpty()) {
                    userSharedPrefs.setCookie(cookies)
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> =
            if (cookies.isEmpty()) {
                userSharedPrefs.getCookie() ?: listOf()
            } else {
                cookies
            }
    }

}