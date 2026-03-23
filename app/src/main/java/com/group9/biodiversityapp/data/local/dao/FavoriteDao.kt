package com.group9.biodiversityapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group9.biodiversityapp.data.local.entity.FavoriteObservationEntity
import com.group9.biodiversityapp.data.local.entity.FavoriteTaxonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    // ── Favorite Taxa ───────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteTaxon(taxon: FavoriteTaxonEntity)

    @Delete
    suspend fun removeFavoriteTaxon(taxon: FavoriteTaxonEntity)

    @Query("DELETE FROM favorite_taxa WHERE taxonId = :taxonId")
    suspend fun removeFavoriteTaxonById(taxonId: String)

    @Query("SELECT * FROM favorite_taxa ORDER BY addedAt DESC")
    fun getAllFavoriteTaxa(): Flow<List<FavoriteTaxonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_taxa WHERE taxonId = :taxonId)")
    fun isTaxonFavorite(taxonId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_taxa WHERE taxonId = :taxonId)")
    suspend fun isTaxonFavoriteSync(taxonId: String): Boolean

    // ── Favorite Observations ───────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteObservation(observation: FavoriteObservationEntity)

    @Delete
    suspend fun removeFavoriteObservation(observation: FavoriteObservationEntity)

    @Query("DELETE FROM favorite_observations WHERE unitId = :unitId")
    suspend fun removeFavoriteObservationById(unitId: String)

    @Query("SELECT * FROM favorite_observations ORDER BY addedAt DESC")
    fun getAllFavoriteObservations(): Flow<List<FavoriteObservationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_observations WHERE unitId = :unitId)")
    fun isObservationFavorite(unitId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_observations WHERE unitId = :unitId)")
    suspend fun isObservationFavoriteSync(unitId: String): Boolean
}
