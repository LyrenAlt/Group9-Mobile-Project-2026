package com.group9.biodiversityapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local user account stored in Room.
 *
 * US-01: User Registration
 * US-02: User Login
 * US-05: Password Reset
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val passwordSalt: String,
    val preferredLanguage: String = "en",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
