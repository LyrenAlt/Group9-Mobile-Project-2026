package com.group9.biodiversityapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.group9.biodiversityapp.data.local.dao.FavoriteDao
import com.group9.biodiversityapp.data.local.dao.ObservationDao
import com.group9.biodiversityapp.data.local.dao.TaxonDao
import com.group9.biodiversityapp.data.local.dao.UserDao
import com.group9.biodiversityapp.data.local.entity.FavoriteObservationEntity
import com.group9.biodiversityapp.data.local.entity.FavoriteTaxonEntity
import com.group9.biodiversityapp.data.local.entity.ObservationEntity
import com.group9.biodiversityapp.data.local.entity.TaxonEntity
import com.group9.biodiversityapp.data.local.entity.UserEntity

@Database(
    entities = [
        TaxonEntity::class,
        ObservationEntity::class,
        FavoriteTaxonEntity::class,
        FavoriteObservationEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BiodiversityDatabase : RoomDatabase() {

    abstract fun taxonDao(): TaxonDao
    abstract fun observationDao(): ObservationDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: BiodiversityDatabase? = null

        fun getInstance(context: Context): BiodiversityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BiodiversityDatabase::class.java,
                    "biodiversity_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
