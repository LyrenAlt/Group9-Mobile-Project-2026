package com.group9.biodiversityapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group9.biodiversityapp.data.local.entity.TaxonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(taxa: List<TaxonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taxon: TaxonEntity)

    @Query("SELECT * FROM cached_taxa WHERE id = :id")
    suspend fun getById(id: String): TaxonEntity?

    @Query("SELECT * FROM cached_taxa ORDER BY cachedAt DESC")
    fun getAll(): Flow<List<TaxonEntity>>

    @Query("SELECT * FROM cached_taxa ORDER BY cachedAt DESC")
    suspend fun getAllSync(): List<TaxonEntity>

    @Query(
        "SELECT * FROM cached_taxa WHERE " +
        "scientificName LIKE '%' || :query || '%' OR " +
        "vernacularName LIKE '%' || :query || '%' " +
        "ORDER BY cachedAt DESC"
    )
    fun search(query: String): Flow<List<TaxonEntity>>

    @Query("DELETE FROM cached_taxa WHERE cachedAt < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)

    @Query("DELETE FROM cached_taxa")
    suspend fun deleteAll()
}
