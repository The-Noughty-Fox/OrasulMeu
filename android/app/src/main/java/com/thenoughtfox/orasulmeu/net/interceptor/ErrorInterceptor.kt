package com.thenoughtfox.orasulmeu.net.interceptor

import android.content.Context
import android.widget.Toast
import com.thenoughtfox.orasulmeu.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorInterceptor(
    private val context: Context,
    private val logoutUseCase: LogoutUseCase
) : Interceptor {

    companion object {
        const val OFFLINE_CODE = 900
        private const val ERROR_CODE = 400
        private const val OFFLINE_MESSAGE = "No internet connection"
        private const val UNKNOWN_ERROR_MESSAGE = "Unknown Error"
    }

    private var messageJob: Job? = null
    private var toast: Toast? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            when (e) {
                is ConnectException, is SocketTimeoutException, is UnknownHostException -> {
                    showMessage(context.getString(R.string.no_network))
                    buildErrorResponse(
                        code = OFFLINE_CODE,
                        request = request,
                        stringBody = context.getString(R.string.no_network),
                        message = OFFLINE_MESSAGE
                    )
                }

                else -> {
                    buildErrorResponse(
                        ERROR_CODE,
                        request,
                        e.message.toString(),
                        UNKNOWN_ERROR_MESSAGE
                    )
                }
            }
        }

        if (response.code == 401) {
            logoutUseCase.logout("Unauthorized")
        }

        return response
    }

    private fun showMessage(errorMsg: String) {
        messageJob?.cancel()
        messageJob = CoroutineScope(Dispatchers.Main).launch {
            toast?.cancel()
            toast = Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    private fun buildErrorResponse(
        code: Int,
        request: Request,
        stringBody: String,
        message: String
    ) = Response.Builder()
        .code(code)
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .body(stringBody.toResponseBody())
        .message(message)
        .build()
}