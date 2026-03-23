package com.group9.biodiversityapp.api.model

import com.google.gson.annotations.SerializedName

/**
 * Generic paginated response wrapper used by most laji.fi endpoints.
 */
data class PagedResponse<T>(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("total") val total: Long,
    @SerializedName("lastPage") val lastPage: Int,
    @SerializedName("results") val results: List<T>
)

/**
 * Taxon / Species response from /v0/taxa and /v0/species endpoints.
 */
data class TaxonResponse(
    @SerializedName("id") val id: String,
    @SerializedName("scientificName") val scientificName: String? = null,
    @SerializedName("vernacularName") val vernacularName: String? = null,
    @SerializedName("taxonRank") val taxonRank: String? = null,
    @SerializedName("cursiveName") val cursiveName: Boolean? = null,
    @SerializedName("finnish") val finnish: Boolean? = null,
    @SerializedName("species") val species: Boolean? = null,
    @SerializedName("informalTaxonGroups") val informalTaxonGroups: List<String>? = null,
    @SerializedName("parent") val parent: String? = null,
    @SerializedName("scientificNameAuthorship") val scientificNameAuthorship: String? = null,
    @SerializedName("hasMultimedia") val hasMultimedia: Boolean? = null,
    @SerializedName("multimedia") val multimedia: List<MultimediaItem>? = null
)

data class MultimediaItem(
    @SerializedName("thumbnailURL") val thumbnailURL: String? = null,
    @SerializedName("squareThumbnailURL") val squareThumbnailURL: String? = null,
    @SerializedName("fullURL") val fullURL: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("licenseId") val licenseId: String? = null
)

/**
 * Observation/unit response from /v0/warehouse/query/unit/list endpoint.
 */
data class ObservationResponse(
    @SerializedName("unit") val unit: ObservationUnit? = null,
    @SerializedName("gathering") val gathering: ObservationGathering? = null,
    @SerializedName("document") val document: ObservationDocument? = null
)

data class ObservationUnit(
    @SerializedName("unitId") val unitId: String? = null,
    @SerializedName("taxonVerbatim") val taxonVerbatim: String? = null,
    @SerializedName("count") val count: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("recordBasis") val recordBasis: String? = null,
    @SerializedName("sex") val sex: String? = null,
    @SerializedName("lifeStage") val lifeStage: String? = null,
    @SerializedName("media") val media: List<MediaItem>? = null,
    @SerializedName("linkings") val linkings: UnitLinkings? = null
)

data class UnitLinkings(
    @SerializedName("taxon") val taxon: LinkedTaxon? = null
)

data class LinkedTaxon(
    @SerializedName("id") val id: String? = null,
    @SerializedName("scientificName") val scientificName: String? = null,
    @SerializedName("vernacularName") val vernacularName: Map<String, String>? = null,
    @SerializedName("taxonRank") val taxonRank: String? = null,
    @SerializedName("informalTaxonGroups") val informalTaxonGroups: List<String>? = null
) {
    /** Get the vernacular name for a given language, falling back to Finnish then first available. */
    fun getVernacularName(lang: String = "fi"): String? =
        vernacularName?.get(lang) ?: vernacularName?.get("fi") ?: vernacularName?.values?.firstOrNull()
}

data class MediaItem(
    @SerializedName("thumbnailURL") val thumbnailURL: String? = null,
    @SerializedName("fullURL") val fullURL: String? = null
)

data class ObservationGathering(
    @SerializedName("gatheringId") val gatheringId: String? = null,
    @SerializedName("displayDateTime") val displayDateTime: String? = null,
    @SerializedName("eventDate") val eventDate: EventDate? = null,
    @SerializedName("locality") val locality: String? = null,
    @SerializedName("team") val team: List<String>? = null,
    @SerializedName("conversions") val conversions: GatheringConversions? = null,
    @SerializedName("interpretations") val interpretations: GatheringInterpretations? = null
) {
    /** Convenience accessor for municipality from interpretations. */
    val municipality: String? get() = interpretations?.municipalityDisplayname

    /** Convenience accessor for WGS84 coordinates from conversions. */
    val coordinates: Coordinates? get() = conversions?.wgs84CenterPoint
}

data class GatheringConversions(
    @SerializedName("wgs84CenterPoint") val wgs84CenterPoint: Coordinates? = null
)

data class GatheringInterpretations(
    @SerializedName("municipalityDisplayname") val municipalityDisplayname: String? = null,
    @SerializedName("coordinateAccuracy") val coordinateAccuracy: Int? = null,
    @SerializedName("sourceOfCoordinates") val sourceOfCoordinates: String? = null
)

data class EventDate(
    @SerializedName("begin") val begin: String? = null,
    @SerializedName("end") val end: String? = null
)

data class Coordinates(
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lon") val lon: Double? = null
)

data class ObservationDocument(
    @SerializedName("documentId") val documentId: String? = null,
    @SerializedName("sourceId") val sourceId: String? = null
)

/**
 * Count response from /v0/warehouse/query/unit/count.
 */
data class CountResponse(
    @SerializedName("total") val total: Long
)

/**
 * Area response from /v0/areas endpoint.
 */
data class AreaResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("areaType") val areaType: String? = null
)

/**
 * Autocomplete result from /v0/autocomplete/taxon.
 */
data class AutocompleteResult(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String,
    @SerializedName("payload") val payload: AutocompletePayload? = null
)

data class AutocompletePayload(
    @SerializedName("scientificName") val scientificName: String? = null,
    @SerializedName("vernacularName") val vernacularName: String? = null,
    @SerializedName("taxonRank") val taxonRank: String? = null,
    @SerializedName("informalTaxonGroups") val informalTaxonGroups: List<String>? = null,
    @SerializedName("cursiveName") val cursiveName: Boolean? = null
)

/**
 * Informal taxon group from /v0/informal-taxon-groups.
 */
data class InformalTaxonGroupResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("hasSubGroup") val hasSubGroup: List<String>? = null
)
