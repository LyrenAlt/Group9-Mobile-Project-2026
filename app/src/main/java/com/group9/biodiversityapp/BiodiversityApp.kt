package com.group9.biodiversityapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.group9.biodiversityapp.api.RetrofitClient
import com.group9.biodiversityapp.data.local.BiodiversityDatabase
import com.group9.biodiversityapp.data.repository.ObservationRepository
import com.group9.biodiversityapp.data.repository.TaxonRepository

/**
 * Application class — the single source of truth for shared dependencies.
 *
 * ViewModels access repositories like this:
 *
 *   val app = application as BiodiversityApp
 *   val taxonRepo = app.taxonRepository
 *   val observationRepo = app.observationRepository
 *
 * Or from a Composable using LocalContext:
 *
 *   val app = LocalContext.current.applicationContext as BiodiversityApp
 *   val taxonRepo = app.taxonRepository
 */
class BiodiversityApp : Application(), ImageLoaderFactory {

    /** Room database — initialized once on first access. */
    val database: BiodiversityDatabase by lazy {
        BiodiversityDatabase.getInstance(this)
    }

    /** Repository for taxa, species, autocomplete, and favorites. */
    val taxonRepository: TaxonRepository by lazy {
        TaxonRepository(
            apiService = RetrofitClient.apiService,
            taxonDao = database.taxonDao(),
            favoriteDao = database.favoriteDao()
        )
    }

    /** Repository for observations, areas, and observation favorites. */
    val observationRepository: ObservationRepository by lazy {
        ObservationRepository(
            apiService = RetrofitClient.apiService,
            observationDao = database.observationDao(),
            favoriteDao = database.favoriteDao()
        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient(RetrofitClient.okHttpClient)
            .crossfade(true)
            .build()
    }
}
