package com.group9.biodiversityapp.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client for the laji.fi API.
 *
 * Usage:
 *   val api = RetrofitClient.apiService
 *   val taxa = api.getTaxa(query = "Parus major")
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.laji.fi/v0/"
    private const val ACCESS_TOKEN = "70130609e1838762db9d30dc3b92c1009c36ee40d6167085050742d8701ff77e"

    /**
     * Interceptor that authenticates every request.
     * Sends the Bearer header, the accessToken query parameter,
     * and the API-Version header matching the Swagger spec.
     */
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("accessToken", ACCESS_TOKEN)
            .build()
        val request = original.newBuilder()
            .url(url)
            .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
            .build()
        chain.proceed(request)
    }

    /**
     * Logging interceptor — logs request/response bodies in debug builds.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: LajiApiService = retrofit.create(LajiApiService::class.java)
}
