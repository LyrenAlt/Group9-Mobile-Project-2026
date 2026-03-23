package com.group9.biodiversityapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached observation/unit data fetched from the laji.fi warehouse API.
 */
@Entity(tableName = "cached_observations")
data class ObservationEntity(
    @PrimaryKey val unitId: String,
    val taxonId: String? = null,
    val scientificName: String? = null,
    val vernacularName: String? = null,
    val taxonVerbatim: String? = null,
    val count: String? = null,
    val eventDateBegin: String? = null,
    val eventDateEnd: String? = null,
    val municipality: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val documentId: String? = null,
    val sourceId: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)
