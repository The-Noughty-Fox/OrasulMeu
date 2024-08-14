package com.thenoughtfox.orasulmeu.net.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.utils.showToast
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

class NetworkConnectionInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            if (!isNetworkAvailable()) {
                Handler(Looper.getMainLooper()).post {
                    context.showToast(context.getString(R.string.no_network))
                }

                createEmptyResponse(chain)
            } else {
                chain.proceed(chain.request())
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.showToast(context.getString(R.string.smth_went_wrong))
            }

            createEmptyResponse(chain)
        }
    }

    private fun createEmptyResponse(chain: Interceptor.Chain): Response {
        return Response.Builder()
            .request(chain.request())
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(503) // Service Unavailable
            .message("Service Unavailable")
            .body(ResponseBody.create(null, ""))
            .build()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    class NoConnectivityException(val context: Context) : IOException() {
        override val message: String
            get() = context.getString(R.string.no_network)
    }
}
