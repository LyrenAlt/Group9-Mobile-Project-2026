package com.group9.biodiversityapp.api

import com.group9.biodiversityapp.api.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for the Finnish Biodiversity Information Facility API.
 * Base URL: https://api.laji.fi/
 *
 * The access_token query parameter is automatically appended by the AuthInterceptor.
 *
 * Every endpoint is ready for ViewModels to use directly — just call the method
 * with the params you need and sensible defaults handle the rest.
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
        @Query("includeMedia") includeMedia: Boolean = true,
        @Query("selectedFields") selectedFields: String? = null
    ): PagedResponse<TaxonResponse>

    /** Get a specific taxon by ID (e.g. "MX.37600"). */
    @GET("taxa/{id}")
    suspend fun getTaxonById(
        @Path("id") id: String,
        @Query("lang") lang: String = "en",
        @Query("langFallback") langFallback: Boolean = true,
        @Query("includeMedia") includeMedia: Boolean = true,
        @Query("includeDescriptions") includeDescriptions: Boolean = true,
        @Query("includeRedListEvaluations") includeRedListEvaluations: Boolean = true
    ): TaxonResponse

    /** Get child taxa of a given taxon (e.g. all species under a genus). */
    @GET("taxa/{id}/children")
    suspend fun getTaxonChildren(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("lang") lang: String = "en",
        @Query("langFallback") langFallback: Boolean = true,
        @Query("includeMedia") includeMedia: Boolean = true
    ): PagedResponse<TaxonResponse>

    /** Get the parent chain of a taxon (kingdom → phylum → ... → species). */
    @GET("taxa/{id}/parents")
    suspend fun getTaxonParents(
        @Path("id") id: String,
        @Query("lang") lang: String = "en",
        @Query("langFallback") langFallback: Boolean = true
    ): List<TaxonResponse>

    /** Get all species under a higher-level taxon. */
    @GET("taxa/{id}/species")
    suspend fun getTaxonSpecies(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("lang") lang: String = "en",
        @Query("langFallback") langFallback: Boolean = true,
        @Query("includeMedia") includeMedia: Boolean = true
    ): PagedResponse<TaxonResponse>

    // ── Species (uses /taxa with species-level defaults) ─────────────

    /**
     * List Finnish species with images — matches the laji.fi species browser.
     * Uses checklist=MR.1 (Finnish checklist), finnish=true, includeMedia=true,
     * and sortOrder=taxonomic to match the website's default view.
     */
    @GET("taxa")
    suspend fun getSpecies(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("q") query: String? = null,
        @Query("informalTaxonGroup") informalTaxonGroup: String? = null,
        @Query("taxonRank") taxonRank: String? = "MX.species",
        @Query("checklist") checklist: String = "MR.1",
        @Query("checklistVersion") checklistVersion: String = "current",
        @Query("finnish") finnish: Boolean = true,
        @Query("lang") lang: String = "en",
        @Query("langFallback") langFallback: Boolean = true,
        @Query("includeMedia") includeMedia: Boolean = true,
        @Query("includeDescriptions") includeDescriptions: Boolean = true,
        @Query("includeRedListEvaluations") includeRedListEvaluations: Boolean = true,
        @Query("sortOrder") sortOrder: String = "taxonomic"
    ): PagedResponse<TaxonResponse>

    // ── Images ───────────────────────────────────────────────────────────

    /** Get image metadata by ID (e.g. "MM.12345"). */
    @GET("images/{id}")
    suspend fun getImageById(
        @Path("id") id: String
    ): ImageResponse

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

    /** Query gatherings (collection events). */
    @GET("warehouse/query/gathering/list")
    suspend fun getGatherings(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("taxonId") taxonId: String? = null,
        @Query("time") time: String? = null,
        @Query("area") area: String? = null,
        @Query("cache") cache: Boolean = true
    ): PagedResponse<ObservationResponse>

    /** Aggregate observation data (e.g. species counts per area/month). */
    @GET("warehouse/query/unit/aggregate")
    suspend fun getObservationAggregate(
        @Query("aggregateBy") aggregateBy: String,
        @Query("taxonId") taxonId: String? = null,
        @Query("time") time: String? = null,
        @Query("area") area: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("cache") cache: Boolean = true
    ): PagedResponse<AggregateResult>

    // ── Areas ───────────────────────────────────────────────────────────

    /** List areas (municipalities, provinces, countries, etc.). */
    @GET("areas")
    suspend fun getAreas(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 100,
        @Query("type") type: String? = null,
        @Query("lang") lang: String = "en"
    ): PagedResponse<AreaResponse>

    /** Get a specific area by ID (e.g. "ML.206"). */
    @GET("areas/{id}")
    suspend fun getAreaById(
        @Path("id") id: String,
        @Query("lang") lang: String = "en"
    ): AreaResponse

    // ── Collections ─────────────────────────────────────────────────────

    /** List data collections/sources. */
    @GET("collections")
    suspend fun getCollections(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("lang") lang: String = "en"
    ): PagedResponse<CollectionResponse>

    /** Get a specific collection by ID. */
    @GET("collections/{id}")
    suspend fun getCollectionById(
        @Path("id") id: String,
        @Query("lang") lang: String = "en"
    ): CollectionResponse

    // ── Autocomplete ────────────────────────────────────────────────────

    /** Autocomplete taxon names — for search bars. */
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

    /** Get a specific informal taxon group by ID (e.g. "MVL.1" for Birds). */
    @GET("informal-taxon-groups/{id}")
    suspend fun getInformalTaxonGroupById(
        @Path("id") id: String,
        @Query("lang") lang: String = "en"
    ): InformalTaxonGroupResponse

    // ── News ────────────────────────────────────────────────────────────

    /** Get news/announcements from laji.fi. */
    @GET("news")
    suspend fun getNews(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25,
        @Query("tag") tag: String? = null,
        @Query("lang") lang: String = "en"
    ): PagedResponse<NewsResponse>
}
