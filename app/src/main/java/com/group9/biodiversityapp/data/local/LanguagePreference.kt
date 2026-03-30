package com.group9.biodiversityapp.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * US-42: Persists the user's language choice (Finnish / English).
 *
 * Usage from ViewModel or Composable:
 *
 *   val langPref = LanguagePreference(context)
 *
 *   // Read current language
 *   val lang = langPref.language  // "en" or "fi"
 *
 *   // Switch language
 *   langPref.language = "fi"
 *
 *   // Pass to any repository method
 *   val species = repo.fetchSpecies(lang = langPref.language)
 *   val taxon = repo.fetchTaxonById("MX.37600", lang = langPref.language)
 */
class LanguagePreference(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("biodiversity_prefs", Context.MODE_PRIVATE)

    /** Current language code: "en" (English) or "fi" (Finnish). Default is "en". */
    var language: String
        get() = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) {
            require(value in SUPPORTED_LANGUAGES) { "Unsupported language: $value. Use one of: $SUPPORTED_LANGUAGES" }
            prefs.edit().putString(KEY_LANGUAGE, value).apply()
        }

    /** Toggle between Finnish and English. Returns the new language. */
    fun toggle(): String {
        language = if (language == "fi") "en" else "fi"
        return language
    }

    companion object {
        private const val KEY_LANGUAGE = "preferred_language"
        private const val DEFAULT_LANGUAGE = "en"
        val SUPPORTED_LANGUAGES = setOf("en", "fi")
    }
}
