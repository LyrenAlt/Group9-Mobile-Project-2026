package com.group9.biodiversityapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group9.biodiversityapp.data.local.entity.ObservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(observations: List<ObservationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(observation: ObservationEntity)

    @Query("SELECT * FROM cached_observations WHERE unitId = :unitId")
    suspend fun getById(unitId: String): ObservationEntity?

    @Query("SELECT * FROM cached_observations ORDER BY cachedAt DESC")
    fun getAll(): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM cached_observations WHERE taxonId = :taxonId ORDER BY eventDateBegin DESC")
    fun getByTaxonId(taxonId: String): Flow<List<ObservationEntity>>

    @Query(
        "SELECT * FROM cached_observations WHERE " +
        "municipality LIKE '%' || :location || '%' " +
        "ORDER BY cachedAt DESC"
    )
    fun searchByLocation(location: String): Flow<List<ObservationEntity>>

    @Query("DELETE FROM cached_observations WHERE cachedAt < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)

    @Query("DELETE FROM cached_observations")
    suspend fun deleteAll()
}
