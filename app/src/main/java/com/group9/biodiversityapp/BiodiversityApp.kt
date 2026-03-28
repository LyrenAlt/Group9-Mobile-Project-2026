package com.group9.biodiversityapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.group9.biodiversityapp.api.RetrofitClient

/**
 * Application class that configures Coil to use the authenticated OkHttp client.
 * This ensures that image URLs from the laji.fi API (e.g. /images/{id}/square.jpg)
 * are loaded with the access_token automatically.
 */
class BiodiversityApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient(RetrofitClient.okHttpClient)
            .crossfade(true)
            .build()
    }
}
