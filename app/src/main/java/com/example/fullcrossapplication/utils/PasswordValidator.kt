package com.example.fullcrossapplication.utils

object PasswordValidator {
    fun validatePassword(password: String): Boolean {
        val hasNumber = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val hasMinLength = password.length >= 6
        val hasNoWhitespace = !password.contains(Regex("\\s"))

        return hasNumber && hasSpecialChar && hasMinLength && hasNoWhitespace
    }

    fun getPasswordRequirementsMessage(): String {
        return "Password must be at least 6 characters long, contain at least one number and one special character, and cannot contain spaces"
    }
} 