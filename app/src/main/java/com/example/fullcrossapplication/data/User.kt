package com.example.fullcrossapplication.data

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 