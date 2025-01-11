package com.example.fullcrossapplication.data

data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
) {
    val fullName: String
        get() = "$firstName $lastName"
} 