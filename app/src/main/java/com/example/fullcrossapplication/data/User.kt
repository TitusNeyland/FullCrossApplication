package com.example.fullcrossapplication.data

data class User(
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 