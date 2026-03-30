package com.group9.biodiversityapp.data.repository

import com.group9.biodiversityapp.data.local.dao.UserDao
import com.group9.biodiversityapp.data.local.entity.UserEntity
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Handles user registration, login, and password reset.
 *
 * US-01: register()
 * US-02: login()
 * US-05: resetPassword()
 *
 * Usage from ViewModel:
 *
 *   val app = application as BiodiversityApp
 *   val auth = app.authRepository
 *
 *   // Register
 *   val result = auth.register("user@email.com", "John", "password123")
 *
 *   // Login
 *   val result = auth.login("user@email.com", "password123")
 *
 *   // Reset password
 *   val result = auth.resetPassword("user@email.com", "newpassword123")
 */
class AuthRepository(
    private val userDao: UserDao
) {

    // ── US-01: User Registration ─────────────────────────────────────

    /**
     * Register a new user. Returns the created user on success, or an error message.
     *
     * Validates:
     * - Email is not blank and contains @
     * - Display name is not blank
     * - Password is at least 6 characters
     * - Email is not already taken
     */
    suspend fun register(email: String, displayName: String, password: String): AuthResult {
        // Validate input
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = displayName.trim()

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return AuthResult.Error("Invalid email address")
        }
        if (trimmedName.isBlank()) {
            return AuthResult.Error("Display name is required")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters")
        }
        if (userDao.emailExists(trimmedEmail)) {
            return AuthResult.Error("An account with this email already exists")
        }

        // Hash password
        val salt = generateSalt()
        val hash = hashPassword(password, salt)

        val user = UserEntity(
            email = trimmedEmail,
            displayName = trimmedName,
            passwordHash = hash,
            passwordSalt = salt
        )

        return try {
            val id = userDao.insert(user)
            val created = user.copy(id = id)
            AuthResult.Success(created)
        } catch (e: Exception) {
            AuthResult.Error("Registration failed: ${e.message}")
        }
    }

    // ── US-02: User Login ────────────────────────────────────────────

    /**
     * Authenticate a user by email and password.
     * Returns the user on success, or an error message.
     */
    suspend fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()

        if (trimmedEmail.isBlank()) {
            return AuthResult.Error("Email is required")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Password is required")
        }

        val user = userDao.getByEmail(trimmedEmail)
            ?: return AuthResult.Error("No account found with this email")

        val hash = hashPassword(password, user.passwordSalt)
        if (hash != user.passwordHash) {
            return AuthResult.Error("Incorrect password")
        }

        return AuthResult.Success(user)
    }

    // ── US-05: Password Reset ────────────────────────────────────────

    /**
     * Reset a user's password. Requires the email of an existing account.
     * In a real app this would send a reset email — here we do it directly
     * since there's no email server.
     */
    suspend fun resetPassword(email: String, newPassword: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()

        if (newPassword.length < 6) {
            return AuthResult.Error("New password must be at least 6 characters")
        }

        val user = userDao.getByEmail(trimmedEmail)
            ?: return AuthResult.Error("No account found with this email")

        val newSalt = generateSalt()
        val newHash = hashPassword(newPassword, newSalt)

        userDao.updatePassword(
            email = trimmedEmail,
            passwordHash = newHash,
            salt = newSalt
        )

        // Return the updated user
        val updated = userDao.getByEmail(trimmedEmail)
        return if (updated != null) {
            AuthResult.Success(updated)
        } else {
            AuthResult.Error("Password reset failed")
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /** Get user by ID (for session restoration). */
    suspend fun getUserById(id: Long): UserEntity? = userDao.getById(id)

    /** Get user by email. */
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getByEmail(email.trim().lowercase())

    /** Update user's preferred language (US-42). */
    suspend fun updateLanguage(userId: Long, lang: String) {
        userDao.updateLanguage(userId, lang)
    }

    /** Update display name. */
    suspend fun updateDisplayName(userId: Long, name: String) {
        userDao.updateDisplayName(userId, name.trim())
    }

    // ── Password hashing ─────────────────────────────────────────────

    private fun generateSalt(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hashPassword(password: String, salt: String): String {
        val input = "$salt:$password"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

/** Result wrapper for auth operations. */
sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
