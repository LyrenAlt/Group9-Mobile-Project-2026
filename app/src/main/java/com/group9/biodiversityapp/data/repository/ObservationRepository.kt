package com.group9.biodiversityapp.data.repository

import com.group9.biodiversityapp.api.LajiApiService
import com.group9.biodiversityapp.api.model.AggregateResult
import com.group9.biodiversityapp.api.model.AreaResponse
import com.group9.biodiversityapp.api.model.CountResponse
import com.group9.biodiversityapp.api.model.ObservationResponse
import com.group9.biodiversityapp.api.model.PagedResponse
import com.group9.biodiversityapp.data.local.dao.FavoriteDao
import com.group9.biodiversityapp.data.local.dao.ObservationDao
import com.group9.biodiversityapp.data.local.entity.FavoriteObservationEntity
import com.group9.biodiversityapp.data.local.entity.ObservationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository that mediates between the laji.fi Warehouse API and the local Room cache.
 *
 * - Network calls go through [apiService].
 * - Cached data is read/written through [observationDao].
 * - Favorites are managed through [favoriteDao].
 *
 * Usage from ViewModel:
 *
 *   val app = application as BiodiversityApp
 *   val repo = app.observationRepository
 *
 *   // US-29: Recent observations for a species
 *   val recent = repo.fetchRecentObservations("MX.37600")
 *
 *   // US-32: Total observation count for a species
 *   val count = repo.fetchObservationCount(taxonId = "MX.37600")
 *
 *   // US-34: Most observed species
 *   val top = repo.fetchMostObservedSpecies()
 */
class ObservationRepository(
    private val apiService: LajiApiService,
    private val observationDao: ObservationDao,
    private val favoriteDao: FavoriteDao
) {

    // ── Remote API calls ────────────────────────────────────────────────

    /** Fetch observations from the warehouse API and cache them locally. */
    suspend fun fetchObservations(
        page: Int = 1,
        pageSize: Int = 25,
        taxonId: String? = null,
        time: String? = null,
        area: String? = null,
        target: String? = null
    ): PagedResponse<ObservationResponse> {
        val response = apiService.getObservations(
            page = page,
            pageSize = pageSize,
            taxonId = taxonId,
            time = time,
            area = area,
            target = target
        )
        // Cache results
        val entities = response.results.mapNotNull { it.toEntity() }
        observationDao.insertAll(entities)
        return response
    }

    /**
     * US-29: Fetch the most recent observations of a specific species.
     * Sorted by date descending. Results are cached locally.
     */
    suspend fun fetchRecentObservations(
        taxonId: String,
        page: Int = 1,
        pageSize: Int = 25
    ): PagedResponse<ObservationResponse> {
        val response = apiService.getObservations(
            page = page,
            pageSize = pageSize,
            taxonId = taxonId,
            orderBy = "gathering.eventDate.begin DESC"
        )
        val entities = response.results.mapNotNull { it.toEntity() }
        observationDao.insertAll(entities)
        return response
    }

    /**
     * US-32: Get the total observation count for a species (or all species).
     * Pass taxonId to filter, or null for the global total.
     */
    suspend fun fetchObservationCount(
        taxonId: String? = null,
        time: String? = null,
        area: String? = null,
        target: String? = null
    ): CountResponse {
        return apiService.getObservationCount(
            taxonId = taxonId,
            time = time,
            area = area,
            target = target
        )
    }

    /**
     * US-34: Get the most observed species using the warehouse aggregate endpoint.
     * Returns species ranked by observation count.
     *
     * Each result has:
     * - aggregateBy["unit.linkings.taxon.id"] = taxon ID
     * - aggregateBy["unit.linkings.taxon.scientificName"] = scientific name
     * - count = number of observations
     * - individualCountSum = total individuals
     */
    suspend fun fetchMostObservedSpecies(
        page: Int = 1,
        pageSize: Int = 10,
        time: String? = null,
        area: String? = null
    ): PagedResponse<AggregateResult> {
        return apiService.getObservationAggregate(
            aggregateBy = "unit.linkings.taxon.speciesId,unit.linkings.taxon.speciesScientificName",
            page = page,
            pageSize = pageSize,
            taxonId = null,
            time = time,
            area = area
        )
    }

    /** Fetch areas from the API. */
    suspend fun fetchAreas(
        type: String? = null,
        lang: String = "en"
    ): PagedResponse<AreaResponse> {
        return apiService.getAreas(type = type, lang = lang)
    }

    // ── Local cache reads ───────────────────────────────────────────────

    /** Observe all cached observations. */
    fun getCachedObservations(): Flow<List<ObservationEntity>> = observationDao.getAll()

    /** Observe cached observations for a specific taxon. */
    fun getCachedObservationsByTaxon(taxonId: String): Flow<List<ObservationEntity>> =
        observationDao.getByTaxonId(taxonId)

    /** Search cached observations by location name. */
    fun searchCachedByLocation(location: String): Flow<List<ObservationEntity>> =
        observationDao.searchByLocation(location)

    /** Get a single cached observation by unit ID. */
    suspend fun getCachedObservationById(unitId: String): ObservationEntity? =
        observationDao.getById(unitId)

    /** Clear cache entries older than the given timestamp. */
    suspend fun clearOldCache(olderThanMillis: Long) {
        observationDao.deleteOlderThan(olderThanMillis)
    }

    // ── Favorites ───────────────────────────────────────────────────────

    fun getFavoriteObservations(): Flow<List<FavoriteObservationEntity>> =
        favoriteDao.getAllFavoriteObservations()

    fun isFavorite(unitId: String): Flow<Boolean> = favoriteDao.isObservationFavorite(unitId)

    suspend fun toggleFavorite(observation: ObservationResponse) {
        val unitId = observation.unit?.unitId ?: return
        val isFav = favoriteDao.isObservationFavoriteSync(unitId)
        if (isFav) {
            favoriteDao.removeFavoriteObservationById(unitId)
        } else {
            favoriteDao.addFavoriteObservation(
                FavoriteObservationEntity(
                    unitId = unitId,
                    taxonId = observation.unit.linkings?.taxon?.id,
                    scientificName = observation.unit.linkings?.taxon?.scientificName,
                    vernacularName = observation.unit.linkings?.taxon?.getVernacularName(),
                    municipality = observation.gathering?.municipality,
                    eventDate = observation.gathering?.eventDate?.begin,
                    latitude = observation.gathering?.coordinates?.lat,
                    longitude = observation.gathering?.coordinates?.lon
                )
            )
        }
    }

    suspend fun addFavorite(entity: FavoriteObservationEntity) =
        favoriteDao.addFavoriteObservation(entity)

    suspend fun removeFavorite(unitId: String) =
        favoriteDao.removeFavoriteObservationById(unitId)
}

// ── Mapping extension ───────────────────────────────────────────────────

private fun ObservationResponse.toEntity(): ObservationEntity? {
    val unitId = unit?.unitId ?: return null
    return ObservationEntity(
        unitId = unitId,
        taxonId = unit.linkings?.taxon?.id,
        scientificName = unit.linkings?.taxon?.scientificName,
        vernacularName = unit.linkings?.taxon?.getVernacularName(),
        taxonVerbatim = unit.taxonVerbatim,
        count = unit.count,
        eventDateBegin = gathering?.eventDate?.begin,
        eventDateEnd = gathering?.eventDate?.end,
        municipality = gathering?.municipality,
        latitude = gathering?.coordinates?.lat,
        longitude = gathering?.coordinates?.lon,
        documentId = document?.documentId,
        sourceId = document?.sourceId
    )
}
