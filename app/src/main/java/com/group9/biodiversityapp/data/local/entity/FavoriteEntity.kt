package com.group9.biodiversityapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User's favorited taxon (species).
 */
@Entity(tableName = "favorite_taxa")
data class FavoriteTaxonEntity(
    @PrimaryKey val taxonId: String,
    val scientificName: String? = null,
    val vernacularName: String? = null,
    val taxonRank: String? = null,
    val thumbnailUrl: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * User's favorited observation.
 */
@Entity(tableName = "favorite_observations")
data class FavoriteObservationEntity(
    @PrimaryKey val unitId: String,
    val taxonId: String? = null,
    val scientificName: String? = null,
    val vernacularName: String? = null,
    val municipality: String? = null,
    val eventDate: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val addedAt: Long = System.currentTimeMillis()
)
