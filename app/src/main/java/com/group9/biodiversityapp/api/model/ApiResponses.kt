package com.group9.biodiversityapp.api.model

import com.google.gson.annotations.SerializedName

// ── Pagination ──────────────────────────────────────────────────────────

/** Generic paginated response wrapper used by most laji.fi endpoints. */
data class PagedResponse<T>(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("total") val total: Long,
    @SerializedName("lastPage") val lastPage: Int,
    @SerializedName("results") val results: List<T>
)

// ── Taxa / Species ──────────────────────────────────────────────────────

/** Taxon response from /taxa and /taxa/{id} endpoints. */
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
    @SerializedName("multimedia") val multimedia: List<MultimediaItem>? = null,
    @SerializedName("descriptions") val descriptions: List<DescriptionGroup>? = null,
    @SerializedName("redListEvaluations") val redListEvaluations: List<RedListEvaluation>? = null,
    @SerializedName("typeOfOccurrenceInFinland") val typeOfOccurrenceInFinland: String? = null,
    @SerializedName("habitat") val habitat: List<String>? = null,
    @SerializedName("primaryHabitat") val primaryHabitat: HabitatInfo? = null,
    @SerializedName("latestRedListStatusFinland") val latestRedListStatusFinland: RedListStatus? = null,
    @SerializedName("administrativeStatuses") val administrativeStatuses: List<String>? = null,
    @SerializedName("taxonSets") val taxonSets: List<String>? = null,
    @SerializedName("stableString") val stableString: String? = null
) {
    /** First image URL (thumbnail) if available — handy for grid views. */
    val thumbnailUrl: String?
        get() = multimedia?.firstOrNull()?.let {
            it.squareThumbnailURL ?: it.thumbnailURL
        }

    /** First image URL (full size) if available — handy for detail views. */
    val fullImageUrl: String?
        get() = multimedia?.firstOrNull()?.fullURL
}

data class MultimediaItem(
    @SerializedName("thumbnailURL") val thumbnailURL: String? = null,
    @SerializedName("squareThumbnailURL") val squareThumbnailURL: String? = null,
    @SerializedName("fullURL") val fullURL: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("licenseId") val licenseId: String? = null,
    @SerializedName("mediaType") val mediaType: String? = null,
    @SerializedName("copyrightOwner") val copyrightOwner: String? = null,
    @SerializedName("caption") val caption: String? = null
)

data class DescriptionGroup(
    @SerializedName("title") val title: String? = null,
    @SerializedName("groups") val groups: List<DescriptionVariable>? = null
)

data class DescriptionVariable(
    @SerializedName("variable") val variable: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null
)

data class RedListEvaluation(
    @SerializedName("status") val status: String? = null,
    @SerializedName("year") val year: Int? = null,
    @SerializedName("criteria") val criteria: String? = null,
    @SerializedName("threatenedAtArea") val threatenedAtArea: String? = null
)

data class RedListStatus(
    @SerializedName("status") val status: String? = null,
    @SerializedName("year") val year: Int? = null
)

data class HabitatInfo(
    @SerializedName("habitat") val habitat: String? = null,
    @SerializedName("habitatSpecificType") val habitatSpecificType: String? = null
)

// ── Images ──────────────────────────────────────────────────────────────

/** Image metadata from /images/{id}. */
data class ImageResponse(
    @SerializedName("id") val id: String,
    @SerializedName("fullURL") val fullURL: String? = null,
    @SerializedName("thumbnailURL") val thumbnailURL: String? = null,
    @SerializedName("squareThumbnailURL") val squareThumbnailURL: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("licenseId") val licenseId: String? = null,
    @SerializedName("copyrightOwner") val copyrightOwner: String? = null,
    @SerializedName("caption") val caption: String? = null,
    @SerializedName("mediaType") val mediaType: String? = null,
    @SerializedName("uploadedBy") val uploadedBy: String? = null,
    @SerializedName("intellectualRights") val intellectualRights: String? = null
)

// ── Observations / Warehouse ────────────────────────────────────────────

/** Observation/unit response from warehouse endpoints. */
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
    val municipality: String? get() = interpretations?.municipalityDisplayname
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

/** Count response from warehouse count endpoints. */
data class CountResponse(
    @SerializedName("total") val total: Long
)

/** Aggregate result from warehouse aggregate endpoints. */
data class AggregateResult(
    @SerializedName("count") val count: Long? = null,
    @SerializedName("individualCountSum") val individualCountSum: Long? = null,
    @SerializedName("aggregateBy") val aggregateBy: Map<String, String>? = null
)

// ── Areas ────────────────────────────────────────────────────────────────

/** Area from /areas endpoint. */
data class AreaResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("areaType") val areaType: String? = null
)

// ── Collections ─────────────────────────────────────────────────────────

/** Collection/data source from /collections endpoint. */
data class CollectionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("longName") val longName: String? = null,
    @SerializedName("abbreviation") val abbreviation: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("collectionType") val collectionType: String? = null,
    @SerializedName("taxonomicCoverage") val taxonomicCoverage: String? = null,
    @SerializedName("geographicCoverage") val geographicCoverage: String? = null,
    @SerializedName("count") val count: Long? = null,
    @SerializedName("shareToGbif") val shareToGbif: Boolean? = null
)

// ── Autocomplete ────────────────────────────────────────────────────────

/** Autocomplete result from /autocomplete/taxon. */
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

// ── Informal Taxon Groups ───────────────────────────────────────────────

/** Informal taxon group from /informal-taxon-groups. */
data class InformalTaxonGroupResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("hasSubGroup") val hasSubGroup: List<String>? = null
)

// ── News ─────────────────────────────────────────────────────────────────

/** News/announcement from /news. */
data class NewsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("posted") val posted: String? = null,
    @SerializedName("tag") val tag: List<String>? = null,
    @SerializedName("externalURL") val externalURL: String? = null
)
