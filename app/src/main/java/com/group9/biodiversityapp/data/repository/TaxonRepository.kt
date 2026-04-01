package com.group9.biodiversityapp.data.repository

import com.group9.biodiversityapp.api.LajiApiService
import com.group9.biodiversityapp.api.model.AutocompleteResult
import com.group9.biodiversityapp.api.model.InformalTaxonGroupResponse
import com.group9.biodiversityapp.api.model.PagedResponse
import com.group9.biodiversityapp.api.model.TaxonResponse
import com.group9.biodiversityapp.data.local.dao.FavoriteDao
import com.group9.biodiversityapp.data.local.dao.TaxonDao
import com.group9.biodiversityapp.data.local.entity.FavoriteTaxonEntity
import com.group9.biodiversityapp.data.local.entity.TaxonEntity
import kotlinx.coroutines.flow.Flow
import com.group9.biodiversityapp.api.model.ParentTaxon

/**
 * Repository that mediates between the laji.fi Taxa API and the local Room cache.
 *
 * - Network calls go through [apiService].
 * - Cached data is read/written through [taxonDao].
 * - Favorites are managed through [favoriteDao].
 *
 * US-41: Offline caching — all fetch methods cache results in Room.
 *        Use getCached* methods to read from cache when offline.
 * US-42: Language support — all methods accept a lang parameter ("en" or "fi").
 *
 * Usage from ViewModel:
 *
 *   val app = application as BiodiversityApp
 *   val repo = app.taxonRepository
 *
 *   // Online: fetches from API and caches
 *   val species = repo.fetchSpecies(lang = "fi")
 *
 *   // Offline: reads from cache
 *   val cached = repo.getCachedTaxa()
 *
 *   // Smart: tries API first, falls back to cache
 *   val taxon = repo.getTaxonByIdWithFallback("MX.37600", lang = "en")
 */
class TaxonRepository(
    private val apiService: LajiApiService,
    private val taxonDao: TaxonDao,
    private val favoriteDao: FavoriteDao
) {

    // ── Cache duration ──────────────────────────────────────────────────

    companion object {
        /** Cache is considered fresh for 24 hours. */
        const val CACHE_DURATION_MS = 24 * 60 * 60 * 1000L
    }

    // ── Remote API calls (all cache results to Room) ────────────────────

    /** Fetch taxa from the API and cache them locally. US-42: pass lang = "fi" for Finnish. */
    suspend fun fetchTaxa(
        page: Int = 1,
        pageSize: Int = 25,
        query: String? = null,
        informalTaxonGroup: String? = null,
        lang: String = "en"
    ): PagedResponse<TaxonResponse> {
        val response = apiService.getTaxa(
            page = page,
            pageSize = pageSize,
            query = query,
            informalTaxonGroup = informalTaxonGroup,
            lang = lang
        )
        // US-41: Cache results
        val entities = response.results.map { it.toEntity() }
        taxonDao.insertAll(entities)
        return response
    }

    /** Fetch Finnish species from the API and cache them locally. */
    suspend fun fetchSpecies(
        page: Int = 1,
        pageSize: Int = 25,
        query: String? = null,
        informalTaxonGroup: String? = null,
        lang: String = "en"
    ): PagedResponse<TaxonResponse> {
        val response = apiService.getSpecies(
            page = page,
            pageSize = pageSize,
            query = query,
            informalTaxonGroup = informalTaxonGroup,
            lang = lang
        )
        // US-41: Cache results
        val entities = response.results.map { it.toEntity() }
        taxonDao.insertAll(entities)
        return response
    }

    /** Fetch a single taxon by ID from the API and cache it. */
    suspend fun fetchTaxonById(id: String, lang: String = "en"): TaxonResponse {
        val response = apiService.getTaxonById(id = id, lang = lang)
        taxonDao.insert(response.toEntity())
        return response
    }

    /**
     * US-41: Smart fetch — tries API first, falls back to local cache if offline.
     * Returns null only if both API and cache miss.
     */
    suspend fun getTaxonByIdWithFallback(id: String, lang: String = "en"): TaxonResponse? {
        return try {
            fetchTaxonById(id, lang)
        } catch (e: Exception) {
            // Offline or API error — try cache
            taxonDao.getById(id)?.toResponse()
        }
    }

    /**
     * US-41: Smart fetch for species list — tries API first, falls back to cache.
     * Returns cached data if API fails.
     */
    suspend fun getSpeciesWithFallback(
        page: Int = 1,
        pageSize: Int = 25,
        query: String? = null,
        informalTaxonGroup: String? = null,
        lang: String = "en"
    ): List<TaxonResponse> {
        return try {
            fetchSpecies(page, pageSize, query, informalTaxonGroup, lang).results
        } catch (e: Exception) {
            // Offline — return cached data
            val cached = taxonDao.getAllSync()
            cached.map { it.toResponse() }
        }
    }

    /** Autocomplete taxon names via the API (not cached). */
    suspend fun autocompleteTaxon(query: String, lang: String = "en"): List<AutocompleteResult> {
        return apiService.autocompleteTaxon(query = query, lang = lang)
    }

    /** Fetch informal taxon groups from the API. */
    suspend fun fetchInformalTaxonGroups(lang: String = "en"): PagedResponse<InformalTaxonGroupResponse> {
        return apiService.getInformalTaxonGroups(lang = lang)
    }

    // ── US-41: Local cache reads ────────────────────────────────────────

    /** Observe all cached taxa (live updates). */
    fun getCachedTaxa(): Flow<List<TaxonEntity>> = taxonDao.getAll()

    /** Search cached taxa by name. */
    fun searchCachedTaxa(query: String): Flow<List<TaxonEntity>> = taxonDao.search(query)

    /** Get a single cached taxon by ID. */
    suspend fun getCachedTaxonById(id: String): TaxonEntity? = taxonDao.getById(id)

    /** Check if cache has fresh data (less than 24 hours old). */
    suspend fun isCacheFresh(): Boolean {
        val cutoff = System.currentTimeMillis() - CACHE_DURATION_MS
        val oldest = taxonDao.getById(taxonDao.getAllSync().firstOrNull()?.id ?: return false)
        return oldest != null && oldest.cachedAt > cutoff
    }

    /** Clear cache entries older than 24 hours. */
    suspend fun clearExpiredCache() {
        val cutoff = System.currentTimeMillis() - CACHE_DURATION_MS
        taxonDao.deleteOlderThan(cutoff)
    }

    /** Clear all cached taxa. */
    suspend fun clearAllCache() {
        taxonDao.deleteAll()
    }

    /** Clear cache entries older than the given timestamp. */
    suspend fun clearOldCache(olderThanMillis: Long) {
        taxonDao.deleteOlderThan(olderThanMillis)
    }

    // ── Favorites ───────────────────────────────────────────────────────

    fun getFavoriteTaxa(): Flow<List<FavoriteTaxonEntity>> = favoriteDao.getAllFavoriteTaxa()

    fun getFavoriteTaxaCount(): Flow<Int> = favoriteDao.getFavoriteTaxaCount()

    fun isFavorite(taxonId: String): Flow<Boolean> = favoriteDao.isTaxonFavorite(taxonId)

    suspend fun toggleFavorite(taxon: TaxonResponse) {
        val isFav = favoriteDao.isTaxonFavoriteSync(taxon.id)
        if (isFav) {
            favoriteDao.removeFavoriteTaxonById(taxon.id)
        } else {
            favoriteDao.addFavoriteTaxon(
                FavoriteTaxonEntity(
                    taxonId = taxon.id,
                    scientificName = taxon.scientificName,
                    vernacularName = taxon.vernacularName,
                    taxonRank = taxon.taxonRank,
                    thumbnailUrl = taxon.multimedia?.firstOrNull()?.squareThumbnailURL
                )
            )
        }
    }

    suspend fun addFavorite(entity: FavoriteTaxonEntity) = favoriteDao.addFavoriteTaxon(entity)

    suspend fun removeFavorite(taxonId: String) = favoriteDao.removeFavoriteTaxonById(taxonId)
}

// ── Mapping extensions ──────────────────────────────────────────────────

private fun TaxonResponse.toEntity() = TaxonEntity(
    id = id,
    scientificName = scientificName,
    vernacularName = vernacularName,
    taxonRank = taxonRank,
    finnish = finnish,
    informalTaxonGroups = informalTaxonGroups,
    parentId = parent?.id,
    scientificNameAuthorship = scientificNameAuthorship,
    thumbnailUrl = multimedia?.firstOrNull()?.squareThumbnailURL
)

/** Convert cached entity back to a TaxonResponse (for offline use). */
private fun TaxonEntity.toResponse() = TaxonResponse(
    id = id,
    scientificName = scientificName,
    vernacularName = vernacularName,
    taxonRank = taxonRank,
    finnish = finnish,
    informalTaxonGroups = informalTaxonGroups,
    parent = parentId?.let {
        ParentTaxon(id = it)
    },
    scientificNameAuthorship = scientificNameAuthorship,
    multimedia = thumbnailUrl?.let {
        listOf(com.group9.biodiversityapp.api.model.MultimediaItem(squareThumbnailURL = it))
    }
)
