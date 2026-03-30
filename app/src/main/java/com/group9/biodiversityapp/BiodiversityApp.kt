package com.group9.biodiversityapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.group9.biodiversityapp.api.RetrofitClient
import com.group9.biodiversityapp.data.local.BiodiversityDatabase
import com.group9.biodiversityapp.data.local.LanguagePreference
import com.group9.biodiversityapp.data.repository.AuthRepository
import com.group9.biodiversityapp.data.repository.ObservationRepository
import com.group9.biodiversityapp.data.repository.TaxonRepository

/**
 * Application class — the single source of truth for shared dependencies.
 *
 * Access from a ViewModel:
 *
 *   val app = application as BiodiversityApp
 *   val taxonRepo = app.taxonRepository
 *   val observationRepo = app.observationRepository
 *   val auth = app.authRepository
 *
 * Access from a Composable:
 *
 *   val app = LocalContext.current.applicationContext as BiodiversityApp
 */
class BiodiversityApp : Application(), ImageLoaderFactory {

    /** Room database — initialized once on first access. */
    val database: BiodiversityDatabase by lazy {
        BiodiversityDatabase.getInstance(this)
    }

    /** Taxa, species, autocomplete, and favorites. */
    val taxonRepository: TaxonRepository by lazy {
        TaxonRepository(
            apiService = RetrofitClient.apiService,
            taxonDao = database.taxonDao(),
            favoriteDao = database.favoriteDao()
        )
    }

    /** Observations, areas, and observation favorites. */
    val observationRepository: ObservationRepository by lazy {
        ObservationRepository(
            apiService = RetrofitClient.apiService,
            observationDao = database.observationDao(),
            favoriteDao = database.favoriteDao()
        )
    }

    /** User registration, login, and password reset. */
    val authRepository: AuthRepository by lazy {
        AuthRepository(userDao = database.userDao())
    }

    /** US-42: Language preference — "en" or "fi". Persists across app restarts. */
    val languagePreference: LanguagePreference by lazy {
        LanguagePreference(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient(RetrofitClient.okHttpClient)
            .crossfade(true)
            .build()
    }
}
