package com.group9.biodiversityapp.api

import com.group9.biodiversityapp.api.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for the Finnish Biodiversity Information Facility API. cool
 * Base URL: https://api.laji.fi/v0/
 *
 * The access_token query parameter is automatically appended by the AuthInterceptor.
 */
interface LajiApiService {

    // ── Taxa ────────────────────────────────────────────────────────────

    /** Search/list taxa with optional filters. */
    @GET("taxa")
    suspend fun getTaxa(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("q") query: String? = null,
        @Query("informalTaxonGroup") informalTaxonGroup: String? = null,
        @Query("taxonRank") taxonRank: String? = null,
        @Query("lang") lang: String = "en",
        @Query("langFallback") langFallback: Boolean = true,
        @Query("selectedFields") selectedFields: String? = null
    ): PagedResponse<TaxonResponse>

    /** Get a specific taxon by ID (e.g. "MX.37600"). */
    @GET("taxa/{id}")
    suspend fun getTaxonById(
        @Path("id") id: String,
        @Query("lang") lang: String = "en",
        @Query("langFallback") langFallback: Boolean = true
    ): TaxonResponse

    // ── Species (uses /taxa with species-level taxonRank filter) ────────

    /** List species-level taxa. Wraps /taxa with taxonRank=MX.species. */
    @GET("taxa")
    suspend fun getSpecies(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("q") query: String? = null,
        @Query("informalTaxonGroup") informalTaxonGroup: String? = null,
        @Query("taxonRank") taxonRank: String? = "MX.species",
        @Query("lang") lang: String = "en",
        @Query("langFallback") langFallback: Boolean = true
    ): PagedResponse<TaxonResponse>

    // ── Warehouse / Observations ────────────────────────────────────────

    /** Query observation units (occurrences) with filters. */
    @GET("warehouse/query/unit/list")
    suspend fun getObservations(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("taxonId") taxonId: String? = null,
        @Query("time") time: String? = null,
        @Query("area") area: String? = null,
        @Query("target") target: String? = null,
        @Query("selected") selected: String? = null,
        @Query("orderBy") orderBy: String? = null,
        @Query("cache") cache: Boolean = true
    ): PagedResponse<ObservationResponse>

    /** Get count of observation units matching the given filters. */
    @GET("warehouse/query/unit/count")
    suspend fun getObservationCount(
        @Query("taxonId") taxonId: String? = null,
        @Query("time") time: String? = null,
        @Query("area") area: String? = null,
        @Query("target") target: String? = null,
        @Query("cache") cache: Boolean = true
    ): CountResponse

    // ── Areas ───────────────────────────────────────────────────────────

    /** List areas (municipalities, provinces, etc.). */
    @GET("areas")
    suspend fun getAreas(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 100,
        @Query("type") type: String? = null,
        @Query("lang") lang: String = "en"
    ): PagedResponse<AreaResponse>

    // ── Autocomplete ────────────────────────────────────────────────────

    /** Autocomplete taxon names. */
    @GET("autocomplete/taxon")
    suspend fun autocompleteTaxon(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10,
        @Query("lang") lang: String = "en",
        @Query("includePayload") includePayload: Boolean = true
    ): List<AutocompleteResult>

    // ── Informal Taxon Groups ───────────────────────────────────────────

    /** List informal taxon groups (e.g. Birds, Mammals, Plants). */
    @GET("informal-taxon-groups")
    suspend fun getInformalTaxonGroups(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 100,
        @Query("lang") lang: String = "en"
    ): PagedResponse<InformalTaxonGroupResponse>
}
