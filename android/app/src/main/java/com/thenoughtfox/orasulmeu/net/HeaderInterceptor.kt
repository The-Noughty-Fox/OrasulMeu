package com.thenoughtfox.orasulmeu.net

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    private companion object{
         const val CONTENT_TYPE = "Content-Type"
         const val TEXT_PLAIN = "text/plain;charset=UTF-8"
         const val ACCEPT = "Accept"
         const val APPLICATION_JSON = "application/json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val builder = request.newBuilder()
            .header(CONTENT_TYPE, TEXT_PLAIN)
            .header(ACCEPT, APPLICATION_JSON)
            .method(request.method, request.body)
            .build()

       return chain.proceed(builder)
    }
}