package com.group9.biodiversityapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.group9.biodiversityapp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user authentication operations.
 *
 * US-01: register (insert)
 * US-02: login (getByEmail + password check in repository)
 * US-05: password reset (updatePassword)
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun emailExists(email: String): Boolean

    @Query("UPDATE users SET passwordHash = :passwordHash, passwordSalt = :salt, updatedAt = :updatedAt WHERE email = :email")
    suspend fun updatePassword(email: String, passwordHash: String, salt: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE users SET preferredLanguage = :lang, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateLanguage(userId: Long, lang: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE users SET displayName = :name, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateDisplayName(userId: Long, name: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Long)
}
