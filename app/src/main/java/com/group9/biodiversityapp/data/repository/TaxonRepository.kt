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

/**
 * Repository that mediates between the laji.fi Taxa API and the local Room cache.
 *
 * - Network calls go through [apiService].
 * - Cached data is read/written through [taxonDao].
 * - Favorites are managed through [favoriteDao].
 */
class TaxonRepository(
    private val apiService: LajiApiService,
    private val taxonDao: TaxonDao,
    private val favoriteDao: FavoriteDao
) {

    // ── Remote API calls ────────────────────────────────────────────────

    /** Fetch taxa from the API and cache them locally. */
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
        // Cache results
        val entities = response.results.map { it.toEntity() }
        taxonDao.insertAll(entities)
        return response
    }

    /** Fetch species from the API and cache them locally. */
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

    /** Autocomplete taxon names via the API (not cached). */
    suspend fun autocompleteTaxon(query: String, lang: String = "en"): List<AutocompleteResult> {
        return apiService.autocompleteTaxon(query = query, lang = lang)
    }

    /** Fetch informal taxon groups from the API. */
    suspend fun fetchInformalTaxonGroups(lang: String = "en"): PagedResponse<InformalTaxonGroupResponse> {
        return apiService.getInformalTaxonGroups(lang = lang)
    }

    // ── Local cache reads ───────────────────────────────────────────────

    /** Observe all cached taxa. */
    fun getCachedTaxa(): Flow<List<TaxonEntity>> = taxonDao.getAll()

    /** Search cached taxa by name. */
    fun searchCachedTaxa(query: String): Flow<List<TaxonEntity>> = taxonDao.search(query)

    /** Get a single cached taxon by ID. */
    suspend fun getCachedTaxonById(id: String): TaxonEntity? = taxonDao.getById(id)

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

// ── Mapping extension ───────────────────────────────────────────────────

private fun TaxonResponse.toEntity() = TaxonEntity(
    id = id,
    scientificName = scientificName,
    vernacularName = vernacularName,
    taxonRank = taxonRank,
    finnish = finnish,
    informalTaxonGroups = informalTaxonGroups,
    parentId = parent,
    scientificNameAuthorship = scientificNameAuthorship,
    thumbnailUrl = multimedia?.firstOrNull()?.squareThumbnailURL
)
