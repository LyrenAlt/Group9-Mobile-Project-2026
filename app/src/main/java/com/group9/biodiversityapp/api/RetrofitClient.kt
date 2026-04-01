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

    private const val BASE_URL = "https://api.laji.fi/"
    /**
     * Interceptor that authenticates every request.
     * Uses Bearer header (required by taxa/autocomplete endpoints)
     * and access_token query parameter (required by warehouse endpoints).
     */
//    private val authInterceptor = Interceptor { chain ->
//        val original = chain.request()
//        val url = original.url.newBuilder()
//            .addQueryParameter("access_token", ACCESS_TOKEN)
//            .build()
//        val request = original.newBuilder()
//            .url(url)
//            .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
//            .build()
//        chain.proceed(request)
//    }


    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()

        val request = original.newBuilder()
            .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
            .addHeader("API-Version", "1")
            .build()

        chain.proceed(request)
    }



    private const val ACCESS_TOKEN = "70130609e1838762db9d30dc3b92c1009c36ee40d6167085050742d8701ff77e"


    /**
     * Logging interceptor — logs request/response bodies in debug builds.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /** Authenticated OkHttp client — also used by Coil for loading images. */
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: LajiApiService = retrofit.create(LajiApiService::class.java)
}
