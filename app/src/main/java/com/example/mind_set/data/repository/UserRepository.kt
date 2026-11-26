package com.example.mind_set.data.repository

import com.example.mind_set.data.local.dao.UserDao
import com.example.mind_set.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserRepository {
    suspend fun register(username: String, pass: String): Boolean
    suspend fun login(username: String, pass: String): Boolean
    suspend fun changePassword(username: String, oldPass: String, newPass: String): Boolean
}

class DefaultUserRepository(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun register(username: String, pass: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val cleanUsername = username.trim().lowercase()
            if (cleanUsername.isBlank() || pass.isBlank()) return@withContext false

            val existing = userDao.getUserByUsername(cleanUsername)
            if (existing != null) {
                return@withContext false
            }

            val hash = PasswordCrypto.hashPassword(pass)
            userDao.insertUser(UserEntity(username = cleanUsername, passwordHash = hash))
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun login(username: String, pass: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val cleanUsername = username.trim().lowercase()
            if (cleanUsername.isBlank() || pass.isBlank()) return@withContext false

            val user = userDao.getUserByUsername(cleanUsername) ?: return@withContext false
            val hash = PasswordCrypto.hashPassword(pass)
            user.passwordHash == hash
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun changePassword(username: String, oldPass: String, newPass: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val cleanUsername = username.trim().lowercase()
            if (cleanUsername.isBlank() || newPass.isBlank()) return@withContext false
            val user = userDao.getUserByUsername(cleanUsername) ?: return@withContext false
            val oldHash = PasswordCrypto.hashPassword(oldPass)
            if (user.passwordHash != oldHash) return@withContext false

            val newHash = PasswordCrypto.hashPassword(newPass)
            userDao.updatePassword(cleanUsername, newHash)
            true
        } catch (e: Exception) {
            false
        }
    }
}
