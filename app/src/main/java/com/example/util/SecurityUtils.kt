package com.example.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtils {

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(Base64.decode(salt, Base64.NO_WRAP))
        val hashedBytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hashedBytes, Base64.NO_WRAP)
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val calculated = hashPassword(password, salt)
        return calculated == expectedHash
    }

    fun calculatePasswordStrength(password: String): PasswordStrength {
        if (password.length < 4) return PasswordStrength.TOO_SHORT
        var score = 0
        if (password.length >= 6) score++
        if (password.length >= 8) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return when {
            score <= 1 -> PasswordStrength.WEAK
            score in 2..3 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.STRONG
        }
    }
}

enum class PasswordStrength(val label: String) {
    TOO_SHORT("Too short (min 4 chars)"),
    WEAK("Weak"),
    MEDIUM("Medium"),
    STRONG("Strong")
}
