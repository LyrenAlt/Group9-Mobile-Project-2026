package com.group9.biodiversityapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached taxon/species data fetched from the laji.fi API.
 */
@Entity(tableName = "cached_taxa")
data class TaxonEntity(
    @PrimaryKey val id: String,
    val scientificName: String? = null,
    val vernacularName: String? = null,
    val taxonRank: String? = null,
    val finnish: Boolean? = null,
    val informalTaxonGroups: List<String>? = null,
    val parentId: String? = null,
    val scientificNameAuthorship: String? = null,
    val thumbnailUrl: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)
