package com.tencent.bk.devops.atom.task.utils

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object OkhttpUtils {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .writeTimeout(30L, TimeUnit.SECONDS)
        .build()!!

    fun doGet(url: String, headers: Map<String, String> = mapOf()): Response {
        return doGet(okHttpClient, url, headers)
    }

    fun doHttp(request: Request): Response {
        return doHttp(okHttpClient, request)
    }

    private fun doGet(okHttpClient: OkHttpClient, url: String, headers: Map<String, String> = mapOf()): Response {
        val requestBuilder = Request.Builder()
            .url(url)
            .get()
        if (headers.isNotEmpty()) {
            headers.forEach { key, value ->
                requestBuilder.addHeader(key, value)
            }
        }
        val request = requestBuilder.build()
        return okHttpClient.newCall(request).execute()
    }

    private fun doHttp(okHttpClient: OkHttpClient, request: Request): Response {
        return okHttpClient.newCall(request).execute()
    }

}
