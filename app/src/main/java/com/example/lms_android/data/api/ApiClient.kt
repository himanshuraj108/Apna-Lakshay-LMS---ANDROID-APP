package com.example.lms_android.data.api

import com.example.lms_android.data.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Target the remote server instead of localhost
    private const val BASE_URL = "https://apnalakshaybackend.onrender.com/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        TokenManager.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(requestBuilder.build())
    }

    // Retry up to 2 times on network failure (helps with Render cold-start)
    private val retryInterceptor = Interceptor { chain ->
        val request = chain.request()
        var response = runCatching { chain.proceed(request) }
        var tryCount = 0
        while (response.isFailure && tryCount < 2) {
            tryCount++
            Thread.sleep(1000L * tryCount)
            response = runCatching { chain.proceed(request) }
        }
        response.getOrThrow()
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(retryInterceptor)
        .addInterceptor(loggingInterceptor)
        // Extended timeouts for Render free-tier cold start (can take 50-90s)
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: LmsApiService by lazy {
        retrofit.create(LmsApiService::class.java)
    }
}
