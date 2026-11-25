package com.example.mind_set.data.repository

import java.security.MessageDigest

object PasswordCrypto {
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
